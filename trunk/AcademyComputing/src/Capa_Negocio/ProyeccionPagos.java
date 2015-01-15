/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Capa_Negocio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 30234
 */
public class ProyeccionPagos {

    /*
     * metodo para convertir String a Calendar y poderlo manipular
     * devuelve un Calendar 
     */
    public static Calendar convierteacalendar(String fecha) {
        //
        try {
            String dateStr = fecha;
            SimpleDateFormat curFormater = new SimpleDateFormat("dd-MM-yyyy");
            Date dateObj = curFormater.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateObj);
            return calendar;
        } catch (ParseException ex) {
            Logger.getLogger(ProyeccionPagos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String calculapagos(Calendar ini, Calendar fin, float colegiatura, String idg) {

        //int dia = fin.getTime().getDay();
        //int mes = fin.getTime().getMonth();
        //int  año = fin.getTime().getYear();
        int dia = fin.get(Calendar.DAY_OF_MONTH);
        int mes = fin.get(Calendar.MONTH) + 1;
        int año = fin.get(Calendar.YEAR);

        String mesaño = (mes + 1) + "-" + año;
//        dcFecha.getCalendar().get(Calendar.YEAR);
//            int dias = dcFecha.getCalendar().get(Calendar.DAY_OF_MONTH);
//            int mess = dcFecha.getCalendar().get(Calendar.MONTH) + 1;

        //System.out.print(dia+"-"+mes+"-"+año);
        String sql = sumarmes(ini, fin, mesaño, colegiatura, idg);
        //System.out.print(mesaño + "comparacion" + "\n\n");
        //sumarmes(fin, mesaño);

        return sql;
    }

    /*
     * metodo para sumar 30 dias a un Calendar 
     * devuelve un Calendar 
     */
    private static String sumarmes(Calendar fecha, Calendar fechafn, String mesaño, float colegiatura, String idg) {
        String fv = "", f = "";
        String sql = "";
        String venc = "08";
        boolean estado = false;

        while (fecha.before(fechafn)) {
            fecha.add(Calendar.MONTH, 1);
            String ff = FormatoFecha.getFormato(fecha.getTime(), FormatoFecha.D_M_A);
            fv = fecha.get(Calendar.YEAR) + "-" + fecha.get(Calendar.MONTH)+ "-" + venc;
            f = (fecha.get(Calendar.MONTH) + 1) + "-" + fecha.get(Calendar.YEAR);
            //System.out.print(f + "calculado" + "\n\n");
            if (!sql.equals("")) {
                sql = sql + ",";
            }
            sql = sql + "('" + (fecha.get(Calendar.MONTH))+ "','" + fecha.get(Calendar.YEAR) + "','" + colegiatura + "','" + fv + "','" + idg + "')";
            System.out.print(sql + "\n");
            
        }
        
        if (((fecha.get(Calendar.MONTH)+1)+"-"+fecha.get(Calendar.YEAR)).equals(((fechafn.get(Calendar.MONTH)+1)+"-"+fechafn.get(Calendar.YEAR)))) {
            fecha.add(Calendar.MONTH, 1);
            String ff = FormatoFecha.getFormato(fecha.getTime(), FormatoFecha.D_M_A);
            fv = fecha.get(Calendar.YEAR) + "-" + fecha.get(Calendar.MONTH)+ "-" + venc;
            f = (fecha.get(Calendar.MONTH) + 1) + "-" + fecha.get(Calendar.YEAR);
            //System.out.print(f + "calculado" + "\n\n");
            if (!sql.equals("")) {
                sql = sql + ",";
            }
            sql = sql + "('" + (fecha.get(Calendar.MONTH))+ "','" + fecha.get(Calendar.YEAR) + "','" + colegiatura + "','" + fv + "','" + idg + "')";
            System.out.print(sql + "\n");
        }
        //System.out.print(sql);
        return sql;
    }
}
