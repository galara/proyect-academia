/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.https://www.youtube.com/watch?v=ICF-RldvSIo
 */
package Capa_Presentacion;

import Capa_Datos.AccesoDatos;
import static Capa_Negocio.AddForms.adminInternalFrame;
import Capa_Negocio.FormatoDecimal;
import Capa_Negocio.Peticiones;
import Capa_Negocio.Utilidades;
import static Capa_Presentacion.Principal.dp;
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
import modelos.mGrupo;

/**
 *
 * @author GLARA
 */
public class Pagos extends javax.swing.JInternalFrame {

    /*El modelo se define en : Jtable-->propiedades-->model--> <User Code> */
    DefaultTableModel model;
    DefaultComboBoxModel modelCombo;
    String[] titulos = {"Codigo", "Descripción", "Dia", "Profesor", "Carrera", "Hora De", "Hora A", "Fecha Inicio", "Fecha Fin", "Alumnos", "Estado"};//Titulos para Jtabla
    /*Se hace una instancia de la clase que recibira las peticiones de esta capa de aplicación*/
    Peticiones peticiones = new Peticiones();
    public Hashtable<String, String> hashGrupo = new Hashtable<>();

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

        cDiia.addItemListener(
                (ItemEvent e) -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        selecciondia();
                    }
                });

        cGrupo.addItemListener(
                (ItemEvent e) -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        selecciongrupo();
                    }
                });
        
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
            codigo.setText("");
            codigo.requestFocus();
            this.dispose();
        }
    }

    /* La funcion de este metodo es limpiar y desabilitar campos que se encuentren en un contenedor
     * ejem: los JTextFiel de un panel, se envian a la capa de negocio "Utilidades.setEditableTexto()" 
     * para que este los limpie,habilite o desabilite dichos componentes */
    public void limpiar() {
        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
        Utilidades.setEditableTexto(this.JPanelRecibo, false, null, true, "");
    }

    /* Para no sobrecargar la memoria y hacer una instancia cada vez que actualizamos la JTable se hace una
     * sola instancia y lo unico que se hace antes de actualizar la JTable es limpiar el modelo y enviarle los
     * nuevos datos a mostrar en la JTable  */
    public void removejtable() {
        while (detallepagos.getRowCount() != 0) {
            model.removeRow(0);
        }
    }

    private void selecciondia() {
        System.out.print(cDiia.getSelectedIndex() + " " + cDiia.getSelectedItem() + "\n");

        if (cDiia.getSelectedIndex() == 0) {
            //grupo.removeAllItems();
        } else if (cDiia.getSelectedIndex() > 0) {
            String sdia = (String) cDiia.getSelectedItem();
            llenarcombogrupo(sdia);
        }
    }

    /*
     *Prepara los parametros para la consulta de datos que deseamos agregar al model del ComboBox
     *y se los envia a un metodo interno getRegistroCombo() 
     *
     */
    public void llenarcombogrupo(String dia) {
        //System.out.print("dia "+dia+" "+"\n");
        String Dato = "1";
        String[] campos = {"codigo", "descripcion", "idgrupo"};
        String[] condiciones = {"estado", "dia"};
        String[] Id = {Dato, dia};
        cGrupo.removeAllItems();
        //Component cmps = profesor;
        getRegistroCombo("grupo", campos, condiciones, Id);

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
            horaa.setText("");
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
                String[] campos = {conct, "carrera.descripcion", "DATE_FORMAT(grupo.horariode,'%h:%i %p')", "DATE_FORMAT(grupo.horarioa,'%h:%i %p')", "DATE_FORMAT(grupo.fechainicio,'%d-%m-%Y')", "DATE_FORMAT(grupo.fechafin,'%d-%m-%Y')", "grupo.inscripcion", "grupo.colegiatura"};

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

//    /*
//     *Prepara los parametros para la consulta de datos que deseamos agregar al model del ComboBox
//     *y se los envia a un metodo interno getRegistroCombo() 
//     *
//     */
//    public void llenarcombocarrera() {
//        String Dato = "1";
//        String[] campos = {"descripcion", "idcarrera"};
//        String[] condiciones = {"estado"};
//        String[] Id = {Dato};
//        //carrera.removeAllItems();
//        //Component cmps = carrera;
//        getRegistroCombocarrera("carrera", campos, condiciones, Id);
//
//    }
//
//    /*El metodo llenarcombo() envia los parametros para la consulta a la BD y el medoto
//     *getRegistroCombo() se encarga de enviarlos a la capa de AccesoDatos.getRegistros()
//     *quiern devolcera un ResultSet para luego obtener los valores y agregarlos al JConboBox
//     *y a una Hashtable que nos servira para obtener el id y seleccionar valores.
//     */
//    public void getRegistroCombocarrera(String tabla, String[] campos, String[] campocondicion, String[] condicionid) {
//        try {
//            ResultSet rs;
//            AccesoDatos ac = new AccesoDatos();
//
//            rs = ac.getRegistros(tabla, campos, campocondicion, condicionid, "");
//
//            int cantcampos = campos.length;
//            if (rs != null) {
//
//                DefaultComboBoxModel modeloComboBox;
//                modeloComboBox = new DefaultComboBoxModel();
//                //carrera.setModel(modeloComboBox);
//
//                modeloComboBox.addElement(new mCarrera("", "0"));
//                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
//                    int count = 0;
//                    rs.beforeFirst();//regresa el puntero al primer registro
//                    Object[] fila = new Object[cantcampos];
//                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
//                        count++;
//                        modeloComboBox.addElement(new mCarrera(rs.getString(1), "" + rs.getInt(2)));
//                        //hashCarrera.put(rs.getString(1), "" + count);
//                    }
//                }
//            } else {
//                JOptionPane.showMessageDialog(null, "No se encontraron datos para la busqueda", "Error", JOptionPane.INFORMATION_MESSAGE);
//            }
//            //rs.close();
//
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(null, "Ocurrio un Error :" + ex, "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

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
        //String[] titulos = {"Codigo", "Descripción", "Dia", "Profesor","Carrera", "Hora De", "Hora A", "Fecha Inicio","Fecha Fin", "Alumnos","Estado"};//Titulos para Jtabla
        String conct = "concat(profesor.nombre,' ',profesor.apellido)";
        String[] campos = {"grupo.codigo", "grupo.descripcion", "grupo.dia", conct, "carrera.descripcion", "DATE_FORMAT(grupo.horariode,'%h:%i %p')", "DATE_FORMAT(grupo.horarioa,'%h:%i %p')", "DATE_FORMAT(grupo.fechainicio,'%d-%m-%Y')", "DATE_FORMAT(grupo.fechafin,'%d-%m-%Y')", "grupo.cantalumnos", "grupo.estado"};
        //String[] campos = {"codigo", "descripcion", "dia", "horariode", "horarioa", "fechainicio", "estado"};
        String[] condiciones = {"grupo.codigo"};
        String[] Id = {Dato};
        String inner = " INNER JOIN profesor on grupo.profesor_idcatedratico=profesor.idcatedratico INNER JOIN carrera on grupo.carrera_idcarrera=carrera.idcarrera ";

//        if (this.rbCodigo.isSelected()) {
//            if (!Dato.isEmpty()) {
//                removejtable();
//                Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
//                Utilidades.esObligatorio(this.JPanelCampos, false);
//                model = peticiones.getRegistroPorPks(model, "grupo", campos, condiciones, Id, inner);
//            } else {
//                JOptionPane.showInternalMessageDialog(this, "Debe ingresar un codigo para la busqueda");
//            }
//        }
//        if (this.rbNombres.isSelected()) {
//            removejtable();
//            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
//            Utilidades.esObligatorio(this.JPanelCampos, false);
//            model = peticiones.getRegistroPorLike(model, "grupo", campos, "grupo.descripcion", Dato, inner);
//        }
//        if (this.rbDia.isSelected()) {
//            removejtable();
//            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
//            Utilidades.esObligatorio(this.JPanelCampos, false);
//            model = peticiones.getRegistroPorLike(model, "grupo", campos, "grupo.dia", Dato, inner);
//        }
        Utilidades.ajustarAnchoColumnas(detallepagos);
    }

//    /* Este metodo  consulta en la BD el codigo de la fila seleccionada y llena los componentes
//     * de la parte superior del formulario con los datos obtenidos en la capa de Negocio getRegistroSeleccionado().
//     * 
//     * @return 
//     */
//    private void filaseleccionada3() {
//
//        int fila = horarios.getSelectedRow();
//        String[] cond = {"grupo.codigo"};
//        String[] id = {(String) horarios.getValueAt(fila, 0)};
//        String inner = " INNER JOIN profesor on grupo.profesor_idcatedratico=profesor.idcatedratico INNER JOIN carrera on grupo.carrera_idcarrera=carrera.idcarrera ";
//        if (horarios.getValueAt(fila, 0) != null) {
//
//            String conct = "concat(profesor.nombre,' ',profesor.apellido)";
//            String[] campos = {"grupo.codigo", "grupo.descripcion", "grupo.dia", conct, "carrera.descripcion", "grupo.horariode", "grupo.horarioa", "grupo.fechainicio", "grupo.fechafin", "grupo.cantalumnos", "grupo.estado", "grupo.graduados"};
//            llenarcomboprofesor(); // borra los items de comboBox y lo vuelve a llenar
//            Component[] cmps = {codigo, descripcion, dia, profesor, carrera, horade, horaa, fechainicio, fechafin, cantalumnos, estado, graduados};
//            Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
//
//            peticiones.getRegistroSeleccionado(cmps, "grupo", campos, cond, id, inner, hashProfesor);
//
//            this.bntGuardar.setEnabled(false);
//            this.bntModificar.setEnabled(true);
//            this.bntEliminar.setEnabled(true);
//            this.bntNuevo.setEnabled(false);
//        }
//    }

    /* Este metodo  consulta en la BD el codigo de la fila seleccionada y llena los componentes
     * de la parte superior del formulario con los datos obtenidos en la capa de Negocio getRegistroSeleccionado().
     * 
     * @return 
     */
    private void filaseleccionada() {

        int fila = detallepagos.getSelectedRow();
        String[] cond = {"grupo.codigo"};
        String[] id = {(String) detallepagos.getValueAt(fila, 0)};
        String inner = " INNER JOIN profesor on grupo.profesor_idcatedratico=profesor.idcatedratico INNER JOIN carrera on grupo.carrera_idcarrera=carrera.idcarrera ";
        if (detallepagos.getValueAt(fila, 0) != null) {

            String conct = "concat(profesor.nombre,' ',profesor.apellido)";
            String[] campos = {"grupo.codigo", "grupo.descripcion", "grupo.dia", conct, "carrera.descripcion", "grupo.horariode", "grupo.horarioa", "grupo.fechainicio", "grupo.fechafin", "grupo.cantalumnos", "grupo.estado", "grupo.inscripcion", "grupo.colegiatura"};
            //llenarcomboprofesor();
            //llenarcombocarrera();
            Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");

            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            rs = ac.getRegistros("grupo", campos, cond, id, inner);

            if (rs != null) {
                try {
                    if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                        rs.beforeFirst();//regresa el puntero al primer registro
                        while (rs.next()) {//mientras tenga registros que haga lo siguiente

                            //codigo.setText(rs.getString(1));
                            //descripcion.setText(rs.getString(2));
                            cDiia.setSelectedItem(rs.getString(3));
                            //int pr = Integer.parseInt((String) hashProfesor.get(rs.getString(4)));
                            //profesor.setSelectedIndex(pr);
                            //int car = Integer.parseInt((String) hashCarrera.get(rs.getString(5)));
                            //carrera.setSelectedIndex(car);
                            //horaa.setValue(rs.getTime(6));
                            //horaa.setValue(rs.getTime(7));
                            //fechainicio.setDate((rs.getDate(8)));
                            //fechafin.setDate((rs.getDate(9)));
                            //cantalumnos.setText(rs.getString(10));

                            if (rs.getObject(11).equals(true)) {
                                //estado.setText("Activo");
                                //estado.setSelected(true);
                                //estado.setBackground(new java.awt.Color(102, 204, 0));
                            } else {
                                //estado.setText("Inactivo");
                                //estado.setSelected(false);
                                //estado.setBackground(Color.red);
                            }
                            //inscripcion.setValue(rs.getFloat(12));
                            //colegiatura.setValue(rs.getFloat(13));
                            //graduados.setSelectedItem(rs.getString(12));
                        }
                    }
                } catch (SQLException e) {
                    JOptionPane.showInternalMessageDialog(this, e);
                }
            }
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

        popupprofesor = new javax.swing.JPopupMenu();
        Nuevo_Profesor = new javax.swing.JMenuItem();
        Actualizar_Profesor = new javax.swing.JMenuItem();
        popupcarrera = new javax.swing.JPopupMenu();
        Nueva_Carrera = new javax.swing.JMenuItem();
        Actualizar_Carrera = new javax.swing.JMenuItem();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cDiia = new javax.swing.JComboBox();
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
        JPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        detallepagos = new javax.swing.JTable();
        JPanelBusqueda = new javax.swing.JPanel();
        codigo = new elaprendiz.gui.textField.TextField();
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
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel22 = new javax.swing.JLabel();
        clockDigital2 = new elaprendiz.gui.varios.ClockDigital();
        jLabel23 = new javax.swing.JLabel();
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
        bntModificar.setEnabled(false);
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntGuardar, gridBagConstraints);

        bntEliminar.setBackground(new java.awt.Color(51, 153, 255));
        bntEliminar.setMnemonic(KeyEvent.VK_E);
        bntEliminar.setText("Eliminar");
        bntEliminar.setEnabled(false);
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
        pnlActionButtons.setBounds(0, 560, 880, 50);

        JPanelCampos.setBackground(java.awt.SystemColor.activeCaption);
        JPanelCampos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        JPanelCampos.setForeground(new java.awt.Color(204, 204, 204));
        JPanelCampos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        JPanelCampos.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Horario A:");
        JPanelCampos.add(jLabel10);
        jLabel10.setBounds(710, 20, 110, 20);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Día:");
        JPanelCampos.add(jLabel2);
        jLabel2.setBounds(20, 40, 60, 27);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Profesor:");
        JPanelCampos.add(jLabel3);
        jLabel3.setBounds(300, 20, 250, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Fecha Fin:");
        JPanelCampos.add(jLabel6);
        jLabel6.setBounds(710, 70, 110, 20);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Horario De:");
        JPanelCampos.add(jLabel13);
        jLabel13.setBounds(580, 20, 100, 20);

        cDiia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo", "Mixto" }));
        cDiia.setName("Dia"); // NOI18N
        JPanelCampos.add(cDiia);
        cDiia.setBounds(90, 40, 150, 27);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Fecha Inicio:");
        JPanelCampos.add(jLabel9);
        jLabel9.setBounds(580, 70, 110, 20);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Carrera:");
        JPanelCampos.add(jLabel5);
        jLabel5.setBounds(300, 70, 250, 20);

        cGrupo.setName("Dia"); // NOI18N
        JPanelCampos.add(cGrupo);
        cGrupo.setBounds(90, 80, 150, 27);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Grupo:");
        JPanelCampos.add(jLabel7);
        jLabel7.setBounds(20, 80, 60, 27);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Inscripción Q.");
        JPanelCampos.add(jLabel24);
        jLabel24.setBounds(580, 120, 110, 20);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Colegiatura Q.");
        JPanelCampos.add(jLabel18);
        jLabel18.setBounds(710, 120, 110, 20);

        carrera.setEditable(false);
        carrera.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        carrera.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        carrera.setName("codigo"); // NOI18N
        carrera.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(carrera);
        carrera.setBounds(300, 90, 250, 27);

        profesor.setEditable(false);
        profesor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        profesor.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        profesor.setName("codigo"); // NOI18N
        profesor.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(profesor);
        profesor.setBounds(300, 40, 250, 27);

        inscripcion.setEditable(false);
        inscripcion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        inscripcion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inscripcion.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        inscripcion.setName("inscripcion"); // NOI18N
        inscripcion.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(inscripcion);
        inscripcion.setBounds(580, 140, 110, 27);

        colegiatura.setEditable(false);
        colegiatura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        colegiatura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        colegiatura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        colegiatura.setName("colegiatura"); // NOI18N
        colegiatura.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(colegiatura);
        colegiatura.setBounds(710, 140, 110, 27);

        horaa.setEditable(false);
        horaa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        horaa.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        horaa.setName("codigo"); // NOI18N
        horaa.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(horaa);
        horaa.setBounds(710, 40, 110, 27);

        horade.setEditable(false);
        horade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        horade.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        horade.setName("codigo"); // NOI18N
        horade.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(horade);
        horade.setBounds(580, 40, 110, 27);

        fechainicio.setEditable(false);
        fechainicio.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fechainicio.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        fechainicio.setName("codigo"); // NOI18N
        fechainicio.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(fechainicio);
        fechainicio.setBounds(580, 90, 110, 27);

        fechafin.setEditable(false);
        fechafin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fechafin.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        fechafin.setName("codigo"); // NOI18N
        fechafin.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(fechafin);
        fechafin.setBounds(710, 90, 110, 27);

        panelImage.add(JPanelCampos);
        JPanelCampos.setBounds(0, 200, 880, 190);

        JPanelTable.setOpaque(false);
        JPanelTable.setPreferredSize(new java.awt.Dimension(786, 402));
        JPanelTable.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        detallepagos.setForeground(new java.awt.Color(51, 51, 51));
        detallepagos.setModel(model = new DefaultTableModel(null, titulos)
            {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            detallepagos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            detallepagos.setFocusCycleRoot(true);
            detallepagos.setGridColor(new java.awt.Color(51, 51, 255));
            detallepagos.setRowHeight(22);
            detallepagos.setSelectionBackground(java.awt.SystemColor.activeCaption);
            detallepagos.setSurrendersFocusOnKeystroke(true);
            detallepagos.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    detallepagosMouseClicked(evt);
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    detallepagosMouseClicked(evt);
                }
            });
            detallepagos.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    detallepagosKeyPressed(evt);
                }
            });
            jScrollPane1.setViewportView(detallepagos);
            detallepagos.getAccessibleContext().setAccessibleName("");

            JPanelTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

            panelImage.add(JPanelTable);
            JPanelTable.setBounds(0, 430, 760, 130);

            JPanelBusqueda.setBackground(java.awt.SystemColor.inactiveCaption);
            JPanelBusqueda.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            JPanelBusqueda.setLayout(null);

            codigo.setPreferredSize(new java.awt.Dimension(250, 27));
            JPanelBusqueda.add(codigo);
            codigo.setBounds(120, 10, 97, 27);

            jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel16.setText("Codigo:");
            JPanelBusqueda.add(jLabel16);
            jLabel16.setBounds(10, 10, 100, 27);

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
            nombrealumno.setBounds(440, 10, 370, 27);

            jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel19.setText("Alumno:");
            JPanelBusqueda.add(jLabel19);
            jLabel19.setBounds(310, 10, 120, 27);

            jLabel26.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel26.setText("Fecha Inicio:");
            JPanelBusqueda.add(jLabel26);
            jLabel26.setBounds(10, 50, 100, 27);

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
            beca.setBounds(440, 50, 130, 27);

            estado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            estado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            JPanelBusqueda.add(estado);
            estado.setBounds(700, 50, 110, 27);

            inicioalumno.setDate(Calendar.getInstance().getTime());
            inicioalumno.setDateFormatString("dd/MM/yyyy");
            inicioalumno.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            inicioalumno.setMaxSelectableDate(new java.util.Date(3093496470100000L));
            inicioalumno.setMinSelectableDate(new java.util.Date(-62135744300000L));
            inicioalumno.setPreferredSize(new java.awt.Dimension(120, 22));
            JPanelBusqueda.add(inicioalumno);
            inicioalumno.setBounds(120, 50, 120, 27);

            panelImage.add(JPanelBusqueda);
            JPanelBusqueda.setBounds(0, 110, 880, 90);

            pnlPaginador.setPreferredSize(new java.awt.Dimension(786, 40));
            pnlPaginador.setLayout(new java.awt.GridBagLayout());

            jLabel8.setFont(new java.awt.Font("Script MT Bold", 1, 32)); // NOI18N
            jLabel8.setText("  Detalle de pagos  ");
            pnlPaginador.add(jLabel8, new java.awt.GridBagConstraints());

            panelImage.add(pnlPaginador);
            pnlPaginador.setBounds(0, 390, 880, 40);

            jPanel1.setLayout(null);

            buttonAction1.setText("Colegiatura");
            buttonAction1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jPanel1.add(buttonAction1);
            buttonAction1.setBounds(10, 0, 90, 35);

            buttonAction2.setText("Otros Pagos");
            buttonAction2.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            jPanel1.add(buttonAction2);
            buttonAction2.setBounds(10, 90, 90, 35);

            panelImage.add(jPanel1);
            jPanel1.setBounds(760, 430, 120, 130);

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

            jDateChooser2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            JPanelRecibo.add(jDateChooser2);
            jDateChooser2.setBounds(120, 30, 120, 27);

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

            setBounds(0, 0, 890, 643);
        }// </editor-fold>//GEN-END:initComponents

    private void bntNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntNuevoActionPerformed
        // TODO add your handling code here:
        Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
        //llenarcomboprofesor();
        //llenarcombocarrera();
        //estado.setSelected(true);
        this.bntGuardar.setEnabled(true);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(false);
        //codigo.requestFocus();

    }//GEN-LAST:event_bntNuevoActionPerformed

    private void bntSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSalirActionPerformed
        cerrarVentana();
    }//GEN-LAST:event_bntSalirActionPerformed

    private void detallepagosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_detallepagosMouseClicked
        // TODO add your handling code here:
        filaseleccionada();

    }//GEN-LAST:event_detallepagosMouseClicked

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
        codigo.setText("");
        codigo.requestFocus();

    }//GEN-LAST:event_bntCancelarActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:
        cerrarVentana();
    }//GEN-LAST:event_formInternalFrameClosing

    private void detallepagosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_detallepagosKeyPressed
        // TODO add your handling code here:
        int key = evt.getKeyCode();
        if (key == java.awt.event.KeyEvent.VK_SPACE) {
            filaseleccionada();
        }
        if (key == java.awt.event.KeyEvent.VK_DOWN || key == java.awt.event.KeyEvent.VK_UP) {
            limpiar();
        }
    }//GEN-LAST:event_detallepagosKeyPressed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Actualizar_Carrera;
    private javax.swing.JMenuItem Actualizar_Profesor;
    private javax.swing.JPanel JPanelBusqueda;
    private javax.swing.JPanel JPanelCampos;
    private javax.swing.JPanel JPanelRecibo;
    private javax.swing.JPanel JPanelTable;
    private javax.swing.JMenuItem Nueva_Carrera;
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
    private javax.swing.JComboBox cDiia;
    private javax.swing.JComboBox cGrupo;
    private elaprendiz.gui.textField.TextField carrera;
    private elaprendiz.gui.varios.ClockDigital clockDigital2;
    public static elaprendiz.gui.textField.TextField codigo;
    private elaprendiz.gui.textField.TextField codigo3;
    private javax.swing.JFormattedTextField colegiatura;
    private javax.swing.JTable detallepagos;
    public static javax.swing.JLabel estado;
    private elaprendiz.gui.textField.TextField fechafin;
    private elaprendiz.gui.textField.TextField fechainicio;
    private elaprendiz.gui.textField.TextField horaa;
    private elaprendiz.gui.textField.TextField horade;
    public static com.toedter.calendar.JDateChooser inicioalumno;
    private javax.swing.JFormattedTextField inscripcion;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JScrollPane jScrollPane1;
    public static elaprendiz.gui.textField.TextField nombrealumno;
    private elaprendiz.gui.panel.PanelImage panelImage;
    private javax.swing.JPanel pnlActionButtons;
    private javax.swing.JPanel pnlPaginador;
    private javax.swing.JPanel pnlPaginador1;
    private javax.swing.JPopupMenu popupcarrera;
    private javax.swing.JPopupMenu popupprofesor;
    private elaprendiz.gui.textField.TextField profesor;
    // End of variables declaration//GEN-END:variables
}
