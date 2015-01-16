/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.https://www.youtube.com/watch?v=ICF-RldvSIo
 */
package Capa_Presentacion;

import Capa_Datos.AccesoDatos;
import static Capa_Negocio.AddForms.adminInternalFrame;
import Capa_Negocio.Editor_CheckBox;
import Capa_Negocio.FormatoDecimal;
import Capa_Negocio.FormatoFecha;
import Capa_Negocio.Peticiones;
import Capa_Negocio.Renderer_CheckBox;
import Capa_Negocio.Utilidades;
import static Capa_Presentacion.Principal.dp;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import modelos.mGrupo;

/**
 *
 * @author GLARA
 */
public class Pagos extends javax.swing.JInternalFrame {

    /*El modelo se define en : Jtable-->propiedades-->model--> <User Code> */
    DefaultTableModel model;
    DefaultComboBoxModel modelCombo;
    String[] titulos = {"Codigo", "Descripción", "Monto", "Cantidad", "Subtotal", "Estado","Check"};//Titulos para Jtabla
    /*Se hace una instancia de la clase que recibira las peticiones de esta capa de aplicación*/
    Peticiones peticiones = new Peticiones();
    public static Hashtable<String, String> hashGrupo = new Hashtable<>();

    //private static Profesor frmProfesor = new Profesor();
    //private static Carrera frmCarrera = new Carrera();
    /*Se hace una instancia de la clase que recibira las peticiones de mensages de la capa de aplicación*/
    //public static JOptionMessage msg = new JOptionMessage();
    /**
     * Creates new form Cliente
     */
    public Pagos() {
        initComponents();
        setFiltroTexto();
        addEscapeKey();

//        cDia.addItemListener(
//                (ItemEvent e) -> {
//                    if (e.getStateChange() == ItemEvent.SELECTED) {
//                        selecciondia();
//                    }
//                });
        cGrupo.addItemListener(
                (ItemEvent e) -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        selecciongrupo();
                    }
                });
        colegiaturas.getColumnModel().getColumn(2).setCellEditor(new Editor_CheckBox());
        //para pintar la columna con el CheckBox en la tabla, en este caso, la primera columna
        colegiaturas.getColumnModel().getColumn(2).setCellRenderer(new Renderer_CheckBox());
        //llenarcomboprofesor();
        //llenarcombogrupo();
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
            codigoa.setText("");
            codigoa.requestFocus();
            this.dispose();
        }
    }

//    /* La funcion de este metodo es limpiar y desabilitar campos que se encuentren en un contenedor
//     * ejem: los JTextFiel de un panel, se envian a la capa de negocio "Utilidades.setEditableTexto()" 
//     * para que este los limpie,habilite o desabilite dichos componentes */
//    public void limpiar() {
//        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
//        Utilidades.setEditableTexto(this.JPanelRecibo, false, null, true, "");
//    }

    /* Para no sobrecargar la memoria y hacer una instancia cada vez que actualizamos la JTable se hace una
     * sola instancia y lo unico que se hace antes de actualizar la JTable es limpiar el modelo y enviarle los
     * nuevos datos a mostrar en la JTable  */
    public void removejtable() {
        while (colegiaturas.getRowCount() != 0) {
            model.removeRow(0);
        }
    }

