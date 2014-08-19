/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.https://www.youtube.com/watch?v=ICF-RldvSIo
 */
package Capa_Presentacion;

import Capa_Datos.AccesoDatos;
import Capa_Negocio.FiltroCampos;
import Capa_Negocio.FormatoDecimal;
import Capa_Negocio.FormatoFecha;
import Capa_Negocio.Peticiones;
import Capa_Negocio.TipoFiltro;
import Capa_Negocio.Utilidades;
import Recursos.mProfesor;
import java.awt.Component;
import java.awt.event.ActionEvent;
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
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author GLARA
 */
public class Horario extends javax.swing.JInternalFrame {

    /*El modelo se define en : Jtable-->propiedades-->model--> <User Code> */
    DefaultTableModel model;
    DefaultComboBoxModel modelCombo;
    String[] titulos = {"Codigo", "Descripción", "Dia", "Profesor", "Hora De", "Hora A", "Fecha Inicio", "Estado"};//Titulos para Jtabla
    /*Se hace una instancia de la clase que recibira las peticiones de esta capa de aplicación*/
    Peticiones peticiones = new Peticiones();
    public Hashtable<String, String> hashProfesor = new Hashtable<>();

    /*Se hace una instancia de la clase que recibira las peticiones de mensages de la capa de aplicación*/
    //public static JOptionMessage msg = new JOptionMessage();
    /**
     * Creates new form Cliente
     */
    public Horario() {
        initComponents();
        setFiltroTexto();
        addEscapeKey();
        llenarcombo();
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
            rbDia.setSelected(false);
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
        while (horarios.getRowCount() != 0) {
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
        String[] campos = {"nombre", "apellido", "idcatedratico"};
        String[] condiciones = {"estado"};
        String[] Id = {Dato};
        profesor.removeAllItems();
        Component cmps = profesor;
        getRegistroCombo("profesor", campos, condiciones, Id);

    }

    /*El metodo llenarcombo() envia los parametros para la consulta a la BD y el medoto
     *getRegistroCombo() se encarga de enviarlos a la capa de AccesoDatos.getRegistros()
     *quiern devolcera un ResultSet para luego obtener los valores y agregarlos al JConboBox
     *y a una Hashtable que nos servira para obtener el id y seleccionar valores.
     */
    public void getRegistroCombo(String tabla, String[] campos, String[] campocondicion, String[] condicionid) {
        try {
            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            rs = ac.getRegistros(tabla, campos, campocondicion, condicionid, "");

            int cantcampos = campos.length;
            if (rs != null) {

                DefaultComboBoxModel modeloComboBox;
                modeloComboBox = new DefaultComboBoxModel();
                profesor.setModel(modeloComboBox);

                modeloComboBox.addElement(new mProfesor("", "0"));
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    int count = 0;
                    rs.beforeFirst();//regresa el puntero al primer registro
                    Object[] fila = new Object[cantcampos];
                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        count++;
                        modeloComboBox.addElement(new mProfesor(rs.getString(1) + " " + rs.getString(2), "" + rs.getInt(3)));
                        hashProfesor.put(rs.getString(1) + " " + rs.getString(2), "" + count);
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

        TipoFiltro.setFiltraEntrada(codigo.getDocument(), FiltroCampos.NUM_LETRAS, 45, false);
        TipoFiltro.setFiltraEntrada(descripcion.getDocument(), FiltroCampos.NUM_LETRAS, 60, true);
        //TipoFiltro.setFiltraEntrada(dia.getDocument(), FiltroCampos.SOLO_LETRAS, 45, false);
        //TipoFiltro.setFiltraEntrada(profesor.getDocument(), FiltroCampos.NUM_LETRAS, 200, true);
        TipoFiltro.setFiltraEntrada(inscripcion.getDocument(), FiltroCampos.SOLO_NUMEROS, 12, true);
        TipoFiltro.setFiltraEntrada(colegiatura.getDocument(), FiltroCampos.SOLO_NUMEROS, 12, false);
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
        //String[] titulos = {"Codigo", "Descripción", "Dia", "Profesor", "Hora De","Hora A", "Fecha Inicio","Estado"};
        String[] campos = {"codigo", "descripcion", "dia", "maestro_idcatedratico", "horariode", "horarioa", "fechainicio", "estado"};
        String[] condiciones = {"codigo"};
        String[] Id = {Dato};

        if (this.rbCodigo.isSelected()) {
            if (!Dato.isEmpty()) {
                removejtable();
                Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                Utilidades.esObligatorio(this.JPanelCampos, false);
                model = peticiones.getRegistroPorPks(model, "horario", campos, condiciones, Id);
            } else {
                JOptionPane.showInternalMessageDialog(this, "Debe ingresar un codigo para la busqueda");
            }
        }
        if (this.rbNombres.isSelected()) {
            removejtable();
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            model = peticiones.getRegistroPorLike(model, "horario", campos, "descripcion", Dato);
        }
        if (this.rbDia.isSelected()) {
            removejtable();
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            model = peticiones.getRegistroPorLike(model, "horario", campos, "dia", Dato);
        }
        Utilidades.ajustarAnchoColumnas(horarios);
    }

    /* Este metodo  consulta en la BD el codigo de la fila seleccionada y llena los componentes
     * de la parte superior del formulario con los datos obtenidos en la capa de Negocio getRegistroSeleccionado().
     * 
     * @return 
     */
    private void filaseleccionada() {

        int fila = horarios.getSelectedRow();
        String[] cond = {"horario.codigo"};
        String[] id = {(String) horarios.getValueAt(fila, 0)};
        String inner = " INNER JOIN profesor on horario.maestro_idcatedratico=profesor.idcatedratico";

        if (horarios.getValueAt(fila, 0) != null) {

            String conct = "concat(profesor.nombre,' ',profesor.apellido)";
            String[] campos = {"horario.codigo", "horario.descripcion", "horario.dia", conct, "horario.horariode", "horario.horarioa", "horario.fechainicio", "horario.montoinscripcion", "horario.colegiatura", "horario.estado"};
            llenarcombo(); // borra los items de comboBox y lo vuelve a llenar
            Component[] cmps = {codigo, descripcion, dia, profesor, horade, horaa, fechainicio, inscripcion, colegiatura, estado};
            Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");

            peticiones.getRegistroSeleccionado(cmps, "horario", campos, cond, id, inner, hashProfesor);

            this.bntGuardar.setEnabled(false);
            this.bntModificar.setEnabled(true);
            this.bntEliminar.setEnabled(true);
            this.bntNuevo.setEnabled(false);
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
        jLabel10 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        codigo = new elaprendiz.gui.textField.TextField();
        descripcion = new elaprendiz.gui.textField.TextField();
        fechainicio = new com.toedter.calendar.JDateChooser();
        estado = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        //http://www.forosdelweb.com/f45/formato-horade-559125/
        //https://www.youtube.com/watch?v=Uh77miF-YMY
        Date date = new Date();
        SpinnerDateModel sm =
        new SpinnerDateModel(date, null, null, Calendar.HOUR_OF_DAY);
        horade = new javax.swing.JSpinner(sm);
        //http://www.forosdelweb.com/f45/formato-horade-559125/
        //https://www.youtube.com/watch?v=Uh77miF-YMY
        Date date2 = new Date();
        SpinnerDateModel sm2 =
        new SpinnerDateModel(date2, null, null, Calendar.HOUR_OF_DAY);
        horaa = new javax.swing.JSpinner(sm2);
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        inscripcion = new javax.swing.JFormattedTextField();
        colegiatura = new javax.swing.JFormattedTextField();
        profesor = new javax.swing.JComboBox();
        dia = new javax.swing.JComboBox();
        JPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        horarios = new javax.swing.JTable();
        JPanelBusqueda = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        busqueda = new elaprendiz.gui.textField.TextField();
        rbCodigo = new javax.swing.JRadioButton();
        rbNombres = new javax.swing.JRadioButton();
        rbDia = new javax.swing.JRadioButton();
        pnlPaginador = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 0, 0));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setForeground(new java.awt.Color(0, 0, 0));
        setIconifiable(true);
        setTitle("Horarios");
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

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("A:");
        JPanelCampos.add(jLabel10);
        jLabel10.setBounds(300, 150, 20, 17);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Descripción:");
        JPanelCampos.add(jLabel1);
        jLabel1.setBounds(70, 60, 100, 20);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Día:");
        JPanelCampos.add(jLabel2);
        jLabel2.setBounds(90, 90, 80, 20);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Profesor:");
        JPanelCampos.add(jLabel3);
        jLabel3.setBounds(90, 120, 80, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Fecha Inicio:");
        JPanelCampos.add(jLabel6);
        jLabel6.setBounds(450, 30, 150, 21);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Colegiatura:");
        JPanelCampos.add(jLabel9);
        jLabel9.setBounds(490, 90, 110, 20);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Estado:");
        JPanelCampos.add(jLabel4);
        jLabel4.setBounds(490, 120, 110, 20);

        codigo.setEditable(false);
        codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        codigo.setName("codigo"); // NOI18N
        codigo.setNextFocusableComponent(descripcion);
        codigo.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(codigo);
        codigo.setBounds(180, 30, 130, 21);

        descripcion.setEditable(false);
        descripcion.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        descripcion.setName("descripcion"); // NOI18N
        descripcion.setNextFocusableComponent(dia);
        JPanelCampos.add(descripcion);
        descripcion.setBounds(180, 60, 250, 21);

        fechainicio.setDate(Calendar.getInstance().getTime());
        fechainicio.setDateFormatString("dd/MM/yyyy");
        fechainicio.setEnabled(false);
        fechainicio.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fechainicio.setMaxSelectableDate(new java.util.Date(3093496470100000L));
        fechainicio.setMinSelectableDate(new java.util.Date(-62135744300000L));
        fechainicio.setNextFocusableComponent(inscripcion);
        fechainicio.setPreferredSize(new java.awt.Dimension(120, 22));
        JPanelCampos.add(fechainicio);
        fechainicio.setBounds(610, 30, 130, 21);

        estado.setBackground(new java.awt.Color(51, 153, 255));
        estado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        estado.setForeground(new java.awt.Color(255, 255, 255));
        estado.setText("Activo");
        estado.setEnabled(false);
        estado.setName("JRadioButton"); // NOI18N
        JPanelCampos.add(estado);
        estado.setBounds(610, 120, 130, 21);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Inscripción:");
        JPanelCampos.add(jLabel11);
        jLabel11.setBounds(464, 60, 140, 17);

        JSpinner.DateEditor de = new JSpinner.DateEditor(horade, "hh:mm a");
        horade.setEditor(de);
        horade.setEnabled(false);
        horade.setNextFocusableComponent(horaa);
        JPanelCampos.add(horade);
        horade.setBounds(180, 150, 100, 21);

        JSpinner.DateEditor de2 = new JSpinner.DateEditor(horaa, "hh:mm a");
        horaa.setEditor(de2);
        horaa.setEnabled(false);
        horaa.setNextFocusableComponent(fechainicio);
        JPanelCampos.add(horaa);
        horaa.setBounds(330, 150, 100, 21);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Codigo:");
        JPanelCampos.add(jLabel12);
        jLabel12.setBounds(90, 30, 80, 17);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText("Horario De:");
        JPanelCampos.add(jLabel13);
        jLabel13.setBounds(80, 150, 90, 17);

        inscripcion.setEditable(false);
        inscripcion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        inscripcion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inscripcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        inscripcion.setName("inscripcion"); // NOI18N
        inscripcion.setNextFocusableComponent(colegiatura);
        inscripcion.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(inscripcion);
        inscripcion.setBounds(610, 60, 130, 21);

        colegiatura.setEditable(false);
        colegiatura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        colegiatura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        colegiatura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        colegiatura.setName("precioalmayor"); // NOI18N
        colegiatura.setNextFocusableComponent(estado);
        colegiatura.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(colegiatura);
        colegiatura.setBounds(610, 90, 130, 23);

        profesor.setModel(modelCombo = new DefaultComboBoxModel());
        profesor.setNextFocusableComponent(horade);
        JPanelCampos.add(profesor);
        profesor.setBounds(180, 120, 250, 21);

        dia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo", "Mixto" }));
        dia.setNextFocusableComponent(profesor);
        JPanelCampos.add(dia);
        dia.setBounds(180, 90, 250, 21);

        panelImage.add(JPanelCampos);
        JPanelCampos.setBounds(0, 40, 880, 190);

        JPanelTable.setOpaque(false);
        JPanelTable.setPreferredSize(new java.awt.Dimension(786, 402));
        JPanelTable.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        horarios.setForeground(new java.awt.Color(51, 51, 51));
        horarios.setModel(model = new DefaultTableModel(null, titulos)
            {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            horarios.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            horarios.setFocusCycleRoot(true);
            horarios.setGridColor(new java.awt.Color(51, 51, 255));
            horarios.setRowHeight(22);
            horarios.setSelectionBackground(java.awt.SystemColor.activeCaption);
            horarios.setSurrendersFocusOnKeystroke(true);
            horarios.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    horariosMouseClicked(evt);
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    horariosMouseClicked(evt);
                }
            });
            horarios.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    horariosKeyPressed(evt);
                }
            });
            jScrollPane1.setViewportView(horarios);
            horarios.getAccessibleContext().setAccessibleName("");

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
            rbCodigo.setBounds(270, 40, 80, 25);

