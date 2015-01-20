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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import modelos.mGrupo;

/**
 *
 * @author GLARA
 */
public class Pagos extends javax.swing.JInternalFrame {

    /*El modelo se define en : Jtable-->propiedades-->model--> <User Code> */
    DefaultTableModel model, model2;
    DefaultComboBoxModel modelCombo;
    String[] titulos = {"Id", "Codigo", "Descripción", "Año", "Monto", "Fecha V", "Mora", "Subtotal", "Pagar Mora", "Pagar"};//Titulos para Jtabla
    String[] titulos2 = {"Código", "Descripción", "Precio", "Cantidad", "SubTotal", "Check"};//Titulos para Jtabla
    /*Se hace una instancia de la clase que recibira las peticiones de esta capa de aplicación*/
    Peticiones peticiones = new Peticiones();
    public static Hashtable<String, String> hashGrupo = new Hashtable<>();
    AccesoDatos acceso = new AccesoDatos();
    static String idalumno = "", iddetallegrupo = "";

    /**
     * Creates new form Cliente
     */
    public Pagos() {
        initComponents();
        setFiltroTexto();
        addEscapeKey();

        cGrupo.addItemListener(
                (ItemEvent e) -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        selecciongrupo();
                    }
                });

        colegiaturas.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                sumartotal();
                //formatotabla();
            }
        });
        //colegiaturas.getColumnModel().getColumn(8).setCellEditor(new Editor_CheckBox());
        colegiaturas.getColumnModel().getColumn(8).setCellEditor(new Editor_CheckBox());
        colegiaturas.getColumnModel().getColumn(9).setCellEditor(new Editor_CheckBox());

        otrosproductos.getColumnModel().getColumn(5).setCellEditor(new Editor_CheckBox());

        //para pintar la columna con el CheckBox en la tabla, en este caso, la primera columna
        //colegiaturas.getColumnModel().getColumn(8).setCellRenderer(new Renderer_CheckBox());
        //colegiaturas.getColumnModel().getColumn(7).setCellRenderer(new Renderer_CheckBox());
        colegiaturas.getColumnModel().getColumn(8).setCellRenderer(new Renderer_CheckBox());
        colegiaturas.getColumnModel().getColumn(9).setCellRenderer(new Renderer_CheckBox());

        otrosproductos.getColumnModel().getColumn(5).setCellRenderer(new Renderer_CheckBox());

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
            //this.bntNuevo.setEnabled(true);
            removejtable();
            codigoa.setText("");
            codigoa.requestFocus();
            this.dispose();
        }
    }

    /* Para no sobrecargar la memoria y hacer una instancia cada vez que actualizamos la JTable se hace una
     * sola instancia y lo unico que se hace antes de actualizar la JTable es limpiar el modelo y enviarle los
     * nuevos datos a mostrar en la JTable  */
    public void removejtable() {
        while (colegiaturas.getRowCount() != 0) {
            model.removeRow(0);
        }
    }

    public void removejtable2() {
        while (otrosproductos.getRowCount() != 0) {
            model2.removeRow(0);
        }
    }

    public void sumartotal() {
        //System.out.print("sumar total");
        //corregir cuando hay solo unalinea da error
        if (colegiaturas.getRowCount() == 0 && colegiaturas.getSelectedRow() == -1) {
            //JOptionPane.showMessageDialog(null, "La tabla no contiene datos que modificar");
            totalapagar.setValue(0.0);
        } else {
            float Actual, Resultado = 0;

            for (int i = 0; i < model.getRowCount(); i++) {

                if (colegiaturas.getValueAt(i, 9).toString().equals("true") /*&& colegiaturas.getValueAt(i, 9).toString().equals(true)*/) {
                    //0     1        2            3        4          5        6      7         8          9
                    //"Id", "Codigo", "Descripción", "Año", "Monto", "Fecha V", "Mora","Subtotal","Exonerar","Pagar"
                    if (colegiaturas.getValueAt(i, 8).toString().equals("true")) {
                        Actual = Float.parseFloat(colegiaturas.getValueAt(i, 7).toString());
                        Resultado = Resultado + Actual;
                    } else if (colegiaturas.getValueAt(i, 8).toString().equals("false")) {
                        Actual = Float.parseFloat(colegiaturas.getValueAt(i, 4).toString());
                        Resultado = Resultado + Actual;
                    }
                }
                totalapagar.setValue(Math.round(Resultado * 100.0) / 100.0);
            }
        }
    }

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

                modeloComboBox.addElement(new mGrupo("SELECCIONE GRUPO", "0"));
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

    /* Metodo que llena los campos con la información de grupo
     * Tambien llena en la pestaña de coelgiatura lo que el alumno tiene pendiente de pago
     */
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
            removejtable();
            removejtable2();
            inicioalumno.setText("");
            beca.setText("");
            dia.setText("");

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
                            idalumnosengrupo(idalumno, "" + grup.getID());
                            MostrarPagos();
                            MostrarProductos();
                        }
                    } catch (SQLException e) {
                        JOptionPane.showInternalMessageDialog(this, e);
                    }
                }

            }
        }
    }

    /*
     * Metodo para buscar un alumno por su codigo devuelde el id
     */
    public void balumnocodigo(String codigo) {
        if (codigo.isEmpty()) {
            nombrealumno.setText("");
            beca.setText("");
            //inicioalumno.setDate(null);
            estado.setText("");
            cGrupo.removeAllItems();
            idalumno = "";
            inicioalumno.setText("");
            beca.setText("");
            dia.setText("");

        } else if (!codigo.isEmpty()) {

            ResultSet rs;
            AccesoDatos ac = new AccesoDatos();

            String[] campos = {"alumno.codigo", "alumno.nombres", "alumno.apellidos", "DATE_FORMAT(alumno.fechanacimiento,'%d-%m-%Y')", "alumno.estado", "alumno.idalumno"};
            String[] cond = {"alumno.codigo"};
            String[] id = {codigo};

            if (!id.equals(0)) {

                rs = ac.getRegistros("alumno", campos, cond, id, "");

                if (rs != null) {
                    try {
                        if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                            rs.beforeFirst();//regresa el puntero al primer registro
                            while (rs.next()) {//mientras tenga registros que haga lo siguiente
                                codigoa.setText(rs.getString(1));
                                llenarcombogrupo(rs.getString(1));
                                nombrealumno.setText(rs.getString(2) + " " + rs.getString(3));
                                //float becac=Float.parseFloat(rs.getString(5));
                                //beca.setText(""+becac);
                                //Date fechaini = FormatoFecha.StringToDate(rs.getString(6));
                                //inicioalumno.setDate(fechaini);

                                if (rs.getString(5).equals("0")) {
                                    estado.setText("Inactivo");
                                    estado.setForeground(Color.red);
                                } else if (rs.getString(5).equals("1")) {
                                    estado.setText("Activo");
                                    estado.setForeground(Color.WHITE/*new java.awt.Color(102, 204, 0)*/);
                                }

                                idalumno = (rs.getString(6));

                            }
                        } else {
                            JOptionPane.showInternalMessageDialog(this, " El codigo no fue encontrado ");
                            nombrealumno.setText("");
                            beca.setText("");
                            inicioalumno.setText("");
                            estado.setText("");
                            cGrupo.removeAllItems();
                            idalumno = "";

                            removejtable();
                            profesor.setText("");
                            carrera.setText("");
                            horade.setText("");
                            horaa.setText("");
                            fechainicio.setText("");
                            fechafin.setText("");
                            inscripcion.setValue(null);
                            colegiatura.setValue(null);
                            dia.setText("");
                            codigoa.requestFocus();
                        }
                    } catch (SQLException e) {
                        JOptionPane.showInternalMessageDialog(this, e);
                    }
                } else {
                    JOptionPane.showInternalMessageDialog(this, " El codigo no fue encontrado ");
                    nombrealumno.setText("");
                    beca.setText("");
                    inicioalumno.setText("");
                    estado.setText("");
                    cGrupo.removeAllItems();
                    idalumno = "";

                    removejtable();
                    profesor.setText("");
                    carrera.setText("");
                    horade.setText("");
                    horaa.setText("");
                    fechainicio.setText("");
                    fechafin.setText("");
                    inscripcion.setValue(null);
                    colegiatura.setValue(null);
                    dia.setText("");
                    codigoa.requestFocus();
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
    private void MostrarPagos() {

        String sql = "SELECT proyeccionpagos.idproyeccionpagos,proyeccionpagos.mes_idmes,mes.mes,proyeccionpagos.año,proyeccionpagos.monto,\n"
                + "     proyeccionpagos.fechavencimiento,IFNULL((SELECT mora.mora FROM mora where proyeccionpagos.idproyeccionpagos = mora.proyeccionpagos_idproyeccionpagos),0.0) AS 'Mora',proyeccionpagos.alumnosengrupo_iddetallegrupo FROM\n"
                + "     mes INNER JOIN proyeccionpagos ON mes.idmes = proyeccionpagos.mes_idmes  where alumnosengrupo_iddetallegrupo='" + iddetallegrupo + "' and proyeccionpagos.estado='0' order by proyeccionpagos.idproyeccionpagos asc ";

        removejtable();
        model = getRegistroPorLikel(model, sql);
        Utilidades.ajustarAnchoColumnas(colegiaturas);
    }

    /**
     * Para una condicion WHERE condicionid LIKE '% campocondicion' * @param
     * modelo ,modelo de la JTable
     *
     * @param tabla , el nombre de la tabla a consultar en la BD
     * @param campos , los campos de la tabla a consultar ejem: nombre, codigo
     * ,dirección etc
     * @param campocondicion , los campos de la tabla para las condiciones ejem:
     * id,estado etc
     * @param condicionid , los valores que se compararan con campocondicion
     * ejem: campocondicion = condicionid
     * @return
     */
    public DefaultTableModel getRegistroPorLikel(DefaultTableModel modelo, String tabla) {
        try {

            ResultSet rs;

            rs = acceso.getRegistroProc(tabla);
            int cantcampos = 9;
            //if (rs != null) {
            if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                //int count = 0;
                rs.beforeFirst();//regresa el puntero al primer registro
                Object[] fila = new Object[cantcampos + 1];

                while (rs.next()) {//mientras tenga registros que haga lo siguiente
                    // Se rellena cada posición del array con una de las columnas de la tabla en base de datos.
                    for (int i = 0; i < cantcampos - 2; i++) {

                        fila[i] = rs.getObject(i + 1); // El primer indice en rs es el 1, no el cero, por eso se suma 1.
                        System.out.print("\n" + fila[i] + "--" + i);
                        if (i == 4) {
                            float monto = (float) rs.getObject(i + 1);
                            float cbeca = Float.parseFloat(beca.getText());
                            float resultado = (float) (Math.round((monto - cbeca) * 100.0) / 100.0);

                            fila[i] = resultado;
                        }
                        if (i == 6) {
                            if (fila[i] == "0.0") {
                                fila[i] = "0.0";
                            } else {
                                float mora = (float) rs.getFloat(i + 1);
                                //float cbeca = Float.parseFloat(beca.getText());
                                float resultado = (float) (Math.round(mora * 100.0) / 100.0);
                                fila[i] = resultado;
                            }

                        }
                        if (fila[i] == null) {
                            fila[i] = "";
                        } else {
                        }
                    }
                    //int mor = 5;
                    //fila[6] = mor;
                    fila[7] = (float) (Math.round(((float) fila[4] + ((float) fila[6])) * 100.0) / 100.0);

                    if (((float) fila[6] == 0.0)) {
                        fila[8] = false;
                    } else {
                        fila[8] = true;
                    }
                    fila[9] = false;
                    modelo.addRow(fila);
                }

            } //} 
            else {
                // JOptionPane.showMessageDialog(null, "No se encontraron datos para la busqueda", "Mensage", JOptionPane.INFORMATION_MESSAGE);
            }
            rs.close();
            return modelo;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrio un Error :" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void MostrarProductos() {

        String sql = "SELECT otrospagos.idpago,otrospagos.descripcion,otrospagos.costo FROM otrospagos order by otrospagos.descripcion";

        removejtable2();
        model2 = getRegistroPorLikell(model2, sql);
        Utilidades.ajustarAnchoColumnas(otrosproductos);
    }

    /**
     * Para una condicion WHERE condicionid LIKE '% campocondicion' * @param
     * modelo ,modelo de la JTable
     *
     * @param tabla , el nombre de la tabla a consultar en la BD
     * @param campos , los campos de la tabla a consultar ejem: nombre, codigo
     * ,dirección etc
     * @param campocondicion , los campos de la tabla para las condiciones ejem:
     * id,estado etc
     * @param condicionid , los valores que se compararan con campocondicion
     * ejem: campocondicion = condicionid
     * @return
     */
    public DefaultTableModel getRegistroPorLikell(DefaultTableModel modelo, String tabla) {
        try {

            ResultSet rs;

            rs = acceso.getRegistroProc(tabla);
            int cantcampos = 6;
            //if (rs != null) {
            if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                //int count = 0;
                rs.beforeFirst();//regresa el puntero al primer registro
                Object[] fila = new Object[cantcampos];

                while (rs.next()) {//mientras tenga registros que haga lo siguiente
                    // Se rellena cada posición del array con una de las columnas de la tabla en base de datos.
//                    for (int i = 0; i < cantcampos-1; i++) {
//                            fila[i] = rs.getObject(i + 1); // El primer indice en rs es el 1, no el cero, por eso se suma 1.
//                        if (fila[i] == null) {
//                            fila[i] = "";
//                        } else {
//                        }
//                    }
//                    fila[3] = "";
//                    fila[4] = "";
//                    fila[5] = false;

                    fila[0] = rs.getString(1);
                    fila[1] = rs.getString(2);
                    fila[2] = Float.parseFloat(rs.getString(3));
                    fila[3] = "";
                    fila[4] = "";
                    fila[5] = false;
                    modelo.addRow(fila);
                }

            } //} 
            else {
                // JOptionPane.showMessageDialog(null, "No se encontraron datos para la busqueda", "Mensage", JOptionPane.INFORMATION_MESSAGE);
            }
            rs.close();
            return modelo;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrio un Error :" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
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

    public void idalumnosengrupo(String idalumno, String idgrupo) {

        String[] id = {idalumno, idgrupo};
        ResultSet rs;
        AccesoDatos ac = new AccesoDatos();
        String[] cond = {"alumnosengrupo.alumno_idalumno", "alumnosengrupo.grupo_idgrupo"};
        String[] campos = {"alumnosengrupo.iddetallegrupo", "alumnosengrupo.fechainicio", "alumnosengrupo.beca"};
        rs = ac.getRegistros("alumnosengrupo", campos, cond, id, "");

        if (rs != null) {
            try {
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    rs.beforeFirst();//regresa el puntero al primer registro
                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        iddetallegrupo = (rs.getString(1));
                        String fechainicio = FormatoFecha.getFormato(rs.getDate(2), FormatoFecha.D_M_A);
                        inicioalumno.setText(fechainicio);
                        //System.out.print(fechainicio + "\n");
                        float becac = Float.parseFloat(rs.getString(3));
                        beca.setText("" + becac);
                        //System.out.print(becac + "\n");
                    }
                }
            } catch (SQLException e) {
                iddetallegrupo = "";
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
        jLabel26 = new javax.swing.JLabel();
        inicioalumno = new elaprendiz.gui.textField.TextField();
        jLabel25 = new javax.swing.JLabel();
        beca = new elaprendiz.gui.textField.TextField();
        JPanelTable = new javax.swing.JPanel();
        tbPane = new elaprendiz.gui.panel.TabbedPaneHeader();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        colegiaturas = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        otrosproductos = new javax.swing.JTable();
        JPanelBusqueda = new javax.swing.JPanel();
        codigoa = new elaprendiz.gui.textField.TextField();
        jLabel16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        nombrealumno = new elaprendiz.gui.textField.TextField();
        jLabel19 = new javax.swing.JLabel();
        estado = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tbPane1 = new elaprendiz.gui.panel.TabbedPaneHeader();
        jPanel5 = new javax.swing.JPanel();
        buttonAction1 = new elaprendiz.gui.button.ButtonAction();
        buttonAction2 = new elaprendiz.gui.button.ButtonAction();
        totalapagar = new javax.swing.JFormattedTextField();
        jLabel27 = new javax.swing.JLabel();
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

        bntModificar.setBackground(new java.awt.Color(51, 153, 255));
        bntModificar.setMnemonic(KeyEvent.VK_M);
        bntModificar.setText("Modificar");
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
        bntSalir.setText("Salir    ");
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
        cGrupo.setBounds(80, 30, 210, 24);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Grupo:");
        JPanelCampos.add(jLabel7);
        jLabel7.setBounds(10, 30, 60, 27);

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
        carrera.setName("codigo"); // NOI18N
        carrera.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(carrera);
        carrera.setBounds(320, 80, 260, 24);

        profesor.setEditable(false);
        profesor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        profesor.setName("codigo"); // NOI18N
        profesor.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(profesor);
        profesor.setBounds(320, 30, 260, 24);

        inscripcion.setEditable(false);
        inscripcion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        inscripcion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inscripcion.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        inscripcion.setName("inscripcion"); // NOI18N
        inscripcion.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(inscripcion);
        inscripcion.setBounds(600, 130, 110, 24);

        colegiatura.setEditable(false);
        colegiatura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        colegiatura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        colegiatura.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        colegiatura.setName("colegiatura"); // NOI18N
        colegiatura.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(colegiatura);
        colegiatura.setBounds(730, 130, 110, 24);

        horaa.setEditable(false);
        horaa.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        horaa.setName("codigo"); // NOI18N
        horaa.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(horaa);
        horaa.setBounds(730, 30, 110, 24);

        horade.setEditable(false);
        horade.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        horade.setName("codigo"); // NOI18N
        horade.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(horade);
        horade.setBounds(600, 30, 110, 24);

        fechainicio.setEditable(false);
        fechainicio.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fechainicio.setName("codigo"); // NOI18N
        fechainicio.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(fechainicio);
        fechainicio.setBounds(600, 80, 110, 24);

        fechafin.setEditable(false);
        fechafin.setHorizontalAlignment(javax.swing.JTextField.CENTER);
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
        dia.setName("codigo"); // NOI18N
        dia.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(dia);
        dia.setBounds(320, 130, 260, 24);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Fecha Inicio Alumno:");
        JPanelCampos.add(jLabel26);
        jLabel26.setBounds(10, 80, 160, 24);

        inicioalumno.setEditable(false);
        inicioalumno.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        inicioalumno.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        inicioalumno.setName("codigo"); // NOI18N
        inicioalumno.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(inicioalumno);
        inicioalumno.setBounds(170, 80, 120, 24);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Beca Alumno: Q.");
        JPanelCampos.add(jLabel25);
        jLabel25.setBounds(40, 130, 130, 27);

        beca.setEditable(false);
        beca.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        beca.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        beca.setName("codigo"); // NOI18N
        beca.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(beca);
        beca.setBounds(170, 130, 120, 24);

        panelImage.add(JPanelCampos);
        JPanelCampos.setBounds(0, 160, 880, 170);

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
                    if(column==8 || column==9){
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

            jPanel3.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 756, 210));

            tbPane.addTab("Colegiatura", jPanel3);

            jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

            jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            jScrollPane2.setOpaque(false);

            otrosproductos.setModel(model2 = new DefaultTableModel(null, titulos2)
                {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        if(column==5){
                            return true;
                        }else{
                            return false;}
                    }
                });
                otrosproductos.setName("otrosproductos"); // NOI18N
                otrosproductos.setOpaque(false);
                jScrollPane2.setViewportView(otrosproductos);

                jPanel4.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 756, 210));

                tbPane.addTab("Otros Pagos", jPanel4);

                JPanelTable.add(tbPane, java.awt.BorderLayout.CENTER);

                panelImage.add(JPanelTable);
                JPanelTable.setBounds(0, 330, 760, 250);

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
                nombrealumno.setBounds(440, 10, 360, 24);

                jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
                jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
                jLabel19.setText("Alumno:");
                JPanelBusqueda.add(jLabel19);
                jLabel19.setBounds(310, 10, 120, 24);

                estado.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
                estado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                JPanelBusqueda.add(estado);
                estado.setBounds(700, 50, 110, 27);

                panelImage.add(JPanelBusqueda);
                JPanelBusqueda.setBounds(0, 110, 880, 50);

                jPanel1.setBackground(new java.awt.Color(51, 51, 51));
                jPanel1.setLayout(new java.awt.BorderLayout());

                tbPane1.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
                tbPane1.setOpaque(true);

                jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

                buttonAction1.setText("Calcular");
                buttonAction1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
                buttonAction1.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        buttonAction1ActionPerformed(evt);
                    }
                });
                jPanel5.add(buttonAction1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 130, 100, -1));

                buttonAction2.setText("Otros Pagos");
                buttonAction2.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
                buttonAction2.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        buttonAction2ActionPerformed(evt);
                    }
                });
                jPanel5.add(buttonAction2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 170, 100, -1));

                totalapagar.setEditable(false);
                totalapagar.setBackground(new java.awt.Color(204, 255, 102));
                totalapagar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
                totalapagar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                totalapagar.setToolTipText("");
                totalapagar.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
                totalapagar.setName("inscripcion"); // NOI18N
                totalapagar.setPreferredSize(new java.awt.Dimension(80, 23));
                jPanel5.add(totalapagar, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 60, 105, 40));

                jLabel27.setBackground(new java.awt.Color(255, 204, 0));
                jLabel27.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
                jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                jLabel27.setText("Total Q.");
                jLabel27.setOpaque(true);
                jPanel5.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 20, 105, 30));

                tbPane1.addTab("============", jPanel5);

                jPanel1.add(tbPane1, java.awt.BorderLayout.CENTER);
                tbPane1.getAccessibleContext().setAccessibleName("TOTAL");

                panelImage.add(jPanel1);
                jPanel1.setBounds(760, 330, 120, 250);

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
                codigo3.setBounds(690, 30, 110, 27);

                jLabel22.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
                jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                jLabel22.setText("No. Recibo");
                JPanelRecibo.add(jLabel22);
                jLabel22.setBounds(690, 10, 110, 19);

                clockDigital2.setForeground(java.awt.Color.blue);
                clockDigital2.setFont(new java.awt.Font("Microsoft Sans Serif", 1, 16)); // NOI18N
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
        inicioalumno.setText("");
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
        // TODO add your handling code here  
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

    private void bntModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntModificarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bntModificarActionPerformed

    private void bntGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntGuardarActionPerformed
        // TODO add your handling code here:
        if (colegiaturas.getRowCount() == 0 && colegiaturas.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "La tabla no contiene datos");
            //totalapagar.setValue(0.0);
        } else {

            int resp = JOptionPane.showInternalConfirmDialog(this, "¿Desea Grabar el Registro?", "Pregunta", 0);
            if (resp == 0) {

                //GUARDAR MESES DE PAGO
                String nomTabla = "proyeccionpagos";
                String columnaId = "idproyeccionpagos";
                int seguardo = 0;
                AccesoDatos ac = new AccesoDatos();
                String campos = "estado";
                boolean camprec = false;
                int cant = model.getRowCount();

                try {
                    colegiaturas.getCellEditor().stopCellEditing();
                } catch (Exception e) {
                }

                for (int i = 0; i < cant; i++) {
                    if (colegiaturas.getValueAt(i, 9).toString().equals("true")) {
                        camprec = true;
                        String id = (String) "" + colegiaturas.getValueAt(i, 0);
                        seguardo = ac.eliminacionTemporal(nomTabla, campos, columnaId, id, 1);
                    }
                }
                if (!camprec) {
                    JOptionPane.showInternalMessageDialog(this, "No se ha marcado ningun Pago", "Mensage", JOptionPane.INFORMATION_MESSAGE);
                }
                if (seguardo >= 1) {
                    //JOptionPane.showInternalMessageDialog(this, "El dato se ha Guardado Correctamente", "Guardar", JOptionPane.INFORMATION_MESSAGE);
                    int resp2 = JOptionPane.showInternalConfirmDialog(this, "El Pago se ha Guardado Correctamente\n ¿Desea realizar otro Pago de este Alumno?", "Pregunta", 0);
                    if (resp2 == 0) {
                        mGrupo grup = (mGrupo) cGrupo.getSelectedItem();
                        String[] id = {grup.getID()};
                        idalumnosengrupo(idalumno, "" + grup.getID());
                        MostrarPagos();
                        MostrarProductos();
                    } else {
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
                        inicioalumno.setText("");
                        dia.setText("");
                        cGrupo.setSelectedIndex(-1);
                        codigoa.requestFocus();
                    }
                }
            }
        }
    }//GEN-LAST:event_bntGuardarActionPerformed

    private void buttonAction2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAction2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonAction2ActionPerformed

    private void buttonAction1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAction1ActionPerformed
        // TODO add your handling code here:
        try {
            colegiaturas.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }
        sumartotal();
    }//GEN-LAST:event_buttonAction1ActionPerformed


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
    public static elaprendiz.gui.textField.TextField inicioalumno;
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
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    public static elaprendiz.gui.textField.TextField nombrealumno;
    private javax.swing.JTable otrosproductos;
    private elaprendiz.gui.panel.PanelImage panelImage;
    private javax.swing.JPanel pnlActionButtons;
    private javax.swing.JPanel pnlPaginador1;
    private javax.swing.JPopupMenu popupcarrera;
    private javax.swing.JPopupMenu popupprofesor;
    private javax.swing.JPopupMenu popuppromatricula;
    private elaprendiz.gui.textField.TextField profesor;
    private elaprendiz.gui.panel.TabbedPaneHeader tbPane;
    private elaprendiz.gui.panel.TabbedPaneHeader tbPane1;
    private javax.swing.JFormattedTextField totalapagar;
    // End of variables declaration//GEN-END:variables
}
