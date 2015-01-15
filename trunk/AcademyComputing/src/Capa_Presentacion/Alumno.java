/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.https://www.youtube.com/watch?v=ICF-RldvSIo
 */
package Capa_Presentacion;

import Capa_Datos.AccesoDatos;
import Capa_Negocio.FiltroCampos;
import Capa_Negocio.FormatoDecimal;
import Capa_Negocio.FormatoFecha;
import Capa_Negocio.GeneraCodigo;
import Capa_Negocio.Peticiones;
import Capa_Negocio.ProyeccionPagos;
import Capa_Negocio.TipoFiltro;
import Capa_Negocio.Utilidades;
import java.awt.Color;
import java.awt.Component;
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
public class Alumno extends javax.swing.JInternalFrame {

    private static Horario frmHorario = new Horario();
    /*El modelo se define en : Jtable-->propiedades-->model--> <User Code> */
    DefaultTableModel model, modelgrupo;
    DefaultComboBoxModel modelCombo;
    String[] titulos = {"codigo", "Nombres", "Apellidos", "Fecha Nec", "Beca", "Fecha Inicio", "Estado"};//Titulos para Jtabla
    String[] titulosgrupo = {"Codigo", "Dia", "Profesor", "Carrera", "De", "A", "Inscr.", "Coleg.", "fecha Inicio", "Fecha Fin",};//Titulos para Jtabla
    /*Se hace una instancia de la clase que recibira las peticiones de esta capa de aplicación*/
    Peticiones peticiones = new Peticiones();
    public static Hashtable<String, String> hashGrupo = new Hashtable<>();
    int nidalumno, idalumno;
    boolean matricula = true;
    //public Hashtable<String, String> hashGrupo = new Hashtable<>();