            rbNombres.setBackground(new java.awt.Color(51, 153, 255));
            rbNombres.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            rbNombres.setForeground(new java.awt.Color(255, 255, 255));
            rbNombres.setSelected(true);
            rbNombres.setText("Descripción");
            rbNombres.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rbNombresActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(rbNombres);
            rbNombres.setBounds(370, 40, 110, 25);

            rbDia.setBackground(new java.awt.Color(51, 153, 255));
            rbDia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            rbDia.setForeground(new java.awt.Color(255, 255, 255));
            rbDia.setText("Día");
            rbDia.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rbDiaActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(rbDia);
            rbDia.setBounds(500, 40, 90, 25);

            panelImage.add(JPanelBusqueda);
            JPanelBusqueda.setBounds(0, 230, 880, 70);

            pnlPaginador.setBackground(new java.awt.Color(57, 104, 163));
            pnlPaginador.setPreferredSize(new java.awt.Dimension(786, 40));
            pnlPaginador.setLayout(new java.awt.GridBagLayout());

            jLabel8.setFont(new java.awt.Font("Script MT Bold", 1, 32)); // NOI18N
            jLabel8.setForeground(new java.awt.Color(255, 255, 255));
            jLabel8.setText("<--Horarios-->");
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
        this.bntGuardar.setEnabled(true);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(false);
        codigo.requestFocus();

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
            String nombreTabla = "horario";
            String campos = "codigo, descripcion, dia, maestro_idcatedratico, horariode, horarioa, fechainicio, montoinscripcion, colegiatura, estado";
            String fechaini = FormatoFecha.getFormato(fechainicio.getCalendar().getTime(), FormatoFecha.A_M_D);

