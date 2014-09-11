/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.https://www.youtube.com/watch?v=ICF-RldvSIo
 */
package Capa_Presentacion;

import Capa_Datos.AccesoDatos;
import static Capa_Negocio.AddForms.adminInternalFrame;
import Capa_Negocio.FiltroCampos;
import Capa_Negocio.FormatoDecimal;
import Capa_Negocio.FormatoFecha;
import Capa_Negocio.Peticiones;
import Capa_Negocio.TipoFiltro;
import Capa_Negocio.Utilidades;
import static Capa_Presentacion.Principal.dp;
import Recursos.mGrupo;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author GLARA
 */
public class Alumno extends javax.swing.JInternalFrame {

    private static Horario frmHorario = new Horario();
    /*El modelo se define en : Jtable-->propiedades-->model--> <User Code> */
    DefaultTableModel model;
    DefaultComboBoxModel modelCombo;
    String[] titulos = {"codigo", "Nombres", "Apellidos", "Fecha Nec", "Colegiatura", "Beca", "Fecha Inicio", "Estado"};//Titulos para Jtabla
    /*Se hace una instancia de la clase que recibira las peticiones de esta capa de aplicación*/
    Peticiones peticiones = new Peticiones();
    public Hashtable<String, String> hashGrupo = new Hashtable<>();

