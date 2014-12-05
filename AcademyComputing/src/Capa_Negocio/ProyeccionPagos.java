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

    public static void calculapagos(Calendar ini, Calendar fin) {

        String mes = "" + fin.getTime().getMonth();
        String año = "" + fin.getTime().getYear();
        String mesaño = mes + "-" + año;

        sumarmes(ini, mesaño);
        sumarmes(fin, mesaño);

        //return null;
    }

    /*
     * metodo para sumar 30 dias a un Calendar 
     * devuelve un Calendar 
     */
    private static Calendar sumarmes(Calendar fecha, String mesaño) {
        String f = "";

        while (!f.equals(mesaño)) {
            fecha.add(Calendar.MONTH, 1);
            String ff=FormatoFecha.getFormato(fecha.getTime(), FormatoFecha.D_M_A);
            System.out.print(ff + "\n");
            f = ""+fecha.getTime().getYear();
            System.out.print(f+"\n");
        }
        return fecha;
    }
}