            //Para obtener el id en la base de datos
            mProfesor prof = (mProfesor) profesor.getSelectedItem();
            String idprof = prof.getID();

            int estad = 0;
            if (this.estado.isSelected()) {
                estad = 1;
            }
            Object[] valores = {codigo.getText(), descripcion.getText(), dia.getSelectedItem(), /*profesor.getText()*/ idprof,
                FormatoFecha.getTime(horade.getValue()), FormatoFecha.getTime(horaa.getValue()),
                fechaini, inscripcion.getText(), colegiatura.getText(), estad
            };

            seguardo = peticiones.guardarRegistros(nombreTabla, campos, valores);

            if (seguardo) {
                Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                MostrarDatos(busqueda.getText());
                busqueda.requestFocus();
                JOptionPane.showInternalMessageDialog(this, "El dato se ha Guardado Correctamente", "Guardar", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bntGuardarActionPerformed

    private void bntSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSalirActionPerformed
        cerrarVentana();
    }//GEN-LAST:event_bntSalirActionPerformed

    private void horariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_horariosMouseClicked
        // TODO add your handling code here:
        filaseleccionada();

    }//GEN-LAST:event_horariosMouseClicked

    private void bntEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntEliminarActionPerformed
        // TODO add your handling code here:

        int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Eliminar el Registro?", "Pregunta", 0);
        if (resp == 0) {

            int fila = horarios.getSelectedRow();
            String id = (String) "" + horarios.getValueAt(fila, 0);
            String nombreTabla = "horario", nomColumnaCambiar = "estado";
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

            String nomTabla = "horario";
            String columnaId = "codigo";
            int seguardo = 0;
            int fila = horarios.getSelectedRow();
            String id = (String) "" + horarios.getValueAt(fila, 0);
            String campos = "codigo, descripcion, dia, maestro_idcatedratico, horariode, horarioa, fechainicio, montoinscripcion, colegiatura, estado";
            String fechaini = FormatoFecha.getFormato(fechainicio.getCalendar().getTime(), FormatoFecha.A_M_D);

            mProfesor prof = (mProfesor) profesor.getSelectedItem();
            String idprof = prof.getID();

            int estad = 0;
            if (this.estado.isSelected()) {
                estad = 1;
            }
            Object[] valores = {codigo.getText(), descripcion.getText(), dia.getSelectedItem(), /*profesor.getText()*/ idprof,
                FormatoFecha.getTime(horade.getValue()), FormatoFecha.getTime(horaa.getValue()), fechaini, colegiatura.getText(), inscripcion.getText(), estad, id};
            seguardo = peticiones.actualizarRegistro(nomTabla, campos, valores, columnaId, id);
            if (seguardo == 1) {
                Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                MostrarDatos(busqueda.getText());
                busqueda.requestFocus();
                JOptionPane.showInternalMessageDialog(this, "El dato se ha Modificado Correctamente", "Modificar", JOptionPane.INFORMATION_MESSAGE);
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
        rbDia.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbCodigoActionPerformed

    private void rbNombresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNombresActionPerformed
        // TODO add your handling code here:
        rbCodigo.setSelected(false);
        rbDia.setSelected(false);
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

    private void horariosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_horariosKeyPressed
        // TODO add your handling code here:
        int key = evt.getKeyCode();
        if (key == java.awt.event.KeyEvent.VK_SPACE) {
            filaseleccionada();
        }
        if (key == java.awt.event.KeyEvent.VK_DOWN || key == java.awt.event.KeyEvent.VK_UP) {
            limpiar();
        }
    }//GEN-LAST:event_horariosKeyPressed

    private void rbDiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDiaActionPerformed
        // TODO add your handling code here:
        rbNombres.setSelected(false);
        rbCodigo.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbDiaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanelBusqueda;
    private javax.swing.JPanel JPanelCampos;
    private javax.swing.JPanel JPanelTable;
    private elaprendiz.gui.button.ButtonRect bntCancelar;
    private elaprendiz.gui.button.ButtonRect bntEliminar;
    private elaprendiz.gui.button.ButtonRect bntGuardar;
    private elaprendiz.gui.button.ButtonRect bntModificar;
    private elaprendiz.gui.button.ButtonRect bntNuevo;
    private elaprendiz.gui.button.ButtonRect bntSalir;
    private elaprendiz.gui.textField.TextField busqueda;
    private elaprendiz.gui.textField.TextField codigo;
    private javax.swing.JFormattedTextField colegiatura;
    private elaprendiz.gui.textField.TextField descripcion;
    private javax.swing.JComboBox dia;
    private javax.swing.JRadioButton estado;
    private com.toedter.calendar.JDateChooser fechainicio;
    private javax.swing.JSpinner horaa;
    private javax.swing.JSpinner horade;
    private javax.swing.JTable horarios;
    private javax.swing.JFormattedTextField inscripcion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private elaprendiz.gui.panel.PanelImage panelImage;
    private javax.swing.JPanel pnlActionButtons;
    private javax.swing.JPanel pnlPaginador;
    private javax.swing.JComboBox profesor;
    private javax.swing.JRadioButton rbCodigo;
    private javax.swing.JRadioButton rbDia;
    private javax.swing.JRadioButton rbNombres;
    // End of variables declaration//GEN-END:variables
}
