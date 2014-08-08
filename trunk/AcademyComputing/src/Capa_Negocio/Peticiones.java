package Capa_Negocio;

import Capa_Datos.AccesoDatos;
import static Capa_Negocio.JOptionMessage.Datos;
import static Capa_Negocio.JOptionMessage.TituloDatos;
import static Capa_Presentacion.Alumno.msg;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

/**
 *
 * @author GLARA
 */
public class Peticiones extends AccesoDatos {

    /*Metodo guarda registros desde un procedimiento almacenado
     *Para mas detalle consultal la clase AccesoDatos.agregarRegistroPs( )
     */
    public boolean guardarRegistro(Object[] valores1, String ps) {
        int gravado;
        boolean resp;

        gravado = this.agregarRegistroPs(ps, valores1);

        if (gravado == 1) {
            resp = true;
        } else {
            resp = true;
        }
        return resp;

    }

    /**
     * Paa varias condiciones WHERE campo1=condicionid1 and campo2=condicionid2
     * ...
     *
     * @param modelo ,modelo de la JTable
     * @param tabla , el nombre de la tabla a consultar en la BD
     * @param campos , los campos de la tabla a consultar ejem: nombre, codigo ,
     * rirección etc
     * @param campocondicion , los campos de la tabla para las condiciones ejem:
     * id,estado etc
     * @param condicionid , los valores que se compararan con campocondicion
     * ejem: campocondicion = condicionid
     * @return
     */
    public DefaultTableModel getRegistroPorPks(DefaultTableModel modelo, String tabla, String[] campos, String[] campocondicion, String[] condicionid) {
        try {

            /*rs es un ResultSet, una tabla de datos que representan un conjunto de resultados de base de datos,
             * generados mediante la ejecución de una consulta a la base de datos en el metodo getRegistros()
             */
            rs = this.getRegistros(tabla, campos, campocondicion, condicionid);
            int cantcampos = campos.length;

            if (rs != null) {
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    rs.beforeFirst();//regresa el puntero al primer registro

                    Object[] fila = new Object[cantcampos];

                    while (rs.next()) {//mientras tenga registros 

                        // Se rellena cada posición del array con una de las columnas de la tabla del rs.
                        for (int i = 0; i < cantcampos; i++) {

                            fila[i] = rs.getObject(i + 1); // El primer indice en rs es el 1, no el cero, por eso se suma 1.
                            if (fila[i].equals("T")) {
                                fila[i] = "Activo";
                            }
                            if (fila[i].equals("F")) {
                                fila[i] = "Activo";
                            }
                            //System.out.print(fila[i] + "\n");
                        }
                        modelo.addRow(fila);
                    }
                } else {

                    msg.Error(Datos + " La busqeuda", TituloDatos);
                }
            }
            rs.close();
            return modelo;
        } catch (SQLException ex) {

            msg.Error(Datos + ": " + ex, TituloDatos);
            return null;
        }
    }

    /**
     * Para una condicion WHERE condicionid LIKE '% campocondicion'
     *
     * @param modelo ,modelo de la JTable
     * @param tabla , el nombre de la tabla a consultar en la BD
     * @param campos , los campos de la tabla a consultar ejem: nombre, codigo
     * ,dirección etc
     * @param campocondicion , los campos de la tabla para las condiciones ejem:
     * id,estado etc
     * @param condicionid , los valores que se compararan con campocondicion
     * ejem: campocondicion = condicionid
     * @return
     */
    public DefaultTableModel getRegistroPorLike(DefaultTableModel modelo, String tabla, String[] campos, String campocondicion, String condicionid) {
        try {
            rs = this.selectPorLike(tabla, campos, campocondicion, condicionid);
            int cantcampos = campos.length;
            if (rs != null) {
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    //int count = 0;
                    rs.beforeFirst();//regresa el puntero al primer registro
                    Object[] fila = new Object[cantcampos];

                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        // Se rellena cada posición del array con una de las columnas de la tabla en base de datos.
                        for (int i = 0; i < cantcampos; i++) {

                            fila[i] = rs.getObject(i + 1); // El primer indice en rs es el 1, no el cero, por eso se suma 1.
                            if (fila[i].equals("T")) {
                                fila[i] = "Activo";
                            }
                            if (fila[i].equals("F")) {
                                fila[i] = "Activo";
                            }
                            System.out.print(fila[i] + "\n");
                        }
                        modelo.addRow(fila);
                        //count = count + 1;
                    }

                } else {
                    msg.Error(Datos + " " + condicionid, TituloDatos);
                }
            }
            rs.close();
            return modelo;
        } catch (SQLException ex) {
            msg.Error(Datos + ": " + ex, TituloDatos);
            return null;
        }
    }

    /**
     * Para una condición WHERE campocondicion = id
     *
     * @param cmps , los componentes JComboBox , JTextFiel etc
     * @param tabla , el nombre de la tabla a consultar en la BD
     * @param campos , los campos de la tabla a consultar ejem: nombre, codigo
     * ,dirección etc
     * @param campocondicion, el campo de la tabla para las condiciones ejem:
     * idalumno etc
     * @param id , los valores que se compararan con campocondicion ejem: "lara"
     */
    public void getRegistroSeleccionado(Component[] cmps, String tabla, String[] campos, String campocondicion, String id) {
        try {
            rs = this.selectPorLike(tabla, campos, campocondicion, id);
            int cantcampos = campos.length;
            Object[] fila = new Object[cantcampos];

            if (rs != null) {
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    //int count = 0;
                    rs.beforeFirst();//regresa el puntero al primer registro

                    while (rs.next()) {//mientras tenga registros 
                        // Se rellena cada posición del array con una de las columnas de la tabla en base de datos.
                        for (int i = 0; i < cantcampos; i++) {

                            /*
                             * Agregar los componenetes que falten 
                             */
                            if (cmps[i] instanceof JTextField) {
                                JTextComponent tmp = (JTextComponent) cmps[i];
                                tmp.setText(rs.getString(i + 1));
                                if (rs.getString(i + 1).equals("T")) {
                                    tmp.setText("Activo");
                                }
                                if (rs.getString(i + 1).equals("F")) {
                                    tmp.setText("Inactivo");
                                }
                                continue;
                            } else if (cmps[i] instanceof JDateChooser) {
                                JDateChooser tmp = (JDateChooser) cmps[i];
                                tmp.setDate((rs.getDate(i + 1)));
                                continue;
                            } else if (cmps[i] instanceof JRadioButton) {
                                JRadioButton tmp = (JRadioButton) cmps[i];

                                if (rs.getString(i + 1).equals("T")) {
                                    tmp.setSelected(true);
                                    tmp.setBackground(new java.awt.Color(102, 204, 0));
                                } else {
                                    tmp.setSelected(false);
                                    tmp.setBackground(Color.red);
                                }
                                continue;
                            }

                            System.out.print(fila[i] + "\n");
                        }
                    }

                } else {
                    msg.Error(Datos + " " + id, TituloDatos);
                }
            }
            rs.close();

        } catch (SQLException ex) {
            msg.Error(Datos + ": " + ex, TituloDatos);
        }
    }

}