    /*Se hace una instancia de la clase que recibira las peticiones de mensages de la capa de aplicación*/
    //public static JOptionMessage msg = new JOptionMessage();
    /**
     * Creates new form Cliente
     */
    public Alumno() {
        initComponents();
        setFiltroTexto();
        addEscapeKey();
        limpiar();

        cDia.addItemListener(
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
            rbNombre.setSelected(true);
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

    public void removejtablegrupo() {
        while (horarios.getRowCount() != 0) {
            modelgrupo.removeRow(0);
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
        TipoFiltro.setFiltraEntrada(dpi.getDocument(), FiltroCampos.NUM_LETRAS, 20, true);
        TipoFiltro.setFiltraEntrada(telefono.getDocument(), FiltroCampos.SOLO_NUMEROS, 16, false);
        TipoFiltro.setFiltraEntrada(busqueda.getDocument(), FiltroCampos.NUM_LETRAS, 100, true);
    }

    private void selecciondia() {
        if (cDia.getSelectedIndex() == 0) {
            cGrupo.removeAllItems();
        } else if (cDia.getSelectedIndex() > 0) {
            String sdia = (String) cDia.getSelectedItem();
            llenarcombogrupo(sdia);
        }
    }

    /*
     *Prepara los parametros para la consulta de datos que deseamos agregar al model del ComboBox
     *y se los envia a un metodo interno getRegistroCombo() 
     *
     */
    public void llenarcombogrupo(String dia) {
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
            horade.setText("");
            horaa.setText("");
            fechaini.setText("");
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
                                fechaini.setText((rs.getString(5)));
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
    private void MostrarDatosGrupos(String Dato) {
        String conct = "concat(profesor.nombre,' ',profesor.apellido)";
        String conct2 = "concat(grupo.codigo,' ',grupo.descripcion)";
        String[] campos = {conct2, "grupo.dia", conct, "carrera.descripcion", "DATE_FORMAT(grupo.horariode,'%h:%i %p')", "DATE_FORMAT(grupo.horarioa,'%h:%i %p')", "grupo.inscripcion", "grupo.colegiatura", "DATE_FORMAT(grupo.fechainicio,'%d-%m-%Y')", "DATE_FORMAT(grupo.fechafin,'%d-%m-%Y')"};
        String[] condiciones = {"alumno.codigo"};
        String[] Id = {Dato};
        String inner = " INNER JOIN alumnosengrupo ON grupo.idgrupo = alumnosengrupo.grupo_idgrupo INNER JOIN alumno ON alumnosengrupo.alumno_idalumno = alumno.idalumno INNER JOIN profesor on grupo.profesor_idcatedratico=profesor.idcatedratico INNER JOIN carrera on grupo.carrera_idcarrera=carrera.idcarrera ";

        if (!Dato.isEmpty()) {
            removejtablegrupo();
            modelgrupo = peticiones.getRegistroPorPks(modelgrupo, "grupo", campos, condiciones, Id, inner);
        } else {
            JOptionPane.showInternalMessageDialog(this, "Debe ingresar un codigo para la busqueda");
        }
        Utilidades.ajustarAnchoColumnas(horarios);
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
        String[] campos = {"alumno.codigo", "alumno.nombres", "alumno.apellidos", "DATE_FORMAT(alumno.fechanacimiento,'%d-%m-%Y')", "alumno.cantidadbeca", "DATE_FORMAT(alumno.fechadeinicio,'%d-%m-%Y')", "alumno.estado"};
        String[] condiciones = {"alumno.codigo"};
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
        if (this.rbNombre.isSelected()) {
            removejtable();
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            model = peticiones.getRegistroPorLike(model, "alumno", campos, "alumno.nombres", Dato, "");
        }
        if (this.rbApellido.isSelected()) {
            removejtable();
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            model = peticiones.getRegistroPorLike(model, "alumno", campos, "alumno.apellidos", Dato, "");
        }
        Utilidades.ajustarAnchoColumnas(alumnos);
    }

    /* Este metodo  consulta en la BD el codigo de la fila seleccionada y llena los componentes
     * de la parte superior del formulario con los datos obtenidos en la capa de Negocio getRegistroSeleccionado().
     * 
     * @return 
     */
    private void filaseleccionada2() {

        int fila = alumnos.getSelectedRow();
        String[] cond = {"alumno.codigo"};
        String[] id = {"" + alumnos.getValueAt(fila, 0)};

        if (alumnos.getValueAt(fila, 0) != null) {

            String[] campos = {"alumno.codigo", "alumno.nombres", "alumno.apellidos", "alumno.fechanacimiento", "alumno.direccion", "alumno.sexo", "alumno.telefono", "alumno.cantidadbeca", "alumno.fechadeinicio", "alumno.titularnombres", "alumno.titularapellidos", "alumno.titulardpi", "alumno.estado"};
            Component[] cmps = {codigo, nombres, apellidos, fechanacimiento, direccion, sexo, telefono, beca, fechainicio, titularnombre, dpi, dpi, estado
            };

            Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");

            peticiones.getRegistroSeleccionado(cmps, "alumno", campos, cond, id, "", null);

            this.bntGuardar.setEnabled(false);
            this.bntModificar.setEnabled(true);
            this.bntEliminar.setEnabled(true);
            this.bntNuevo.setEnabled(false);
        }
    }

    /* Este metodo  consulta en la BD el codigo de la fila seleccionada y llena los componentes
     * de la parte superior del formulario con los datos obtenidos en la capa de Negocio getRegistroSeleccionado().
     * 
     * @return 
     */
    private void filaseleccionada() {

        int fila = alumnos.getSelectedRow();
        String[] cond = {"alumno.codigo"};
        String[] id = {(String) alumnos.getValueAt(fila, 0)};
        if (alumnos.getValueAt(fila, 0) != null) {
            matricula = false;
            String[] campos = {"alumno.codigo", "alumno.nombres", "alumno.apellidos", "alumno.fechanacimiento", "alumno.direccion", "alumno.sexo", "alumno.telefono", "alumno.cantidadbeca", "alumno.fechadeinicio", "alumno.titularnombres", "alumno.titularapellidos", "alumno.titulardpi", "alumno.estado", "alumno.idalumno"};
            Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");

            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            rs = ac.getRegistros("alumno", campos, cond, id, "");

            if (rs != null) {
                try {
                    if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                        rs.beforeFirst();//regresa el puntero al primer registro
                        while (rs.next()) {//mientras tenga registros que haga lo siguiente
                            codigo.setText(rs.getString(1));
                            nombres.setText(rs.getString(2));
                            apellidos.setText(rs.getString(3));
                            fechanacimiento.setDate((rs.getDate(4)));
                            direccion.setText(rs.getString(5));
                            sexo.setSelectedItem(rs.getString(6));
                            telefono.setText(rs.getString(7));
                            //colegiatura.setText(rs.getString(8));
                            beca.setText(rs.getString(8));
                            fechainicio.setDate((rs.getDate(9)));
                            titularnombre.setText(rs.getString(10));
                            titularapellido.setText(rs.getString(11));
                            dpi.setText(rs.getString(12));
                            if (rs.getObject(13).equals(true)) {
                                estado.setText("Activo");
                                estado.setSelected(true);
                                estado.setBackground(new java.awt.Color(102, 204, 0));
                            } else {
                                estado.setText("Inactivo");
                                estado.setSelected(false);
                                estado.setBackground(Color.red);
                            }
                            nidalumno = rs.getInt(14);
                            MostrarDatosGrupos(alumnos.getValueAt(fila, 0).toString());
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
            codigo.setEditable(false);
            beca.setEditable(false);

        }
    }

    private int ultimoalumno() {
        if (nidalumno == 0) {
            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            rs = ac.getUltimoRegistro("alumno", "idalumno");
            if (rs != null) {
                try {
                    if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                        rs.beforeFirst();//regresa el puntero al primer registro
                        while (rs.next()) {//mientras tenga registros que haga lo siguiente
                            nidalumno = (rs.getInt(1) + 1);
                        }
                    }
                } catch (SQLException e) {
                    JOptionPane.showInternalMessageDialog(this, e);
                }
            }
        }
        return nidalumno;

    }

    private void codigoalumno() {
        String tx = nombres.getText() + " " + apellidos.getText();
        if (tx.isEmpty()) {
        } else {
            String cod = GeneraCodigo.actualizarRegistro(nombres.getText() + " " + apellidos.getText());
            codigo.setText(cod + "-" + ultimoalumno());
        }
    }

    public void idalumnog(String codigo) {

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
                        idalumno = (rs.getInt(1));
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
        jLabel16 = new javax.swing.JLabel();
        beca = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        fechainicio = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        titularnombre = new elaprendiz.gui.textField.TextField();
        jLabel2 = new javax.swing.JLabel();
        dpi = new elaprendiz.gui.textField.TextField();
        jLabel15 = new javax.swing.JLabel();
        telefono = new elaprendiz.gui.textField.TextField();
        titularapellido = new elaprendiz.gui.textField.TextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        horarios = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cDia = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cGrupo = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        carrera = new elaprendiz.gui.textField.TextField();
        profesor = new elaprendiz.gui.textField.TextField();
        inscripcion = new javax.swing.JFormattedTextField();
        colegiatura = new javax.swing.JFormattedTextField();
        horaa = new elaprendiz.gui.textField.TextField();
        horade = new elaprendiz.gui.textField.TextField();
        fechaini = new elaprendiz.gui.textField.TextField();
        fechafin = new elaprendiz.gui.textField.TextField();
        buttonAction1 = new elaprendiz.gui.button.ButtonAction();
        JPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        alumnos = new javax.swing.JTable();
        JPanelBusqueda = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        busqueda = new elaprendiz.gui.textField.TextField();
        rbCodigo = new javax.swing.JRadioButton();
        rbNombre = new javax.swing.JRadioButton();
        rbApellido = new javax.swing.JRadioButton();
        pnlPaginador = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 0, 0));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setForeground(new java.awt.Color(0, 0, 0));
        setIconifiable(true);
        setTitle("Alumnos");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setName("alumnos"); // NOI18N
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
        bntEliminar.setEnabled(false);
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
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jPanel1.setBackground(java.awt.SystemColor.activeCaption);
        jPanel1.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Código:");
        jPanel1.add(jLabel10);
        jLabel10.setBounds(10, 30, 80, 20);

        codigo.setEditable(false);
        codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        codigo.setName("codigo"); // NOI18N
        codigo.setNextFocusableComponent(nombres);
        jPanel1.add(codigo);
        codigo.setBounds(100, 30, 130, 21);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("Nombres:");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 60, 80, 20);

        nombres.setEditable(false);
        nombres.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        nombres.setName("nombres"); // NOI18N
        nombres.setNextFocusableComponent(apellidos);
        nombres.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                nombresFocusLost(evt);
            }
        });
        nombres.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nombresKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nombresKeyPressed(evt);
            }
        });
        jPanel1.add(nombres);
        nombres.setBounds(100, 60, 250, 21);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Apellidos:");
        jPanel1.add(jLabel11);
        jLabel11.setBounds(10, 90, 80, 20);

        apellidos.setEditable(false);
        apellidos.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        apellidos.setName("apellidos"); // NOI18N
        apellidos.setNextFocusableComponent(direccion);
        apellidos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                apellidosFocusLost(evt);
            }
        });
        apellidos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nombresKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nombresKeyPressed(evt);
            }
        });
        jPanel1.add(apellidos);
        apellidos.setBounds(100, 90, 250, 21);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel14.setText("Dirección:");
        jPanel1.add(jLabel14);
        jLabel14.setBounds(10, 120, 80, 20);

        direccion.setEditable(false);
        direccion.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        direccion.setNextFocusableComponent(sexo);
        jPanel1.add(direccion);
        direccion.setBounds(100, 120, 250, 21);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Fecha Nacimiento:");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(360, 90, 150, 21);

        fechanacimiento.setDate(Calendar.getInstance().getTime());
        fechanacimiento.setDateFormatString("dd/MM/yyyy");
        fechanacimiento.setEnabled(false);
        fechanacimiento.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fechanacimiento.setMaxSelectableDate(new java.util.Date(3093496470100000L));
        fechanacimiento.setMinSelectableDate(new java.util.Date(-62135744300000L));
        fechanacimiento.setNextFocusableComponent(fechanacimiento);
        fechanacimiento.setPreferredSize(new java.awt.Dimension(120, 22));
        jPanel1.add(fechanacimiento);
        fechanacimiento.setBounds(520, 90, 130, 21);

        sexo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sexo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "F", "M" }));
        sexo.setName("sexo"); // NOI18N
        sexo.setNextFocusableComponent(estado);
        sexo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sexoActionPerformed(evt);
            }
        });
        jPanel1.add(sexo);
        sexo.setBounds(520, 30, 130, 21);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel18.setText("Sexo:");
        jPanel1.add(jLabel18);
        jLabel18.setBounds(430, 30, 80, 20);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Estado:");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(400, 60, 110, 20);

        estado.setBackground(new java.awt.Color(51, 153, 255));
        estado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        estado.setForeground(new java.awt.Color(255, 255, 255));
        estado.setText("Activo");
        estado.setEnabled(false);
        estado.setName("JRadioButton"); // NOI18N
        estado.setNextFocusableComponent(fechanacimiento);
        jPanel1.add(estado);
        estado.setBounds(520, 60, 130, 21);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel16.setText("Beca:");
        jPanel1.add(jLabel16);
        jLabel16.setBounds(650, 30, 110, 20);

        beca.setEditable(false);
        beca.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        beca.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        beca.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        beca.setPreferredSize(new java.awt.Dimension(80, 23));
        jPanel1.add(beca);
        beca.setBounds(780, 30, 80, 23);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Fecha Inicio:");
        jPanel1.add(jLabel9);
        jLabel9.setBounds(390, 120, 110, 17);

        fechainicio.setDate(Calendar.getInstance().getTime());
        fechainicio.setDateFormatString("dd/MM/yyyy");
        fechainicio.setEnabled(false);
        fechainicio.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fechainicio.setMaxSelectableDate(new java.util.Date(3093496470100000L));
        fechainicio.setMinSelectableDate(new java.util.Date(-62135744300000L));
        fechainicio.setNextFocusableComponent(titularnombre);
        fechainicio.setPreferredSize(new java.awt.Dimension(120, 22));
        jPanel1.add(fechainicio);
        fechainicio.setBounds(520, 120, 130, 21);

        jTabbedPane1.addTab("Datos Alumno", jPanel1);

        jPanel2.setBackground(java.awt.SystemColor.activeCaption);
        jPanel2.setLayout(null);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel17.setText("Titular Apellidos:");
        jPanel2.add(jLabel17);
        jLabel17.setBounds(70, 70, 130, 20);

        titularnombre.setEditable(false);
        titularnombre.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        titularnombre.setName("descripcion"); // NOI18N
        titularnombre.setNextFocusableComponent(dpi);
        jPanel2.add(titularnombre);
        titularnombre.setBounds(210, 30, 250, 21);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Titular Nombres:");
        jPanel2.add(jLabel2);
        jLabel2.setBounds(70, 30, 120, 20);

        dpi.setEditable(false);
        dpi.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        dpi.setNextFocusableComponent(telefono);
        jPanel2.add(dpi);
        dpi.setBounds(570, 70, 250, 21);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel15.setText("DPI:");
        jPanel2.add(jLabel15);
        jLabel15.setBounds(480, 70, 80, 20);

        telefono.setEditable(false);
        telefono.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jPanel2.add(telefono);
        telefono.setBounds(570, 30, 250, 21);

        titularapellido.setEditable(false);
        titularapellido.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        titularapellido.setNextFocusableComponent(telefono);
        jPanel2.add(titularapellido);
        titularapellido.setBounds(210, 70, 250, 21);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel20.setText("Telefono:");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(480, 30, 80, 20);

        jTabbedPane1.addTab("Encargado del Alumno", jPanel2);

        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(786, 402));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        horarios.setForeground(new java.awt.Color(51, 51, 51));
        horarios.setModel(modelgrupo = new DefaultTableModel(null, titulosgrupo)
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
            jScrollPane2.setViewportView(horarios);

            jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

            jTabbedPane1.addTab("Información Academicos", jPanel3);

            jPanel4.setBackground(java.awt.SystemColor.activeCaption);
            jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            jPanel4.setForeground(new java.awt.Color(204, 204, 204));
            jPanel4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            jPanel4.setLayout(null);

            jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel13.setText("Horario A:");
            jPanel4.add(jLabel13);
            jLabel13.setBounds(710, 10, 110, 20);

            jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel3.setText("Día:");
            jPanel4.add(jLabel3);
            jLabel3.setBounds(20, 30, 60, 27);

            jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel5.setText("Profesor:");
            jPanel4.add(jLabel5);
            jLabel5.setBounds(300, 10, 250, 20);

            jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel6.setText("Fecha Fin:");
            jPanel4.add(jLabel6);
            jLabel6.setBounds(710, 60, 110, 20);

            jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel19.setText("Horario De:");
            jPanel4.add(jLabel19);
            jLabel19.setBounds(580, 10, 100, 20);

            cDia.setEditable(true);
            cDia.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            cDia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo", "Mixto" }));
            cDia.setName("cdia"); // NOI18N
            jPanel4.add(cDia);
            cDia.setBounds(90, 30, 150, 21);

            jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel21.setText("Fecha Inicio:");
            jPanel4.add(jLabel21);
            jLabel21.setBounds(580, 60, 110, 20);

            jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel22.setText("Carrera:");
            jPanel4.add(jLabel22);
            jLabel22.setBounds(300, 60, 250, 20);

            cGrupo.setEditable(true);
            cGrupo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
            cGrupo.setName("cgrupo"); // NOI18N
            jPanel4.add(cGrupo);
            cGrupo.setBounds(90, 70, 150, 21);

            jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel23.setText("Grupo:");
            jPanel4.add(jLabel23);
            jLabel23.setBounds(20, 70, 60, 27);

            jLabel24.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel24.setText("Inscripción Q.");
            jPanel4.add(jLabel24);
            jLabel24.setBounds(580, 110, 110, 20);

            jLabel25.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
            jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel25.setText("Colegiatura Q.");
            jPanel4.add(jLabel25);
            jLabel25.setBounds(710, 110, 110, 20);

            carrera.setEditable(false);
            carrera.setHorizontalAlignment(javax.swing.JTextField.LEFT);
            carrera.setName("codigo"); // NOI18N
            carrera.setPreferredSize(new java.awt.Dimension(120, 21));
            jPanel4.add(carrera);
            carrera.setBounds(300, 80, 250, 21);

            profesor.setEditable(false);
            profesor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
            profesor.setName("codigo"); // NOI18N
            profesor.setPreferredSize(new java.awt.Dimension(120, 21));
            jPanel4.add(profesor);
            profesor.setBounds(300, 30, 250, 21);

            inscripcion.setEditable(false);
            inscripcion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
            inscripcion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            inscripcion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            inscripcion.setName("inscripcion"); // NOI18N
            inscripcion.setPreferredSize(new java.awt.Dimension(80, 23));
            jPanel4.add(inscripcion);
            inscripcion.setBounds(580, 130, 110, 23);

            colegiatura.setEditable(false);
            colegiatura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
            colegiatura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            colegiatura.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            colegiatura.setName("colegiatura"); // NOI18N
            colegiatura.setPreferredSize(new java.awt.Dimension(80, 23));
            jPanel4.add(colegiatura);
            colegiatura.setBounds(710, 130, 110, 23);

            horaa.setEditable(false);
            horaa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            horaa.setName("codigo"); // NOI18N
            horaa.setPreferredSize(new java.awt.Dimension(120, 21));
            jPanel4.add(horaa);
            horaa.setBounds(710, 30, 110, 21);

            horade.setEditable(false);
            horade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            horade.setName("codigo"); // NOI18N
            horade.setPreferredSize(new java.awt.Dimension(120, 21));
            jPanel4.add(horade);
            horade.setBounds(580, 30, 110, 21);

            fechaini.setEditable(false);
            fechaini.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            fechaini.setName("codigo"); // NOI18N
            fechaini.setPreferredSize(new java.awt.Dimension(120, 21));
            jPanel4.add(fechaini);
            fechaini.setBounds(580, 80, 110, 21);

            fechafin.setEditable(false);
            fechafin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            fechafin.setName("codigo"); // NOI18N
            fechafin.setPreferredSize(new java.awt.Dimension(120, 21));
            jPanel4.add(fechafin);
            fechafin.setBounds(710, 80, 110, 21);

            buttonAction1.setText("Nueva Matricula");
            buttonAction1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
            buttonAction1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonAction1ActionPerformed(evt);
                }
            });
            jPanel4.add(buttonAction1);
            buttonAction1.setBounds(90, 110, 150, 35);

            jTabbedPane1.addTab("Matricular Alumno", jPanel4);

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
                jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/buscar.png"))); // NOI18N
                jLabel7.setText("Buscar Alumno por:");
                JPanelBusqueda.add(jLabel7);
                jLabel7.setBounds(117, 2, 173, 40);

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

                rbNombre.setBackground(new java.awt.Color(51, 153, 255));
                rbNombre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
                rbNombre.setForeground(new java.awt.Color(255, 255, 255));
                rbNombre.setSelected(true);
                rbNombre.setText("Nombre");
                rbNombre.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        rbNombreActionPerformed(evt);
                    }
                });
                JPanelBusqueda.add(rbNombre);
                rbNombre.setBounds(380, 40, 90, 25);

                rbApellido.setBackground(new java.awt.Color(51, 153, 255));
                rbApellido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
                rbApellido.setForeground(new java.awt.Color(255, 255, 255));
                rbApellido.setText("Apellido");
                rbApellido.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        rbApellidoActionPerformed(evt);
                    }
                });
                JPanelBusqueda.add(rbApellido);
                rbApellido.setBounds(490, 40, 79, 25);

                panelImage.add(JPanelBusqueda);
                JPanelBusqueda.setBounds(0, 230, 880, 70);

                pnlPaginador.setBackground(new java.awt.Color(57, 104, 163));
                pnlPaginador.setPreferredSize(new java.awt.Dimension(786, 40));
                pnlPaginador.setLayout(new java.awt.GridBagLayout());

                jLabel8.setFont(new java.awt.Font("Script MT Bold", 1, 32)); // NOI18N
                jLabel8.setForeground(new java.awt.Color(255, 255, 255));
                jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/alumno.png"))); // NOI18N
                jLabel8.setText("<--Alumnos-->");
                pnlPaginador.add(jLabel8, new java.awt.GridBagConstraints());

                panelImage.add(pnlPaginador);
                pnlPaginador.setBounds(0, 0, 880, 40);

                getContentPane().add(panelImage, java.awt.BorderLayout.CENTER);

                getAccessibleContext().setAccessibleName("Profesores");

                setBounds(0, 0, 890, 512);
            }// </editor-fold>//GEN-END:initComponents

    private void bntNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntNuevoActionPerformed
        // TODO add your handling code here:
        Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
        estado.setSelected(true);
        this.bntGuardar.setEnabled(true);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(false);
        //colegiatura.setEditable(false);
        beca.setEditable(false);
        codigo.setEditable(false);
        nombres.requestFocus();
        nidalumno = 0;
        matricula = true;

    }//GEN-LAST:event_bntNuevoActionPerformed

    private void bntGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntGuardarActionPerformed
        // TODO add your handling code here:
        if (Utilidades.esObligatorio(this.jPanel1, true) || (Utilidades.esObligatorio(this.jPanel2, true)) || (Utilidades.esObligatorio(this.jPanel4, true))) {
            JOptionPane.showInternalMessageDialog(this, "Los campos marcados son Obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Grabar el Registro?", "Pregunta", 0);
        if (resp == 0) {
            String mensagecodigo = null;
            codigoalumno();
            boolean seguardo = false;
            String nombreTabla = "alumno";
            String nombreTabla2 = "alumnosengrupo";
            String campos = "codigo, nombres, apellidos, fechanacimiento, sexo, direccion, telefono, cantidadbeca, fechadeinicio, titularnombres, titularapellidos, titulardpi, estado";
            String campos2 = "alumno_idalumno, grupo_idgrupo";

            String fechainici = FormatoFecha.getFormato(fechainicio.getCalendar().getTime(), FormatoFecha.A_M_D);
            String fechanacimient = FormatoFecha.getFormato(fechanacimiento.getCalendar().getTime(), FormatoFecha.A_M_D);

            int estad = 0;
            if (this.estado.isSelected()) {
                estad = 1;
            }
            if (beca.getText().isEmpty()) {
                beca.setValue(0);
            }
            Object[] valores = {codigo.getText(), nombres.getText(), apellidos.getText(), fechanacimient, sexo.getSelectedItem(), direccion.getText(), telefono.getText(), beca.getText(), fechainici, titularnombre.getText(), titularapellido.getText(), dpi.getText(), estad
            };

            seguardo = peticiones.guardarRegistros(nombreTabla, campos, valores);

            if (seguardo) {
                if (cGrupo.getSelectedIndex() != -1) {
                    mensagecodigo = codigo.getText();

                    mGrupo grup = (mGrupo) cGrupo.getSelectedItem();
                    String idg = grup.getID();
                    idalumnog(codigo.getText());
                    String alumnoid = "" + idalumno;
                    Object[] valores2 = {alumnoid, idg};

                    seguardo = peticiones.guardarRegistros(nombreTabla2, campos2, valores2);
                    if (seguardo) {
                        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                        MostrarDatos(busqueda.getText());
                        removejtablegrupo();
                        this.bntGuardar.setEnabled(false);
                        this.bntModificar.setEnabled(false);
                        this.bntEliminar.setEnabled(false);
                        this.bntNuevo.setEnabled(true);
                        busqueda.requestFocus();
                        idalumno = 0;
                        JOptionPane.showInternalMessageDialog(this, "El alumno no se ha Guardado con el código:\n" + mensagecodigo, "Guardar", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showInternalMessageDialog(this, "El grupo no se Guardo", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                //  }
            } else {
                JOptionPane.showInternalMessageDialog(this, "El alumno no se ha Guardado con el código:\n" + mensagecodigo, "Error", JOptionPane.ERROR_MESSAGE);
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
                removejtablegrupo();
                this.bntGuardar.setEnabled(false);
                this.bntModificar.setEnabled(false);
                this.bntEliminar.setEnabled(false);
                this.bntNuevo.setEnabled(true);
                busqueda.requestFocus();
                JOptionPane.showInternalMessageDialog(this, "El dato se ha Eliminado Correctamente", "Eliminar", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bntEliminarActionPerformed

    private void bntModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntModificarActionPerformed
        // TODO add your handling code here:
        if (Utilidades.esObligatorio(this.jPanel1, true) || (Utilidades.esObligatorio(this.jPanel2, true))) {
            JOptionPane.showInternalMessageDialog(this, "Los campos marcados son Obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Modificar el Registro?", "Pregunta", 0);
        if (resp == 0) {

            codigoalumno();
            int seguardo = 0;
            int fila = alumnos.getSelectedRow();
            String id = (String) "" + alumnos.getValueAt(fila, 0);
            String columnaId = "codigo";

            String nombreTabla = "alumno";
            String campos = "codigo, nombres, apellidos, fechanacimiento, sexo, direccion, telefono, cantidadbeca, fechadeinicio, titularnombres, titularapellidos, titulardpi, estado";
            String fechainici = FormatoFecha.getFormato(fechainicio.getCalendar().getTime(), FormatoFecha.A_M_D);
            String fechanacimient = FormatoFecha.getFormato(fechanacimiento.getCalendar().getTime(), FormatoFecha.A_M_D);

            int estad = 0;
            if (this.estado.isSelected()) {
                estad = 1;
            }
            Object[] valores = {codigo.getText(), nombres.getText(), apellidos.getText(), fechanacimient, sexo.getSelectedItem(), direccion.getText(), telefono.getText(), beca.getText(), fechainici, titularnombre.getText(), titularapellido.getText(), dpi.getText(), estad, id
            };

            seguardo = peticiones.actualizarRegistro(nombreTabla, campos, valores, columnaId, id);

            if (seguardo == 1) {
                Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                MostrarDatos(busqueda.getText());
                removejtablegrupo();
                this.bntGuardar.setEnabled(false);
                this.bntModificar.setEnabled(false);
                this.bntEliminar.setEnabled(false);
                this.bntNuevo.setEnabled(true);
                busqueda.requestFocus();
                JOptionPane.showInternalMessageDialog(this, "El dato se ha Modificado Correctamente", "Modificar", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bntModificarActionPerformed

    private void bntCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCancelarActionPerformed
        // TODO add your handling code here:
        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
        Utilidades.esObligatorio(this.JPanelCampos, false);
        this.bntGuardar.setEnabled(false);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(true);
        removejtable();
        removejtablegrupo();
        busqueda.requestFocus();
        nidalumno = 0;
        matricula = false;

    }//GEN-LAST:event_bntCancelarActionPerformed

    private void rbCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbCodigoActionPerformed
        // TODO add your handling code here:
        rbNombre.setSelected(false);
        rbApellido.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbCodigoActionPerformed

    private void rbNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNombreActionPerformed
        // TODO add your handling code here:
        rbCodigo.setSelected(false);
        rbApellido.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbNombreActionPerformed

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

    private void rbApellidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbApellidoActionPerformed
        // TODO add your handling code here:
        rbCodigo.setSelected(false);
        rbNombre.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbApellidoActionPerformed

    private void sexoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sexoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sexoActionPerformed

    private void nombresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nombresKeyPressed
        // TODO add your handling code here:
        codigoalumno();
    }//GEN-LAST:event_nombresKeyPressed

    private void nombresFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nombresFocusLost
        // TODO add your handling code here:
        nombres.setText(nombres.getText().toUpperCase());
    }//GEN-LAST:event_nombresFocusLost

    private void apellidosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_apellidosFocusLost
        // TODO add your handling code here:
        apellidos.setText(apellidos.getText().toUpperCase());
    }//GEN-LAST:event_apellidosFocusLost

    private void buttonAction1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAction1ActionPerformed
        // TODO add your handling code here:
        if (matricula == false) {
            if ((Utilidades.esObligatorio(this.jPanel4, true))) {
                JOptionPane.showInternalMessageDialog(this, "Los campos marcados son Obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Grabar el Registro?", "Pregunta", 0);
            if (resp == 0) {
                boolean seguardo = false;
                String nombreTabla2 = "alumnosengrupo";
                String campos2 = "alumno_idalumno, grupo_idgrupo";

                if (cGrupo.getSelectedIndex() != -1) {

                    //Para saber si el grupo ya ha sigo asignado y no asiganar el mismo grupo dos veces.
                    boolean carrera = false;
                    int cant = modelgrupo.getRowCount();
                    for (int i = 0; i < cant; i++) {
                        if (modelgrupo.getValueAt(i, 0).toString().equals(cGrupo.getSelectedItem().toString())) {
                            carrera = true;
                        }
                    }

                    if (carrera == false) {
                        mGrupo grup = (mGrupo) cGrupo.getSelectedItem();
                        String idg = grup.getID();
                        idalumnog(codigo.getText());
                        String alumnoid = "" + idalumno;
                        Object[] valores2 = {alumnoid, idg};

                        seguardo = peticiones.guardarRegistros(nombreTabla2, campos2, valores2);
                        if (seguardo) {

                            AccesoDatos ac = new AccesoDatos();
                            Calendar a = ProyeccionPagos.convierteacalendar(fechaini.getText());
                            float cole = Float.parseFloat(colegiatura.getText());
                            Calendar b = ProyeccionPagos.convierteacalendar(fechafin.getText());

                            String sql = ProyeccionPagos.calculapagos(a, b, cole, idg);

                            int pagos = ac.agregarRegistrosql("INSERT INTO PAGOS (mes_idmes,año,monto,fechavencimiento,grupo_idgrupo) VALUES " + sql);

                            if (pagos == 1) {
                            } else {
                                JOptionPane.showInternalMessageDialog(this, "Los pagos no se Guardaron", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            //String mes = "" + fechafin.getTime().getMonth();
                            //String año = "" + fechafin.getTime().getYear();
                            //String mesaño = mes + "-" + año;
                            filaseleccionada();
                            JOptionPane.showInternalMessageDialog(this, "El grupo se ha asignado Correctamente", "Guardar", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showInternalMessageDialog(this, "El grupo no se Guardo", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showInternalMessageDialog(this, "El grupo ya fue asiganado al alumno\n"
                                + " porfavor selecciones uno diferente ", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showInternalMessageDialog(this, "Debe seleccionar un alumno antes de asignar Nuevo Grupo", "Mensage", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_buttonAction1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanelBusqueda;
    private javax.swing.JPanel JPanelCampos;
    private javax.swing.JPanel JPanelTable;
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
    private elaprendiz.gui.button.ButtonAction buttonAction1;
    public static javax.swing.JComboBox cDia;
    public static javax.swing.JComboBox cGrupo;
    private elaprendiz.gui.textField.TextField carrera;
    private elaprendiz.gui.textField.TextField codigo;
    private javax.swing.JFormattedTextField colegiatura;
    private elaprendiz.gui.textField.TextField direccion;
    private elaprendiz.gui.textField.TextField dpi;
    private javax.swing.JRadioButton estado;
    private elaprendiz.gui.textField.TextField fechafin;
    private elaprendiz.gui.textField.TextField fechaini;
    private com.toedter.calendar.JDateChooser fechainicio;
    private com.toedter.calendar.JDateChooser fechanacimiento;
    private elaprendiz.gui.textField.TextField horaa;
    private elaprendiz.gui.textField.TextField horade;
    private javax.swing.JTable horarios;
    private javax.swing.JFormattedTextField inscripcion;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private elaprendiz.gui.textField.TextField nombres;
    private elaprendiz.gui.panel.PanelImage panelImage;
    private javax.swing.JPanel pnlActionButtons;
    private javax.swing.JPanel pnlPaginador;
    private elaprendiz.gui.textField.TextField profesor;
    private javax.swing.JRadioButton rbApellido;
    private javax.swing.JRadioButton rbCodigo;
    private javax.swing.JRadioButton rbNombre;
    private javax.swing.JComboBox sexo;
    private elaprendiz.gui.textField.TextField telefono;
    private elaprendiz.gui.textField.TextField titularapellido;
    private elaprendiz.gui.textField.TextField titularnombre;
    // End of variables declaration//GEN-END:variables
}