    /*Se hace una instancia de la clase que recibira las peticiones de mensages de la capa de aplicación*/
    //public static JOptionMessage msg = new JOptionMessage();
    /**
     * Creates new form Cliente
     */
    public Alumno() {
        initComponents();
        setFiltroTexto();
        addEscapeKey();
        llenarcombo();
        grupo.addItemListener(
                (ItemEvent e) -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        profesor();
                    }
                });
    }

    /*addEscapeKey agrega a este JInternalFrame un evento de cerrarVentana() al presionar la tecla "ESC" */
    private void addEscapeKey() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarVentana();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    /*Este metodo visualiza una mensage de cinfirmación al usuario antes de Cerrar la ventana,
     * si por eror se intento cerrar el formulario devera indicar que "NO" para no perder los datos
     * que no haya Guardado de lo contrario presiona "SI" y se cerrara la ventana sin Guardar ningun dato. */
    private void cerrarVentana() {
        int nu = JOptionPane.showInternalConfirmDialog(this, "Todos los datos que no se ha guardadox "
                + "se perderan.\n"
                + "¿Desea Cerrar esta ventana?", "Cerrar ventana", JOptionPane.YES_NO_OPTION);
        if (nu == JOptionPane.YES_OPTION || nu == 0) {
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            this.bntGuardar.setEnabled(false);
            this.bntModificar.setEnabled(false);
            this.bntEliminar.setEnabled(false);
            this.bntNuevo.setEnabled(true);
            removejtable();
            busqueda.setText("");
            rbNombres.setSelected(true);
            rbCodigo.setSelected(false);
            busqueda.requestFocus();

            this.dispose();
        }
    }

    /* La funcion de este metodo es limpiar y desabilitar campos que se encuentren en un contenedor
     * ejem: los JTextFiel de un panel, se envian a la capa de negocio "Utilidades.setEditableTexto()" 
     * para que este los limpie,habilite o desabilite dichos componentes */
    public void limpiar() {
        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
    }

    /* Para no sobrecargar la memoria y hacer una instancia cada vez que actualizamos la JTable se hace una
     * sola instancia y lo unico que se hace antes de actualizar la JTable es limpiar el modelo y enviarle los
     * nuevos datos a mostrar en la JTable  */
    public void removejtable() {
        while (alumnos.getRowCount() != 0) {
            model.removeRow(0);
        }
    }

    /*
     *Prepara los parametros para la consulta de datos que deseamos agregar al model del ComboBox
     *y se los envia a un metodo interno getRegistroCombo() 
     *
     */
    public void llenarcombo() {
        String Dato = "1";
        //String[] campos = {"grupo.grupo", "horario.dia", "horario.horariode", "horario.horarioa", "grupo.idgrupo"};
        String[] campos = {"grupo.grupo", "horario.dia", "grupo.idgrupo"};
        String inner = " INNER JOIN horario on grupo.horarios_idhorarios=horario.idhorarios";
        String[] condiciones = {"grupo.estado"};
        String[] Id = {Dato};
        grupo.removeAllItems();
        Component cmps = grupo;
        getRegistroCombo("grupo", campos, condiciones, Id, inner);

    }

    /*El metodo llenarcombo() envia los parametros para la consulta a la BD y el medoto
     *getRegistroCombo() se encarga de enviarlos a la capa de AccesoDatos.getRegistros()
     *quiern devolcera un ResultSet para luego obtener los valores y agregarlos al JConboBox
     *y a una Hashtable que nos servira para obtener el id y seleccionar valores.
     */
    public void getRegistroCombo(String tabla, String[] campos, String[] campocondicion, String[] condicionid, String inner) {
        try {
            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            rs = ac.getRegistros(tabla, campos, campocondicion, condicionid, inner);

            int cantcampos = campos.length;
            if (rs != null) {

                DefaultComboBoxModel modeloComboBox;
                modeloComboBox = new DefaultComboBoxModel();
                grupo.setModel(modeloComboBox);

                modeloComboBox.addElement(new mGrupo("", "0"));
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    int count = 0;
                    rs.beforeFirst();//regresa el puntero al primer registro
                    Object[] fila = new Object[cantcampos];
                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        count++;
                        //modeloComboBox.addElement(new mGrupo(rs.getString(1) + " " + rs.getString(2) + " " + FormatoFecha.getTimedoce(rs.getTime(3)) + " " + FormatoFecha.getTimedoce(rs.getTime(4)), "" + rs.getInt(5)));
                        modeloComboBox.addElement(new mGrupo(rs.getString(1) + " " + rs.getString(2), "" + rs.getInt(3)));
                        hashGrupo.put(rs.getString(1) + " " + rs.getString(2), "" + count);
                        System.out.print(rs.getString(1) + "n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron datos para la busqueda", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
            rs.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrio un Error :" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Este metodo se encarga de filtrar los datos que se deben ingresar en cada uno de los campos del formulario
     * podemos indicar que el usuario ingrese solo numeros , solo letras, numeros y letras, o cualquier caracter
     * tambien podemos validar si se aseptaran espacios en blanco en la cadena ingresada , para mas detalle visualizar
     * la clase TipoFiltro()  */
    private void setFiltroTexto() {

        TipoFiltro.setFiltraEntrada(codigo.getDocument(), FiltroCampos.NUM_LETRAS, 25, true);
        TipoFiltro.setFiltraEntrada(nombres.getDocument(), FiltroCampos.SOLO_LETRAS, 60, true);
        TipoFiltro.setFiltraEntrada(apellidos.getDocument(), FiltroCampos.SOLO_LETRAS, 60, true);
        TipoFiltro.setFiltraEntrada(direccion.getDocument(), FiltroCampos.NUM_LETRAS, 150, true);
        TipoFiltro.setFiltraEntrada(titularnombre.getDocument(), FiltroCampos.SOLO_LETRAS, 60, true);
        TipoFiltro.setFiltraEntrada(titularapellido.getDocument(), FiltroCampos.SOLO_LETRAS, 60, true);
        TipoFiltro.setFiltraEntrada(telefono.getDocument(), FiltroCampos.SOLO_NUMEROS, 16, false);
        TipoFiltro.setFiltraEntrada(busqueda.getDocument(), FiltroCampos.NUM_LETRAS, 100, true);
    }

    /* Este metodo recibe de el campo busqueda un parametro que es el que servirá para realizar la cunsulta
     * de los datos, este envia a la capa de negocio "peticiones.getRegistroPorPks( el modelo de la JTable,
     * el nombre de la tabla, los campos de la tabla a consultar, los campos de condiciones, y el dato a comparar
     * en la(s) condicion(es) de la busqueda) .
     *   
     * Nota: si el campo busqueda no contiene ningun dato devolvera todos los datos de la tabla o un mensage
     * indicando que no hay datos para la busqueda  
     *
     * @param Dato , dato a buscar
     * @return 
     */
    private void MostrarDatos(String Dato) {
        //{"codigo", "Nombres", "Apellidos","Fecha Nec","Colegiatura","Beca","Fecha Inicio","Estado"};
        //String conct = "concat(horario.codigo,' ',horario.dia,' ',DATE_FORMAT(horario.horariode,'%h:%i %p'),' ',DATE_FORMAT(horario.horarioa,'%h:%i %p'))";
        //String[] campos = {"grupo.idgrupo", "grupo.grupo", "grupo.fechainicio", "grupo.fechafin", conct,"grupo.estado"};
        String[] campos = {"alumno.codigo", "alumno.nombres", "alumno.apellidos", "alumno.fechanacimiento", "alumno.colegiatura", "alumno.cantidadbeca", "alumno.fechadeinicio", "alumno.estado"};
        String[] condiciones = {"alumno.codigo"};
        //String inner = " INNER JOIN horario on grupo.horarios_idhorarios=horario.idhorarios";
        String[] Id = {Dato};

        if (this.rbCodigo.isSelected()) {
            if (!Dato.isEmpty()) {
                removejtable();
                Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                Utilidades.esObligatorio(this.JPanelCampos, false);
                model = peticiones.getRegistroPorPks(model, "alumno", campos, condiciones, Id, "");
            } else {
                JOptionPane.showInternalMessageDialog(this, "Debe ingresar un codigo para la busqueda");
            }
        }
        if (this.rbNombres.isSelected()) {
            removejtable();
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            model = peticiones.getRegistroPorLike(model, "alumno", campos, "alumno.nombres", Dato, "");
        }
        Utilidades.ajustarAnchoColumnas(alumnos);
    }

    /* Este metodo  consulta en la BD el codigo de la fila seleccionada y llena los componentes
     * de la parte superior del formulario con los datos obtenidos en la capa de Negocio getRegistroSeleccionado().
     * 
     * @return 
     */
    private void filaseleccionada() {

        int fila = alumnos.getSelectedRow();
        String[] cond = {"alumno.codigo"};
        String[] id = {"" + alumnos.getValueAt(fila, 0)};
        String inner = " INNER JOIN alumnosengrupo ON alumno.idalumno = alumnosengrupo.alumno_idalumno   INNER JOIN grupo ON alumnosengrupo.grupo_idgrupo = grupo.idgrupo INNER JOIN horario ON grupo.horarios_idhorarios = horario.idhorarios";

        if (alumnos.getValueAt(fila, 0) != null) {

            String conct = "concat(horario.codigo,' ',horario.dia,' ',DATE_FORMAT(horario.horariode,'%h:%i %p'),' ',DATE_FORMAT(horario.horarioa,'%h:%i %p'))";
            //String[] campos={"alumno.codigo","alumno.nombres","alumno.apellidos","alumno.fechanacimiento","alumno.colegiatura","alumno.cantidadbeca","alumno.fechadeinicio","alumno.estado"};
            String concat2 = "concat(grupo.grupo,' ',horario.dia)";
            String[] campos = {"alumno.codigo", "alumno.nombres", "alumno.apellidos", "alumno.fechanacimiento", "alumno.sexo", "alumno.direccion", "alumno.telefono", "alumno.colegiatura", "alumno.fechainscripcion", "alumno.cantidadbeca", "alumno.fechadeinicio", "alumno.titularnombres", "alumno.titularapellidos", "alumno.estado", concat2};
            //String[] campos = {"grupo.grupo", "grupo.fechainicio", "grupo.fechafin", conct, "grupo.estado"};
            llenarcombo(); // borra los items de comboBox y lo vuelve a llenar
            //Component[] cmps = {nombres, fechainscripcion,fechainicio,horario, estado};
            Component[] cmps = {codigo, nombres, apellidos, fechanacimiento, sexo, direccion, telefono, colegiatura, fechainscripcion, beca, fechainicio, titularnombre, titularapellido, estado, grupo
            };

            Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");

            peticiones.getRegistroSeleccionado(cmps, "alumno", campos, cond, id, inner, hashGrupo);
            profesor();

            this.bntGuardar.setEnabled(false);
            this.bntModificar.setEnabled(true);
            this.bntEliminar.setEnabled(true);
            this.bntNuevo.setEnabled(false);
        }
    }

    public void profesor() {
        if (grupo.getSelectedIndex() != -1) {
            mGrupo horari = (mGrupo) grupo.getSelectedItem();
            String[] id = {horari.getID()};

            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();
            String[] cond = {"grupo.idgrupo"};
            String inner = " INNER JOIN grupo ON horario.idhorarios=grupo.horarios_idhorarios INNER JOIN profesor ON horario.maestro_idcatedratico=profesor.idcatedratico";
            if (!id.equals(0)) {

                String conct = "concat(profesor.nombre,' ',profesor.apellido)";
                String conct2 = "concat(horario.dia,' ',DATE_FORMAT(horario.horariode,'%h:%i %p'),' ',DATE_FORMAT(horario.horarioa,'%h:%i %p'))";
                String[] campos = {conct, conct2, "horario.colegiatura"};
                Component[] cmps = {profesor, horario, colegiatura};

                rs = ac.getRegistros("horario", campos, cond, id, inner);

                if (rs != null) {
                    try {
                        if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                            rs.beforeFirst();//regresa el puntero al primer registro
                            while (rs.next()) {//mientras tenga registros que haga lo siguiente
                                profesor.setText(rs.getString(1));
                                horario.setText(rs.getString(2));
                                colegiatura.setValue(rs.getFloat(3));
                            }
                        }
                        profesor.setEditable(false);
                        horario.setEditable(false);
                        colegiatura.setEditable(false);
                    } catch (SQLException e) {
                        profesor.setEditable(false);
                        horario.setEditable(false);
                        colegiatura.setEditable(false);
                        JOptionPane.showInternalMessageDialog(this, e);
                    }
                }

            }
        }
    }

    public void idasignagrupo(String codigo) {

        String[] id = {codigo};

        ResultSet rs;
        AccesoDatos ac = new AccesoDatos();
        String[] cond = {"alumno.codigo"};
        String[] campos = {"alumno.idalumno","alumnosengrupo.idasignagrupo"};
        String inner=" inner join alumnosengrupo on  alumno.idalumno=alumnosengrupo.alumno_idalumno ";
        
        rs = ac.getRegistros("alumno", campos, cond, id, inner);

        if (rs != null) {
            try {
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    rs.beforeFirst();//regresa el puntero al primer registro
                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        idalumno.setText(rs.getString(1));
                        idasignagrupo.setText(rs.getString(2));
                    }
                }

            } catch (SQLException e) {
                JOptionPane.showInternalMessageDialog(this, e);
            }
        }
    }
    
    public void idalumno(String codigo) {

        String[] id = {codigo};

        ResultSet rs;
        AccesoDatos ac = new AccesoDatos();
        String[] cond = {"alumno.codigo"};
        String[] campos = {"alumno.idalumno"};
        //String inner=" inner join alumnosengrupo on  alumno.idalumno=alumnosengrupo.idasignagrupo ";
        
        rs = ac.getRegistros("alumno", campos, cond, id, "");

        if (rs != null) {
            try {
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    rs.beforeFirst();//regresa el puntero al primer registro
                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        idalumno.setText(rs.getString(1));
                        //idasignagrupo.setText(rs.getString(2));
                    }
                }

            } catch (SQLException e) {
                JOptionPane.showInternalMessageDialog(this, e);
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelImage = new elaprendiz.gui.panel.PanelImage();
        pnlActionButtons = new javax.swing.JPanel();
        bntNuevo = new elaprendiz.gui.button.ButtonRect();
        bntModificar = new elaprendiz.gui.button.ButtonRect();
        bntGuardar = new elaprendiz.gui.button.ButtonRect();
        bntEliminar = new elaprendiz.gui.button.ButtonRect();
        bntCancelar = new elaprendiz.gui.button.ButtonRect();
        bntSalir = new elaprendiz.gui.button.ButtonRect();
        JPanelCampos = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        codigo = new elaprendiz.gui.textField.TextField();
        jLabel1 = new javax.swing.JLabel();
        nombres = new elaprendiz.gui.textField.TextField();
        jLabel11 = new javax.swing.JLabel();
        apellidos = new elaprendiz.gui.textField.TextField();
        jLabel14 = new javax.swing.JLabel();
        direccion = new elaprendiz.gui.textField.TextField();
        jLabel12 = new javax.swing.JLabel();
        fechanacimiento = new com.toedter.calendar.JDateChooser();
        sexo = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        estado = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        titularnombre = new elaprendiz.gui.textField.TextField();
        jLabel17 = new javax.swing.JLabel();
        titularapellido = new elaprendiz.gui.textField.TextField();
        jLabel15 = new javax.swing.JLabel();
        telefono = new elaprendiz.gui.textField.TextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fechainscripcion = new com.toedter.calendar.JDateChooser();
        horario = new elaprendiz.gui.textField.TextField();
        jLabel9 = new javax.swing.JLabel();
        fechainicio = new com.toedter.calendar.JDateChooser();
        grupo = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        beca = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        colegiatura = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        profesor = new elaprendiz.gui.textField.TextField();
        addHorario = new javax.swing.JButton();
        updatecombo = new javax.swing.JButton();
        JPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        alumnos = new javax.swing.JTable();
        JPanelBusqueda = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        busqueda = new elaprendiz.gui.textField.TextField();
        rbCodigo = new javax.swing.JRadioButton();
        rbNombres = new javax.swing.JRadioButton();
        rbNombres1 = new javax.swing.JRadioButton();
        idalumno = new javax.swing.JTextField();
        idasignagrupo = new javax.swing.JTextField();
        pnlPaginador = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 0, 0));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setForeground(new java.awt.Color(0, 0, 0));
        setIconifiable(true);
        setTitle("Alumno");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelImage.setLayout(null);

        pnlActionButtons.setBackground(java.awt.SystemColor.activeCaption);
        pnlActionButtons.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));
        pnlActionButtons.setForeground(new java.awt.Color(204, 204, 204));
        pnlActionButtons.setPreferredSize(new java.awt.Dimension(786, 52));
        pnlActionButtons.setLayout(new java.awt.GridBagLayout());

        bntNuevo.setBackground(new java.awt.Color(51, 153, 255));
        bntNuevo.setMnemonic(KeyEvent.VK_N);
        bntNuevo.setText("Nuevo");
        bntNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntNuevoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 84, 12, 0);
        pnlActionButtons.add(bntNuevo, gridBagConstraints);

        bntModificar.setBackground(new java.awt.Color(51, 153, 255));
        bntModificar.setMnemonic(KeyEvent.VK_M);
        bntModificar.setText("Modificar");
        bntModificar.setEnabled(false);
        bntModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntModificarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntModificar, gridBagConstraints);

        bntGuardar.setBackground(new java.awt.Color(51, 153, 255));
        bntGuardar.setMnemonic(KeyEvent.VK_G);
        bntGuardar.setText("Guardar");
        bntGuardar.setEnabled(false);
        bntGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntGuardarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntGuardar, gridBagConstraints);

        bntEliminar.setBackground(new java.awt.Color(51, 153, 255));
        bntEliminar.setMnemonic(KeyEvent.VK_E);
        bntEliminar.setText("Eliminar");
        bntEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntEliminarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntEliminar, gridBagConstraints);

        bntCancelar.setBackground(new java.awt.Color(51, 153, 255));
        bntCancelar.setMnemonic(KeyEvent.VK_X);
        bntCancelar.setText("Cancelar");
        bntCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCancelarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntCancelar, gridBagConstraints);

        bntSalir.setBackground(new java.awt.Color(51, 153, 255));
        bntSalir.setText("Salir");
        bntSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntSalirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 93);
        pnlActionButtons.add(bntSalir, gridBagConstraints);

        panelImage.add(pnlActionButtons);
        pnlActionButtons.setBounds(0, 430, 880, 50);

        JPanelCampos.setBackground(java.awt.SystemColor.activeCaption);
        JPanelCampos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        JPanelCampos.setForeground(new java.awt.Color(204, 204, 204));
        JPanelCampos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        JPanelCampos.setLayout(null);

        jTabbedPane1.setBackground(java.awt.SystemColor.inactiveCaption);

        jPanel1.setBackground(java.awt.SystemColor.activeCaption);
        jPanel1.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Código:");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(100, 30, 80, 20);

        codigo.setEditable(false);
        codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel1.add(codigo);
        codigo.setBounds(190, 30, 130, 21);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Nombres:");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(100, 60, 80, 20);

        nombres.setEditable(false);
        nombres.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        nombres.setName("nombres"); // NOI18N
        jPanel1.add(nombres);
        nombres.setBounds(190, 60, 250, 21);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Apellidos:");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(100, 90, 80, 20);

        apellidos.setEditable(false);
        apellidos.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel1.add(apellidos);
        apellidos.setBounds(190, 90, 250, 21);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel14.setText("Dirección:");
        jPanel1.add(jLabel14);
        jLabel14.setBounds(100, 120, 80, 20);

        direccion.setEditable(false);
        direccion.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel1.add(direccion);
        direccion.setBounds(190, 120, 250, 21);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Fecha Nacimiento:");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(450, 30, 150, 21);

        fechanacimiento.setDate(Calendar.getInstance().getTime());
        fechanacimiento.setDateFormatString("dd/MM/yyyy");
        fechanacimiento.setEnabled(false);
        fechanacimiento.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fechanacimiento.setMaxSelectableDate(new java.util.Date(3093496470100000L));
        fechanacimiento.setMinSelectableDate(new java.util.Date(-62135744300000L));
        fechanacimiento.setPreferredSize(new java.awt.Dimension(120, 22));
        jPanel1.add(fechanacimiento);
        fechanacimiento.setBounds(610, 30, 130, 21);

        sexo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Hombre", "Mujer" }));
        sexo.setName("Horario"); // NOI18N
        jPanel1.add(sexo);
        sexo.setBounds(610, 60, 130, 21);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel18.setText("Sexo:");
        jPanel1.add(jLabel18);
        jLabel18.setBounds(520, 60, 80, 20);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Estado:");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(500, 90, 110, 20);

        estado.setBackground(new java.awt.Color(51, 153, 255));
        estado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        estado.setForeground(new java.awt.Color(255, 255, 255));
        estado.setText("Activo");
        estado.setEnabled(false);
        estado.setName("JRadioButton"); // NOI18N
        jPanel1.add(estado);
        estado.setBounds(620, 90, 130, 21);

        jTabbedPane1.addTab("Datos Alumno", jPanel1);

        jPanel3.setBackground(java.awt.SystemColor.activeCaption);
        jPanel3.setLayout(null);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Titular Nombres:");
        jPanel3.add(jLabel2);
        jLabel2.setBounds(60, 30, 120, 20);

        titularnombre.setEditable(false);
        titularnombre.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        titularnombre.setName("descripcion"); // NOI18N
        jPanel3.add(titularnombre);
        titularnombre.setBounds(190, 30, 250, 21);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel17.setText("Titular Apellidos:");
        jPanel3.add(jLabel17);
        jLabel17.setBounds(50, 70, 130, 20);

        titularapellido.setEditable(false);
        titularapellido.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel3.add(titularapellido);
        titularapellido.setBounds(190, 70, 250, 21);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel15.setText("Telefono:");
        jPanel3.add(jLabel15);
        jLabel15.setBounds(100, 110, 80, 20);

        telefono.setEditable(false);
        telefono.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel3.add(telefono);
        telefono.setBounds(190, 110, 250, 21);

        jTabbedPane1.addTab("Titular", jPanel3);

        jPanel2.setBackground(java.awt.SystemColor.activeCaption);
        jPanel2.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Profesor:");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(70, 90, 80, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Fecha Incripción:");
        jPanel2.add(jLabel6);
        jLabel6.setBounds(510, 30, 150, 21);

        fechainscripcion.setDate(Calendar.getInstance().getTime());
        fechainscripcion.setDateFormatString("dd/MM/yyyy");
        fechainscripcion.setEnabled(false);
        fechainscripcion.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fechainscripcion.setMaxSelectableDate(new java.util.Date(3093496470100000L));
        fechainscripcion.setMinSelectableDate(new java.util.Date(-62135744300000L));
        fechainscripcion.setPreferredSize(new java.awt.Dimension(120, 22));
        jPanel2.add(fechainscripcion);
        fechainscripcion.setBounds(680, 30, 130, 21);

        horario.setEditable(false);
        horario.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel2.add(horario);
        horario.setBounds(170, 60, 270, 21);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Fecha Inicio:");
        jPanel2.add(jLabel9);
        jLabel9.setBounds(550, 60, 110, 17);

        fechainicio.setDate(Calendar.getInstance().getTime());
        fechainicio.setDateFormatString("dd/MM/yyyy");
        fechainicio.setEnabled(false);
        fechainicio.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fechainicio.setMaxSelectableDate(new java.util.Date(3093496470100000L));
        fechainicio.setMinSelectableDate(new java.util.Date(-62135744300000L));
        fechainicio.setPreferredSize(new java.awt.Dimension(120, 22));
        jPanel2.add(fechainicio);
        fechainicio.setBounds(680, 60, 130, 21);

        grupo.setModel(modelCombo = new DefaultComboBoxModel());
        grupo.setName("Horario"); // NOI18N
        jPanel2.add(grupo);
        grupo.setBounds(170, 30, 270, 21);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText("Grupo:");
        jPanel2.add(jLabel13);
        jLabel13.setBounds(70, 30, 80, 20);

        beca.setEditable(false);
        beca.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        beca.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        beca.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        beca.setPreferredSize(new java.awt.Dimension(80, 23));
        jPanel2.add(beca);
        beca.setBounds(170, 120, 80, 23);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel16.setText("Beca:");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(40, 120, 110, 20);

        colegiatura.setEditable(false);
        colegiatura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        colegiatura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        colegiatura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        colegiatura.setPreferredSize(new java.awt.Dimension(80, 23));
        jPanel2.add(colegiatura);
        colegiatura.setBounds(370, 120, 70, 23);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel19.setText("Colegiatura:");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(260, 120, 100, 20);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Horario:");
        jPanel2.add(jLabel5);
        jLabel5.setBounds(70, 60, 80, 20);

        profesor.setEditable(false);
        profesor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel2.add(profesor);
        profesor.setBounds(170, 90, 270, 21);

        addHorario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/horario3.png"))); // NOI18N
        addHorario.setToolTipText("Pulse para crear un nuevo Horario");
        addHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHorarioActionPerformed(evt);
            }
        });
        jPanel2.add(addHorario);
        addHorario.setBounds(30, 50, 40, 40);

        updatecombo.setEnabled(false);
        updatecombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatecomboActionPerformed(evt);
            }
        });
        jPanel2.add(updatecombo);
        updatecombo.setBounds(440, 30, 20, 20);

        jTabbedPane1.addTab("Datos Carrera", jPanel2);

        JPanelCampos.add(jTabbedPane1);
        jTabbedPane1.setBounds(0, 0, 880, 190);

        panelImage.add(JPanelCampos);
        JPanelCampos.setBounds(0, 40, 880, 190);

        JPanelTable.setOpaque(false);
        JPanelTable.setPreferredSize(new java.awt.Dimension(786, 402));
        JPanelTable.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        alumnos.setForeground(new java.awt.Color(51, 51, 51));
        alumnos.setModel(model = new DefaultTableModel(null, titulos)
            {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            alumnos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            alumnos.setFocusCycleRoot(true);
            alumnos.setGridColor(new java.awt.Color(51, 51, 255));
            alumnos.setRowHeight(22);
            alumnos.setSelectionBackground(java.awt.SystemColor.activeCaption);
            alumnos.setSurrendersFocusOnKeystroke(true);
            alumnos.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    alumnosMouseClicked(evt);
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    alumnosMouseClicked(evt);
                }
            });
            alumnos.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    alumnosKeyPressed(evt);
                }
            });
            jScrollPane1.setViewportView(alumnos);
            alumnos.getAccessibleContext().setAccessibleName("");

            JPanelTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

            panelImage.add(JPanelTable);
            JPanelTable.setBounds(0, 300, 880, 130);

            JPanelBusqueda.setBackground(java.awt.SystemColor.inactiveCaption);
            JPanelBusqueda.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            JPanelBusqueda.setLayout(null);

            jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel7.setText("Buscar Por:");
            JPanelBusqueda.add(jLabel7);
            jLabel7.setBounds(210, 10, 80, 17);

            busqueda.setPreferredSize(new java.awt.Dimension(250, 27));
            busqueda.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    busquedaActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(busqueda);
            busqueda.setBounds(300, 10, 250, 27);

            rbCodigo.setBackground(new java.awt.Color(51, 153, 255));
            rbCodigo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            rbCodigo.setForeground(new java.awt.Color(255, 255, 255));
            rbCodigo.setText("Codigo");
            rbCodigo.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rbCodigoActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(rbCodigo);
            rbCodigo.setBounds(280, 40, 80, 25);

            rbNombres.setBackground(new java.awt.Color(51, 153, 255));
            rbNombres.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            rbNombres.setForeground(new java.awt.Color(255, 255, 255));
            rbNombres.setSelected(true);
            rbNombres.setText("Nombre");
            rbNombres.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rbNombresActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(rbNombres);
            rbNombres.setBounds(380, 40, 90, 25);

            rbNombres1.setBackground(new java.awt.Color(51, 153, 255));
            rbNombres1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            rbNombres1.setForeground(new java.awt.Color(255, 255, 255));
            rbNombres1.setSelected(true);
            rbNombres1.setText("Apellido");
            rbNombres1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rbNombres1ActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(rbNombres1);
            rbNombres1.setBounds(490, 40, 79, 25);
            JPanelBusqueda.add(idalumno);
            idalumno.setBounds(40, 20, 59, 20);
            JPanelBusqueda.add(idasignagrupo);
            idasignagrupo.setBounds(40, 40, 59, 20);

            panelImage.add(JPanelBusqueda);
            JPanelBusqueda.setBounds(0, 230, 880, 70);

            pnlPaginador.setBackground(new java.awt.Color(57, 104, 163));
            pnlPaginador.setPreferredSize(new java.awt.Dimension(786, 40));
            pnlPaginador.setLayout(new java.awt.GridBagLayout());

            jLabel8.setFont(new java.awt.Font("Script MT Bold", 1, 32)); // NOI18N
            jLabel8.setForeground(new java.awt.Color(255, 255, 255));
            jLabel8.setText("<--Alumnos-->");
            pnlPaginador.add(jLabel8, new java.awt.GridBagConstraints());

            panelImage.add(pnlPaginador);
            pnlPaginador.setBounds(0, 0, 880, 40);

            getContentPane().add(panelImage, java.awt.BorderLayout.CENTER);

            getAccessibleContext().setAccessibleName("Profesores");

            setBounds(0, 0, 893, 512);
        }// </editor-fold>//GEN-END:initComponents

    private void bntNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntNuevoActionPerformed
        // TODO add your handling code here:
        Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
        llenarcombo();
        estado.setSelected(true);
        this.bntGuardar.setEnabled(true);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(false);
        nombres.requestFocus();

    }//GEN-LAST:event_bntNuevoActionPerformed

    private void bntGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntGuardarActionPerformed
        // TODO add your handling code here:
        if (Utilidades.esObligatorio(this.JPanelCampos, true)) {
            JOptionPane.showInternalMessageDialog(this, "Los campos marcados son Obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Grabar el Registro?", "Pregunta", 0);
        if (resp == 0) {

            boolean seguardo = false;
            String nombreTabla = "alumno";
            String campos = "codigo, nombres, apellidos, fechanacimiento, sexo, direccion, telefono, colegiatura, fechainscripcion, cantidadbeca, fechadeinicio, titularnombres, titularapellidos, estado";
            String fechainscripcio = FormatoFecha.getFormato(fechainscripcion.getCalendar().getTime(), FormatoFecha.A_M_D);
            String fechainici = FormatoFecha.getFormato(fechainicio.getCalendar().getTime(), FormatoFecha.A_M_D);
            String fechanacimient = FormatoFecha.getFormato(fechanacimiento.getCalendar().getTime(), FormatoFecha.A_M_D);
            //Para obtener el id en la base de datos

            mGrupo horari = (mGrupo) grupo.getSelectedItem();
            String idgrupo = horari.getID();

            int estad = 0;
            if (this.estado.isSelected()) {
                estad = 1;
            }
            Object[] valores = {codigo.getText(), nombres.getText(), apellidos.getText(), fechanacimient, sexo.getSelectedItem(), direccion.getText(), telefono.getText(), colegiatura.getText(), fechainscripcio, beca.getText(), fechainici, titularnombre.getText(), titularapellido.getText(), estad
            };

            //boolean seguardo2 = false;
            String nombreTabla2 = "alumnosengrupo";
            String campos2 = "grupo_idgrupo, alumno_idalumno, estado";

            seguardo = peticiones.guardarRegistros(nombreTabla, campos, valores);

            if (seguardo) {
                idalumno(codigo.getText());
                Object[] valores2 = {idgrupo, idalumno.getText(), 1};
                seguardo = peticiones.guardarRegistros(nombreTabla2, campos2, valores2);
                if (seguardo) {
                    Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                    idalumno.setText("");
                    MostrarDatos(busqueda.getText());
                    busqueda.requestFocus();
                    JOptionPane.showInternalMessageDialog(this, "El dato se ha Guardado Correctamente", "Guardar", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_bntGuardarActionPerformed

    private void bntSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSalirActionPerformed
        cerrarVentana();
    }//GEN-LAST:event_bntSalirActionPerformed

    private void alumnosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_alumnosMouseClicked
        // TODO add your handling code here:
        filaseleccionada();

    }//GEN-LAST:event_alumnosMouseClicked

    private void bntEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntEliminarActionPerformed
        // TODO add your handling code here:

        int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Eliminar el Registro?", "Pregunta", 0);
        if (resp == 0) {

            int fila = alumnos.getSelectedRow();
            String id = (String) "" + alumnos.getValueAt(fila, 0);
            String nombreTabla = "alumno", nomColumnaCambiar = "estado";
            String nomColumnaId = "codigo";
            int seguardo = 0;

            seguardo = peticiones.eliminarRegistro(nombreTabla, nomColumnaCambiar, nomColumnaId, id);

            if (seguardo == 1) {
                Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
                MostrarDatos(busqueda.getText());
                busqueda.requestFocus();
                JOptionPane.showInternalMessageDialog(this, "El dato se ha Eliminado Correctamente", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bntEliminarActionPerformed

    private void bntModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntModificarActionPerformed
        // TODO add your handling code here:

        if (Utilidades.esObligatorio(this.JPanelCampos, true)) {
            JOptionPane.showInternalMessageDialog(this, "Los campos marcados son Obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Modificar el Registro?", "Pregunta", 0);
        if (resp == 0) {

            int seguardo = 0;
            int fila = alumnos.getSelectedRow();
            String id = (String) "" + alumnos.getValueAt(fila, 0);
            String columnaId = "codigo";

            String nombreTabla = "alumno";
            String campos = "codigo, nombres, apellidos, fechanacimiento, sexo, direccion, telefono, colegiatura, fechainscripcion, cantidadbeca, fechadeinicio, titularnombres, titularapellidos, estado";
            String fechainscripcio = FormatoFecha.getFormato(fechainscripcion.getCalendar().getTime(), FormatoFecha.A_M_D);
            String fechainici = FormatoFecha.getFormato(fechainicio.getCalendar().getTime(), FormatoFecha.A_M_D);
            String fechanacimient = FormatoFecha.getFormato(fechanacimiento.getCalendar().getTime(), FormatoFecha.A_M_D);
            //Para obtener el id en la base de datos

            mGrupo horari = (mGrupo) grupo.getSelectedItem();
            String idgrupo = horari.getID();

            int estad = 0;
            if (this.estado.isSelected()) {
                estad = 1;
            }
            Object[] valores = {codigo.getText(), nombres.getText(), apellidos.getText(), fechanacimient, sexo.getSelectedItem(), direccion.getText(), telefono.getText(), colegiatura.getText(), fechainscripcio, beca.getText(), fechainici, titularnombre.getText(), titularapellido.getText(), estad, id
            };

            seguardo = peticiones.actualizarRegistro(nombreTabla, campos, valores, columnaId, id);

            String nombreTabla2 = "alumnosengrupo";
            String campos2 = "grupo_idgrupo, alumno_idalumno, estado";
            String columnaId2="idasignagrupo";
            
            if (seguardo == 1) {
                idasignagrupo(id);
                String idasignagrup=idasignagrupo.getText();
                String idalumn=idalumno.getText();
                
                Object[] valores2 = {idgrupo, idalumn, 1,idasignagrup};
                seguardo = peticiones.actualizarRegistro(nombreTabla2, campos2, valores2, columnaId2, idasignagrup);
                if (seguardo==1) {
                    Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                    MostrarDatos(busqueda.getText());
                    idasignagrupo.setText("");
                    idalumno.setText("");
                    busqueda.requestFocus();
                    JOptionPane.showInternalMessageDialog(this, "El dato se ha Modificado Correctamente", "Modificar", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_bntModificarActionPerformed

    private void bntCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCancelarActionPerformed
        // TODO add your handling code here:
        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
        Utilidades.esObligatorio(this.JPanelCampos, false);
        removejtable();
        this.bntGuardar.setEnabled(false);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(true);
        removejtable();
        busqueda.requestFocus();

    }//GEN-LAST:event_bntCancelarActionPerformed

    private void rbCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCodigoActionPerformed
        // TODO add your handling code here:
        rbNombres.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbCodigoActionPerformed

    private void rbNombresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNombresActionPerformed
        // TODO add your handling code here:
        rbCodigo.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbNombresActionPerformed

    private void busquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busquedaActionPerformed
        // TODO add your handling code here:
        MostrarDatos(busqueda.getText());
    }//GEN-LAST:event_busquedaActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:
        cerrarVentana();
    }//GEN-LAST:event_formInternalFrameClosing

    private void alumnosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_alumnosKeyPressed
        // TODO add your handling code here:
        int key = evt.getKeyCode();
        if (key == java.awt.event.KeyEvent.VK_SPACE) {
            filaseleccionada();
        }
        if (key == java.awt.event.KeyEvent.VK_DOWN || key == java.awt.event.KeyEvent.VK_UP) {
            limpiar();
        }
    }//GEN-LAST:event_alumnosKeyPressed

    private void rbNombres1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNombres1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbNombres1ActionPerformed

    private void addHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHorarioActionPerformed
        // TODO add your handling code here:
        if (frmHorario == null) {
            frmHorario = new Horario();
        }
        adminInternalFrame(dp, frmHorario);
        updatecombo.setEnabled(true);
    }//GEN-LAST:event_addHorarioActionPerformed

    private void updatecomboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatecomboActionPerformed
        // TODO add your handling code here:
        llenarcombo();
    }//GEN-LAST:event_updatecomboActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanelBusqueda;
    private javax.swing.JPanel JPanelCampos;
    private javax.swing.JPanel JPanelTable;
    private javax.swing.JButton addHorario;
    private javax.swing.JTable alumnos;
    private elaprendiz.gui.textField.TextField apellidos;
    private javax.swing.JFormattedTextField beca;
    private elaprendiz.gui.button.ButtonRect bntCancelar;
    private elaprendiz.gui.button.ButtonRect bntEliminar;
    private elaprendiz.gui.button.ButtonRect bntGuardar;
    private elaprendiz.gui.button.ButtonRect bntModificar;
    private elaprendiz.gui.button.ButtonRect bntNuevo;
    private elaprendiz.gui.button.ButtonRect bntSalir;
    private elaprendiz.gui.textField.TextField busqueda;
    private elaprendiz.gui.textField.TextField codigo;
    private javax.swing.JFormattedTextField colegiatura;
    private elaprendiz.gui.textField.TextField direccion;
    private javax.swing.JRadioButton estado;
    private com.toedter.calendar.JDateChooser fechainicio;
    private com.toedter.calendar.JDateChooser fechainscripcion;
    private com.toedter.calendar.JDateChooser fechanacimiento;
    private javax.swing.JComboBox grupo;
    private elaprendiz.gui.textField.TextField horario;
    private javax.swing.JTextField idalumno;
    private javax.swing.JTextField idasignagrupo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private elaprendiz.gui.textField.TextField nombres;
    private elaprendiz.gui.panel.PanelImage panelImage;
    private javax.swing.JPanel pnlActionButtons;
    private javax.swing.JPanel pnlPaginador;
    private elaprendiz.gui.textField.TextField profesor;
    private javax.swing.JRadioButton rbCodigo;
    private javax.swing.JRadioButton rbNombres;
    private javax.swing.JRadioButton rbNombres1;
    private javax.swing.JComboBox sexo;
    private elaprendiz.gui.textField.TextField telefono;
    private elaprendiz.gui.textField.TextField titularapellido;
    private elaprendiz.gui.textField.TextField titularnombre;
    private javax.swing.JButton updatecombo;
    // End of variables declaration//GEN-END:variables
}
