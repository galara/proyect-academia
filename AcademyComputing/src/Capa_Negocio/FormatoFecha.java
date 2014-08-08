package Capa_Negocio;

import static Capa_Negocio.JOptionMessage.ErrorDatos;
import static Capa_Negocio.JOptionMessage.TituloErrDatos;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author GLARA
 */
public class FormatoFecha {

    private static SimpleDateFormat formato;
    public static final int A_M_D = 1;
    public static final int D_M_A = 2;
    private static Date fechaString;
    public static JOptionMessage msg;

    /**
     * Este metodo recibe un Date y le coloca un formato de año-mes-dia o dia-mes-año devuelve
     * un String con el formato indicado.
     *
     * @param fecha
     * @param tipoFormato
     * @return
     */
    public static String getFormato(Date fecha, int tipoFormato) {
        formato = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            if (tipoFormato == D_M_A) {
                formato = new SimpleDateFormat("dd-MM-yyyy");
            }
            if (tipoFormato == A_M_D) {
                formato = new SimpleDateFormat("yyy/MM/dd");
            }
        } catch (Exception ex) {
         msg.Error(ErrorDatos + ": " + ex, TituloErrDatos);
        }
        return formato.format((Date) fecha);
    }

    /**
     * Este metodo recibe un String y lo comvierte a Date le coloca un formato
     * de año-mes-dia devuelve un Date con el formato indicado.
     *
     * @param text
     * @return
     */
    public static Date StringToDate(String text) {
        formato = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fechaString = (Date) formato.parseObject(text);
        } catch (ParseException ex) {
            msg.Error(ErrorDatos + ": " + ex, TituloErrDatos);
        }
        return fechaString;
    }
}