//    private void selecciondia() {
//        if (cDia.getSelectedIndex() == 0) {
//            cGrupo.removeAllItems();
//        } else if (cDia.getSelectedIndex() > 0) {
//            String sdia = (String) cDia.getSelectedItem();
//            llenarcombogrupo(sdia);
//        }
//    }

    /*
     *Prepara los parametros para la consulta de datos que deseamos agregar al model del ComboBox
     *y se los envia a un metodo interno getRegistroCombo() 
     *
     */
    public static void llenarcombogrupo(String idalumn) {
        String Dato = "1";
        String[] campos = {"grupo.codigo", "grupo.descripcion", "grupo.idgrupo"};
        String[] condiciones = {"grupo.estado", "alumno.codigo"};
        String[] Id = {Dato, idalumn};
        cGrupo.removeAllItems();
        String inner = " INNER JOIN alumnosengrupo ON grupo.idgrupo = alumnosengrupo.grupo_idgrupo INNER JOIN alumno ON alumnosengrupo.alumno_idalumno = alumno.idalumno ";

        //Component cmps = profesor;
        getRegistroCombo("grupo", campos, condiciones, Id, inner);

    }

    /*El metodo llenarcombo() envia los parametros para la consulta a la BD y el medoto
     *getRegistroCombo() se encarga de enviarlos a la capa de AccesoDatos.getRegistros()
     *quiern devolcera un ResultSet para luego obtener los valores y agregarlos al JConboBox
     *y a una Hashtable que nos servira para obtener el id y seleccionar valores.
     */
    public static void getRegistroCombo(String tabla, String[] campos, String[] campocondicion, String[] condicionid, String inner) {
        try {
            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            rs = ac.getRegistros(tabla, campos, campocondicion, condicionid, inner);

            int cantcampos = campos.length;
            if (rs != null) {

                DefaultComboBoxModel modeloComboBox;
                modeloComboBox = new DefaultComboBoxModel();
                cGrupo.setModel(modeloComboBox);

                modeloComboBox.addElement(new mGrupo("", "0"));
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    int count = 0;
                    rs.beforeFirst();//regresa el puntero al primer registro
                    //Object[] fila = new Object[cantcampos];
                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        count++;
                        modeloComboBox.addElement(new mGrupo(rs.getString(1) + " " + rs.getString(2), "" + rs.getInt(3)));
                        hashGrupo.put(rs.getString(1) + " " + rs.getString(2), "" + count);
                        //System.out.print(rs.getString(1) + " " + rs.getString(2)+ "" + count+"\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No se encontraron datos para la busqueda", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
            //rs.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrio un Error :" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void selecciongrupo() {
        if (cGrupo.getSelectedIndex() == 0) {
            profesor.setText("");
            carrera.setText("");
            horade.setText("");
            horaa.setText("");
            fechainicio.setText("");
            fechafin.setText("");
            inscripcion.setValue(null);
            colegiatura.setValue(null);
        } else if (cGrupo.getSelectedIndex() != -1) {

            mGrupo grup = (mGrupo) cGrupo.getSelectedItem();
            String[] id = {grup.getID()};

            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();
            String[] cond = {"grupo.idgrupo"};
            String inner = " INNER JOIN profesor on grupo.profesor_idcatedratico=profesor.idcatedratico INNER JOIN carrera on grupo.carrera_idcarrera=carrera.idcarrera ";
            if (!id.equals(0)) {

                String conct = "concat(profesor.nombre,' ',profesor.apellido)";
                String[] campos = {conct, "carrera.descripcion", "DATE_FORMAT(grupo.horariode,'%h:%i %p')", "DATE_FORMAT(grupo.horarioa,'%h:%i %p')", "DATE_FORMAT(grupo.fechainicio,'%d-%m-%Y')", "DATE_FORMAT(grupo.fechafin,'%d-%m-%Y')", "grupo.inscripcion", "grupo.colegiatura", "grupo.dia"};

                rs = ac.getRegistros("grupo", campos, cond, id, inner);

                if (rs != null) {
                    try {
                        if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                            rs.beforeFirst();//regresa el puntero al primer registro
                            while (rs.next()) {//mientras tenga registros que haga lo siguiente
                                profesor.setText(rs.getString(1));
                                carrera.setText(rs.getString(2));
                                horade.setText(rs.getString(3));
                                horaa.setText(rs.getString(4));
                                fechainicio.setText((rs.getString(5)));
                                fechafin.setText((rs.getString(6)));
                                inscripcion.setValue(rs.getFloat(7));
                                colegiatura.setValue(rs.getFloat(8));
                                dia.setText(rs.getString(9));

                            }
                        }
                        //profesor.setEditable(false);
                    } catch (SQLException e) {
                        //profesor.setEditable(false);
                        JOptionPane.showInternalMessageDialog(this, e);
                    }
                }

            }
        }
    }

    /*
     * Metodo para buscar un alumno por su codigo
     */
    public void balumnocodigo(String codigo) {
        if (codigo.isEmpty()) {
            //codigoa.setText(rs.getString(1));
            //filaseleccionada(alumnos.getValueAt(p, 0).toString());
            //llenarcombogrupo(rs.getString(1));
            nombrealumno.setText("");
            beca.setText("");
            //Date fechaini = FormatoFecha.StringToDate(rs.getString(6));
            inicioalumno.setDate(null);
            estado.setText("");
            cGrupo.removeAllItems();

        } else if (!codigo.isEmpty()) {

            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            String[] campos = {"alumno.codigo", "alumno.nombres", "alumno.apellidos", "DATE_FORMAT(alumno.fechanacimiento,'%d-%m-%Y')", "alumno.cantidadbeca", "DATE_FORMAT(alumno.fechadeinicio,'%d-%m-%Y')", "alumno.estado"};
            String[] cond = {"alumno.codigo"};
            String[] id = {codigo};
            //String[] Id = {Dato};

            //String[] cond = {"alumno.idgrupo"};
            //String inner = " INNER JOIN profesor on grupo.profesor_idcatedratico=profesor.idcatedratico INNER JOIN carrera on grupo.carrera_idcarrera=carrera.idcarrera ";
            if (!id.equals(0)) {

                //String conct = "concat(profesor.nombre,' ',profesor.apellido)";
                //String[] campos = {conct, "carrera.descripcion", "DATE_FORMAT(grupo.horariode,'%h:%i %p')", "DATE_FORMAT(grupo.horarioa,'%h:%i %p')", "DATE_FORMAT(grupo.fechainicio,'%d-%m-%Y')", "DATE_FORMAT(grupo.fechafin,'%d-%m-%Y')", "grupo.inscripcion", "grupo.colegiatura","grupo.dia"};
                rs = ac.getRegistros("alumno", campos, cond, id, "");

                if (rs != null) {
                    try {
                        if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                            rs.beforeFirst();//regresa el puntero al primer registro
                            while (rs.next()) {//mientras tenga registros que haga lo siguiente
//                                profesor.setText(rs.getString(1));
//                                carrera.setText(rs.getString(2));
//                                horade.setText(rs.getString(3));
//                                horaa.setText(rs.getString(4));
//                                fechainicio.setText((rs.getString(5)));
//                                fechafin.setText((rs.getString(6)));
//                                inscripcion.setValue(rs.getFloat(7));
//                                colegiatura.setValue(rs.getFloat(8));
//                                dia.setText(rs.getString(9));

                                codigoa.setText(rs.getString(1));
                                //filaseleccionada(alumnos.getValueAt(p, 0).toString());
                                llenarcombogrupo(rs.getString(1));
                                nombrealumno.setText(rs.getString(2) + " " + rs.getString(3));
                                beca.setText(rs.getString(5));
                                Date fechaini = FormatoFecha.StringToDate(rs.getString(6));
                                inicioalumno.setDate(fechaini);
                                
                                if (rs.getString(7).equals("0")) {
                                    estado.setText("Inactivo");
                                    estado.setForeground(Color.red);
                                } else if (rs.getString(7).equals("1")) {
                                    estado.setText("Activo");
                                    estado.setForeground(Color.WHITE/*new java.awt.Color(102, 204, 0)*/);
                                }
                                //estado.setText(rs.getString(7));

                            }
                        } else {
                            JOptionPane.showInternalMessageDialog(this, " El codigo no fue encontrado ");
                            nombrealumno.setText("");
                            beca.setText("");
                            //Date fechaini = FormatoFecha.StringToDate(rs.getString(6));
                            inicioalumno.setDate(null);
                            estado.setText("");
                            cGrupo.removeAllItems();
                        }
                        //profesor.setEditable(false);
                    } catch (SQLException e) {
                        //profesor.setEditable(false);
                        JOptionPane.showInternalMessageDialog(this, e);
                    }
                } else {
                    JOptionPane.showInternalMessageDialog(this, " El codigo no fue encontrado ");
                    nombrealumno.setText("");
                    beca.setText("");
                    //Date fechaini = FormatoFecha.StringToDate(rs.getString(6));
                    inicioalumno.setDate(null);
                    estado.setText("");
                    cGrupo.removeAllItems();
                }

            }
        }
    }

    /* Este metodo se encarga de filtrar los datos que se deben ingresar en cada uno de los campos del formulario
     * podemos indicar que el usuario ingrese solo numeros , solo letras, numeros y letras, o cualquier caracter
     * tambien podemos validar si se aseptaran espacios en blanco en la cadena ingresada , para mas detalle visualizar
     * la clase TipoFiltro()  */
    private void setFiltroTexto() {

        //TipoFiltro.setFiltraEntrada(codigo.getDocument(), FiltroCampos.NUM_LETRAS, 45, false);
        //TipoFiltro.setFiltraEntrada(descripcion.getDocument(), FiltroCampos.NUM_LETRAS, 60, true);
        //TipoFiltro.setFiltraEntrada(dia.getDocument(), FiltroCampos.SOLO_LETRAS, 45, false);
        //TipoFiltro.setFiltraEntrada(profesor.getDocument(), FiltroCampos.NUM_LETRAS, 200, true);
        //TipoFiltro.setFiltraEntrada(cantalumnos.getDocument(), FiltroCampos.SOLO_NUMEROS, 5, true);
//        TipoFiltro.setFiltraEntrada(colegiatura.getDocument(), FiltroCampos.SOLO_NUMEROS, 12, false);
        //TipoFiltro.setFiltraEntrada(busqueda.getDocument(), FiltroCampos.NUM_LETRAS, 100, true);
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

        popupprofesor = new javax.swing.JPopupMenu();
        Nuevo_Profesor = new javax.swing.JMenuItem();
        Actualizar_Profesor = new javax.swing.JMenuItem();
        popupcarrera = new javax.swing.JPopupMenu();
        Nueva_Carrera = new javax.swing.JMenuItem();
        Actualizar_Carrera = new javax.swing.JMenuItem();
        popuppromatricula = new javax.swing.JPopupMenu();
        Nueva_Matricula = new javax.swing.JMenuItem();
        Actualiza = new javax.swing.JMenuItem();
        panelImage = new elaprendiz.gui.panel.PanelImage();
        pnlActionButtons = new javax.swing.JPanel();
        bntNuevo = new elaprendiz.gui.button.ButtonRect();
        bntModificar = new elaprendiz.gui.button.ButtonRect();
        bntGuardar = new elaprendiz.gui.button.ButtonRect();
        bntEliminar = new elaprendiz.gui.button.ButtonRect();
        bntCancelar = new elaprendiz.gui.button.ButtonRect();
        bntSalir = new elaprendiz.gui.button.ButtonRect();
        JPanelCampos = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cGrupo = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        carrera = new elaprendiz.gui.textField.TextField();
        profesor = new elaprendiz.gui.textField.TextField();
        inscripcion = new javax.swing.JFormattedTextField();
        colegiatura = new javax.swing.JFormattedTextField();
        horaa = new elaprendiz.gui.textField.TextField();
        horade = new elaprendiz.gui.textField.TextField();
        fechainicio = new elaprendiz.gui.textField.TextField();
        fechafin = new elaprendiz.gui.textField.TextField();
        jLabel12 = new javax.swing.JLabel();
        dia = new elaprendiz.gui.textField.TextField();
        JPanelTable = new javax.swing.JPanel();
        tbPane = new elaprendiz.gui.panel.TabbedPaneHeader();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        colegiaturas = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblMarca = new javax.swing.JTable();
        JPanelBusqueda = new javax.swing.JPanel();
        codigoa = new elaprendiz.gui.textField.TextField();
        jLabel16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        nombrealumno = new elaprendiz.gui.textField.TextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        beca = new elaprendiz.gui.textField.TextField();
        estado = new javax.swing.JLabel();
        inicioalumno = new com.toedter.calendar.JDateChooser();
        pnlPaginador = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        buttonAction1 = new elaprendiz.gui.button.ButtonAction();
        buttonAction2 = new elaprendiz.gui.button.ButtonAction();
        JPanelRecibo = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        codigo3 = new elaprendiz.gui.textField.TextField();
        jLabel22 = new javax.swing.JLabel();
        clockDigital2 = new elaprendiz.gui.varios.ClockDigital();
        jLabel23 = new javax.swing.JLabel();
        fechapago = new com.toedter.calendar.JDateChooser();
        pnlPaginador1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();

        Nuevo_Profesor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/profesor.png"))); // NOI18N
        Nuevo_Profesor.setText("Nuevo Profesor");
        Nuevo_Profesor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nuevo_ProfesorActionPerformed(evt);
            }
        });
        popupprofesor.add(Nuevo_Profesor);

        Actualizar_Profesor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/update.png"))); // NOI18N
        Actualizar_Profesor.setText("Actualizar Combo");
        Actualizar_Profesor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Actualizar_ProfesorActionPerformed(evt);
            }
        });
        popupprofesor.add(Actualizar_Profesor);

        Nueva_Carrera.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/carrera.png"))); // NOI18N
        Nueva_Carrera.setText("Nueva Carrera");
        Nueva_Carrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nueva_CarreraActionPerformed(evt);
            }
        });
        popupcarrera.add(Nueva_Carrera);

        Actualizar_Carrera.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/update.png"))); // NOI18N
        Actualizar_Carrera.setText("Actualizar Combo");
        Actualizar_Carrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Actualizar_CarreraActionPerformed(evt);
            }
        });
        popupcarrera.add(Actualizar_Carrera);

        Nueva_Matricula.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/profesor.png"))); // NOI18N
        Nueva_Matricula.setText("Nuevo Profesor");
        Nueva_Matricula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nueva_MatriculaActionPerformed(evt);
            }
        });
        popuppromatricula.add(Nueva_Matricula);

        Actualiza.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/update.png"))); // NOI18N
        Actualiza.setText("Actualizar Combo");
        Actualiza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActualizaActionPerformed(evt);
            }
        });
        popuppromatricula.add(Actualiza);

        setBackground(new java.awt.Color(0, 0, 0));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setForeground(new java.awt.Color(0, 0, 0));
        setIconifiable(true);
        setTitle("Registro de Pagos");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setName("pagos"); // NOI18N
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntModificar, gridBagConstraints);

        bntGuardar.setBackground(new java.awt.Color(51, 153, 255));
        bntGuardar.setMnemonic(KeyEvent.VK_G);
        bntGuardar.setText("Guardar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntGuardar, gridBagConstraints);

        bntEliminar.setBackground(new java.awt.Color(51, 153, 255));
        bntEliminar.setMnemonic(KeyEvent.VK_E);
        bntEliminar.setText("Eliminar");
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
        pnlActionButtons.setBounds(0, 580, 880, 50);

        JPanelCampos.setBackground(java.awt.SystemColor.activeCaption);
        JPanelCampos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        JPanelCampos.setForeground(new java.awt.Color(204, 204, 204));
        JPanelCampos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        JPanelCampos.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Horario A:");
        JPanelCampos.add(jLabel10);
        jLabel10.setBounds(730, 10, 110, 20);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Profesor:");
        JPanelCampos.add(jLabel3);
        jLabel3.setBounds(320, 10, 250, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Fecha Fin:");
        JPanelCampos.add(jLabel6);
        jLabel6.setBounds(730, 60, 110, 20);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Horario De:");
        JPanelCampos.add(jLabel13);
        jLabel13.setBounds(600, 10, 100, 20);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Fecha Inicio:");
        JPanelCampos.add(jLabel9);
        jLabel9.setBounds(600, 60, 110, 20);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Carrera:");
        JPanelCampos.add(jLabel5);
        jLabel5.setBounds(320, 60, 250, 20);

        cGrupo.setEditable(true);
        cGrupo.setName("Dia"); // NOI18N
        JPanelCampos.add(cGrupo);
        cGrupo.setBounds(110, 70, 150, 24);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Grupo:");
        JPanelCampos.add(jLabel7);
        jLabel7.setBounds(40, 70, 60, 27);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Inscripción Q.");
        JPanelCampos.add(jLabel24);
        jLabel24.setBounds(600, 110, 110, 20);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Colegiatura Q.");
        JPanelCampos.add(jLabel18);
        jLabel18.setBounds(730, 110, 110, 20);

        carrera.setEditable(false);
        carrera.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        carrera.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        carrera.setName("codigo"); // NOI18N
        carrera.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(carrera);
        carrera.setBounds(320, 80, 250, 24);

        profesor.setEditable(false);
        profesor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        profesor.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        profesor.setName("codigo"); // NOI18N
        profesor.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(profesor);
        profesor.setBounds(320, 30, 250, 24);

        inscripcion.setEditable(false);
        inscripcion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        inscripcion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inscripcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        inscripcion.setName("inscripcion"); // NOI18N
        inscripcion.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(inscripcion);
        inscripcion.setBounds(600, 130, 110, 24);

        colegiatura.setEditable(false);
        colegiatura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        colegiatura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        colegiatura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        colegiatura.setName("colegiatura"); // NOI18N
        colegiatura.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(colegiatura);
        colegiatura.setBounds(730, 130, 110, 24);

        horaa.setEditable(false);
        horaa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        horaa.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        horaa.setName("codigo"); // NOI18N
        horaa.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(horaa);
        horaa.setBounds(730, 30, 110, 24);

        horade.setEditable(false);
        horade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        horade.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        horade.setName("codigo"); // NOI18N
        horade.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(horade);
        horade.setBounds(600, 30, 110, 24);

        fechainicio.setEditable(false);
        fechainicio.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fechainicio.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        fechainicio.setName("codigo"); // NOI18N
        fechainicio.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(fechainicio);
        fechainicio.setBounds(600, 80, 110, 24);

        fechafin.setEditable(false);
        fechafin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fechafin.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        fechafin.setName("codigo"); // NOI18N
        fechafin.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(fechafin);
        fechafin.setBounds(730, 80, 110, 24);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setText("Día:");
        JPanelCampos.add(jLabel12);
        jLabel12.setBounds(320, 110, 250, 20);

        dia.setEditable(false);
        dia.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        dia.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        dia.setName("codigo"); // NOI18N
        dia.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(dia);
        dia.setBounds(320, 130, 250, 24);

        panelImage.add(JPanelCampos);
        JPanelCampos.setBounds(0, 190, 880, 170);

        JPanelTable.setOpaque(false);
        JPanelTable.setPreferredSize(new java.awt.Dimension(786, 402));
        JPanelTable.setLayout(new java.awt.BorderLayout());

        tbPane.setOpaque(true);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        colegiaturas.setForeground(new java.awt.Color(51, 51, 51));
        colegiaturas.setModel(model = new DefaultTableModel(null, titulos)
            {
                @Override
                public boolean isCellEditable(int row, int column) {
                    if(column==2){
                        return true;
                    }else{
                        return false;}
                }
            });
            colegiaturas.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            colegiaturas.setFocusCycleRoot(true);
            colegiaturas.setGridColor(new java.awt.Color(51, 51, 255));
            colegiaturas.setRowHeight(22);
            colegiaturas.setSelectionBackground(java.awt.SystemColor.activeCaption);
            colegiaturas.setSurrendersFocusOnKeystroke(true);
            colegiaturas.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    colegiaturasMouseClicked(evt);
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    colegiaturasMouseClicked1(evt);
                }
            });
            colegiaturas.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    colegiaturasKeyPressed(evt);
                }
            });
            jScrollPane4.setViewportView(colegiaturas);

            jPanel3.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 756, 150));

            tbPane.addTab("Colegiatura", jPanel3);

            jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

            jScrollPane2.setOpaque(false);

            tblMarca.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null}
                },
                new String [] {
                    "Title 1", "Title 2", "Title 3", "Title 4"
                }
            ));
            tblMarca.setName("tblMarca"); // NOI18N
            tblMarca.setOpaque(false);
            jScrollPane2.setViewportView(tblMarca);

            jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 756, 150));

            tbPane.addTab("Otros Pagos", jPanel4);

            JPanelTable.add(tbPane, java.awt.BorderLayout.CENTER);

            panelImage.add(JPanelTable);
            JPanelTable.setBounds(0, 390, 760, 190);

            JPanelBusqueda.setBackground(java.awt.SystemColor.inactiveCaption);
            JPanelBusqueda.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            JPanelBusqueda.setLayout(null);

            codigoa.setPreferredSize(new java.awt.Dimension(250, 27));
            codigoa.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    codigoaActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(codigoa);
            codigoa.setBounds(120, 10, 97, 24);

            jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel16.setText("Codigo:");
            JPanelBusqueda.add(jLabel16);
            jLabel16.setBounds(10, 10, 100, 24);

            jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/buscar.png"))); // NOI18N
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(jButton1);
            jButton1.setBounds(220, 10, 20, 27);

            nombrealumno.setEditable(false);
            nombrealumno.setPreferredSize(new java.awt.Dimension(250, 27));
            JPanelBusqueda.add(nombrealumno);
            nombrealumno.setBounds(440, 10, 370, 24);

            jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel19.setText("Alumno:");
            JPanelBusqueda.add(jLabel19);
            jLabel19.setBounds(310, 10, 120, 24);

            jLabel26.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel26.setText("Fecha Inicio:");
            JPanelBusqueda.add(jLabel26);
            jLabel26.setBounds(10, 50, 100, 24);

            jLabel25.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel25.setText("Becado: Q.");
            JPanelBusqueda.add(jLabel25);
            jLabel25.setBounds(340, 50, 90, 27);

            beca.setHorizontalAlignment(javax.swing.JTextField.LEFT);
            beca.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
            beca.setName("codigo"); // NOI18N
            beca.setPreferredSize(new java.awt.Dimension(120, 21));
            JPanelBusqueda.add(beca);
            beca.setBounds(440, 50, 130, 24);

            estado.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
            estado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            JPanelBusqueda.add(estado);
            estado.setBounds(700, 50, 110, 27);

            inicioalumno.setDate(null);
            inicioalumno.setDateFormatString("dd/MM/yyyy");
            inicioalumno.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            inicioalumno.setMaxSelectableDate(new java.util.Date(3093496470100000L));
            inicioalumno.setMinSelectableDate(new java.util.Date(-62135744300000L));
            inicioalumno.setPreferredSize(new java.awt.Dimension(120, 22));
            JPanelBusqueda.add(inicioalumno);
            inicioalumno.setBounds(120, 50, 120, 24);

            panelImage.add(JPanelBusqueda);
            JPanelBusqueda.setBounds(0, 110, 880, 80);

            pnlPaginador.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
            pnlPaginador.setPreferredSize(new java.awt.Dimension(786, 40));
            pnlPaginador.setLayout(new java.awt.GridBagLayout());

            jLabel8.setFont(new java.awt.Font("Script MT Bold", 1, 26)); // NOI18N
            jLabel8.setForeground(new java.awt.Color(0, 102, 102));
            jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel8.setText("  Detalle de pagos  ");
            jLabel8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
            pnlPaginador.add(jLabel8, new java.awt.GridBagConstraints());

            panelImage.add(pnlPaginador);
            pnlPaginador.setBounds(0, 360, 880, 30);

            jPanel1.setLayout(null);

            buttonAction1.setText("Colegiatura");
            buttonAction1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jPanel1.add(buttonAction1);
            buttonAction1.setBounds(10, 50, 90, 35);

            buttonAction2.setText("Otros Pagos");
            buttonAction2.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jPanel1.add(buttonAction2);
            buttonAction2.setBounds(10, 120, 90, 35);

            panelImage.add(jPanel1);
            jPanel1.setBounds(760, 390, 120, 190);

            JPanelRecibo.setBackground(java.awt.SystemColor.activeCaption);
            JPanelRecibo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            JPanelRecibo.setLayout(null);

            jLabel21.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel21.setText("Hora");
            JPanelRecibo.add(jLabel21);
            jLabel21.setBounds(270, 10, 100, 19);

            codigo3.setEditable(false);
            codigo3.setHorizontalAlignment(javax.swing.JTextField.LEFT);
            codigo3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
            codigo3.setName("codigo"); // NOI18N
            codigo3.setPreferredSize(new java.awt.Dimension(120, 21));
            JPanelRecibo.add(codigo3);
            codigo3.setBounds(700, 30, 110, 27);

            jLabel22.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel22.setText("No. Recibo");
            JPanelRecibo.add(jLabel22);
            jLabel22.setBounds(700, 10, 110, 19);

            clockDigital2.setForeground(java.awt.Color.blue);
            clockDigital2.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 15)); // NOI18N
            JPanelRecibo.add(clockDigital2);
            clockDigital2.setBounds(270, 30, 100, 27);

            jLabel23.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel23.setText("Fecha");
            JPanelRecibo.add(jLabel23);
            jLabel23.setBounds(120, 10, 120, 19);

            fechapago.setDate(Calendar.getInstance().getTime());
            fechapago.setDateFormatString("dd/MM/yyyy");
            fechapago.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            fechapago.setMaxSelectableDate(new java.util.Date(3093496470100000L));
            fechapago.setMinSelectableDate(new java.util.Date(-62135744300000L));
            fechapago.setPreferredSize(new java.awt.Dimension(120, 22));
            JPanelRecibo.add(fechapago);
            fechapago.setBounds(120, 30, 120, 27);

            panelImage.add(JPanelRecibo);
            JPanelRecibo.setBounds(0, 40, 880, 70);

            pnlPaginador1.setBackground(new java.awt.Color(57, 104, 163));
            pnlPaginador1.setPreferredSize(new java.awt.Dimension(786, 40));
            pnlPaginador1.setLayout(new java.awt.GridBagLayout());

            jLabel11.setFont(new java.awt.Font("Script MT Bold", 1, 32)); // NOI18N
            jLabel11.setForeground(new java.awt.Color(255, 255, 255));
            jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/money.png"))); // NOI18N
            jLabel11.setText("<--Registro de pagos-->");
            pnlPaginador1.add(jLabel11, new java.awt.GridBagConstraints());

            panelImage.add(pnlPaginador1);
            pnlPaginador1.setBounds(0, 0, 880, 40);

            getContentPane().add(panelImage, java.awt.BorderLayout.CENTER);

            getAccessibleContext().setAccessibleName("Profesores");

            setBounds(0, 0, 890, 662);
        }// </editor-fold>//GEN-END:initComponents

    private void bntNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntNuevoActionPerformed
        // TODO add your handling code here:
        this.bntGuardar.setEnabled(true);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(false);
        //codigo.requestFocus();

    }//GEN-LAST:event_bntNuevoActionPerformed

    private void bntSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSalirActionPerformed
        cerrarVentana();
    }//GEN-LAST:event_bntSalirActionPerformed

    private void bntCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCancelarActionPerformed
        // TODO add your handling code here:
        removejtable();
        codigoa.setText("");
        codigoa.requestFocus();
        profesor.setText("");
        carrera.setText("");
        horade.setText("");
        horaa.setText("");
        fechainicio.setText("");
        fechafin.setText("");
        inscripcion.setValue(null);
        colegiatura.setValue(null);
        nombrealumno.setText("");
        beca.setText("");
        inicioalumno.setDate(null);
        dia.setText("");
        cGrupo.setSelectedIndex(-1);
        codigoa.requestFocus();
    }//GEN-LAST:event_bntCancelarActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:
        cerrarVentana();
    }//GEN-LAST:event_formInternalFrameClosing

    private void Actualizar_ProfesorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Actualizar_ProfesorActionPerformed
        // TODO add your handling code here:
        //llenarcomboprofesor();
    }//GEN-LAST:event_Actualizar_ProfesorActionPerformed

    private void Nuevo_ProfesorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nuevo_ProfesorActionPerformed
        // TODO add your handling code here:
        Profesor frmProfesor = new Profesor();
        if (frmProfesor == null) {
            frmProfesor = new Profesor();
        }
        adminInternalFrame(dp, frmProfesor);
    }//GEN-LAST:event_Nuevo_ProfesorActionPerformed

    private void Nueva_CarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nueva_CarreraActionPerformed
        // TODO add your handling code here:
        Carrera frmCarrera = new Carrera();
        if (frmCarrera == null) {
            frmCarrera = new Carrera();
        }
        adminInternalFrame(dp, frmCarrera);
    }//GEN-LAST:event_Nueva_CarreraActionPerformed

    private void Actualizar_CarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Actualizar_CarreraActionPerformed
        // TODO add your handling code here:
        //llenarcombogrupo();
    }//GEN-LAST:event_Actualizar_CarreraActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        BuscarAlumno frmBuscarAlumno = new BuscarAlumno();
        if (frmBuscarAlumno == null) {
            frmBuscarAlumno = new BuscarAlumno();
        }
        adminInternalFrame(dp, frmBuscarAlumno);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void codigoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codigoaActionPerformed
        // TODO add your handling code here:

        balumnocodigo(codigoa.getText());

    }//GEN-LAST:event_codigoaActionPerformed

    private void Nueva_MatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nueva_MatriculaActionPerformed
        // TODO add your handling code here:
        Alumno frmAlumno = new Alumno();
        if (frmAlumno == null) {
            frmAlumno = new Alumno();
        }
        adminInternalFrame(dp, frmAlumno);

    }//GEN-LAST:event_Nueva_MatriculaActionPerformed

    private void ActualizaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActualizaActionPerformed
        // TODO add your handling code here:
        balumnocodigo(codigoa.getText());
    }//GEN-LAST:event_ActualizaActionPerformed

    private void colegiaturasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_colegiaturasKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_colegiaturasKeyPressed

    private void colegiaturasMouseClicked1(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_colegiaturasMouseClicked1
        // TODO add your handling code here:
    }//GEN-LAST:event_colegiaturasMouseClicked1

    private void colegiaturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_colegiaturasMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_colegiaturasMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Actualiza;
    private javax.swing.JMenuItem Actualizar_Carrera;
    private javax.swing.JMenuItem Actualizar_Profesor;
    private javax.swing.JPanel JPanelBusqueda;
    private javax.swing.JPanel JPanelCampos;
    private javax.swing.JPanel JPanelRecibo;
    private javax.swing.JPanel JPanelTable;
    private javax.swing.JMenuItem Nueva_Carrera;
    private javax.swing.JMenuItem Nueva_Matricula;
    private javax.swing.JMenuItem Nuevo_Profesor;
    public static elaprendiz.gui.textField.TextField beca;
    private elaprendiz.gui.button.ButtonRect bntCancelar;
    private elaprendiz.gui.button.ButtonRect bntEliminar;
    private elaprendiz.gui.button.ButtonRect bntGuardar;
    private elaprendiz.gui.button.ButtonRect bntModificar;
    private elaprendiz.gui.button.ButtonRect bntNuevo;
    private elaprendiz.gui.button.ButtonRect bntSalir;
    private elaprendiz.gui.button.ButtonAction buttonAction1;
    private elaprendiz.gui.button.ButtonAction buttonAction2;
    public static javax.swing.JComboBox cGrupo;
    private elaprendiz.gui.textField.TextField carrera;
    private elaprendiz.gui.varios.ClockDigital clockDigital2;
    private elaprendiz.gui.textField.TextField codigo3;
    public static elaprendiz.gui.textField.TextField codigoa;
    private javax.swing.JFormattedTextField colegiatura;
    private javax.swing.JTable colegiaturas;
    private elaprendiz.gui.textField.TextField dia;
    public static javax.swing.JLabel estado;
    private elaprendiz.gui.textField.TextField fechafin;
    private elaprendiz.gui.textField.TextField fechainicio;
    public static com.toedter.calendar.JDateChooser fechapago;
    private elaprendiz.gui.textField.TextField horaa;
    private elaprendiz.gui.textField.TextField horade;
    public static com.toedter.calendar.JDateChooser inicioalumno;
    private javax.swing.JFormattedTextField inscripcion;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    public static elaprendiz.gui.textField.TextField nombrealumno;
    private elaprendiz.gui.panel.PanelImage panelImage;
    private javax.swing.JPanel pnlActionButtons;
    private javax.swing.JPanel pnlPaginador;
    private javax.swing.JPanel pnlPaginador1;
    private javax.swing.JPopupMenu popupcarrera;
    private javax.swing.JPopupMenu popupprofesor;
    private javax.swing.JPopupMenu popuppromatricula;
    private elaprendiz.gui.textField.TextField profesor;
    private elaprendiz.gui.panel.TabbedPaneHeader tbPane;
    private javax.swing.JTable tblMarca;
    // End of variables declaration//GEN-END:variables
}
