package Capa_Negocio;

import javax.swing.JOptionPane;

/**
 *
 * @author GLARA
 */
public class GeneraCodigo {

    public static final String PREFIJO_ARTICULO = "AR";
    public static final String PREFIJO_CLIENTE = "CL";
    public static final String PREFIJO_VENDEDOR = "VE";

    //Genera un codigo con la nomenclatuda indicada (pendiente modificar a nomenclatura del colegio)
    
    public static synchronized String generarCodigo(String cadena, String prefijo) {
        String pre = "";
        int longitudPrefijo = 0;

        if (cadena == null || cadena.isEmpty()) {
            throw new NullPointerException("cadena nula o vacia. solo el prefijo puede ser nulo o vacio");
        }

        if (prefijo != null) {
            pre = prefijo;
            String pre2 = cadena.substring(0, pre.length());
            if (!pre.equals(pre2)) {

                JOptionPane.showMessageDialog(null, "El prefijo: " + pre2 + " de la cadena " + cadena + " no coincide con el prefijo " + pre);

                //throw new ExceptionValoresNoIgual("El prefijo: " + pre2 + " de la cadena "
                //        + cadena + " no coincide con el prefijo " + pre);
            }
            if (!prefijo.equals(PREFIJO_ARTICULO) || !prefijo.equals(PREFIJO_CLIENTE)
                    || !prefijo.equals(PREFIJO_VENDEDOR)) {
                longitudPrefijo = contadorLetras(cadena);
            } else {
                longitudPrefijo = prefijo.length();
            }
        } else {
            longitudPrefijo = contadorLetras(cadena);
            prefijo = "";
        }

        StringBuilder cadOriginal = new StringBuilder(cadena);
        String tmpCadena = cadOriginal.toString();

        if (longitudPrefijo > 0) {
            tmpCadena = cadOriginal.substring(longitudPrefijo);
        }

        int valorEntero;
        try {
            valorEntero = Integer.parseInt(tmpCadena);
        } catch (NumberFormatException ex) {

            System.err.println(tmpCadena + " tiene un formato no valido.\n"
                    + "ejem: formato valido: PREJ0000015,pref0000025,0000158");
            return null;
        }
        valorEntero += 1;
        if (valorEntero <= 9) {
            tmpCadena = tmpCadena.substring(0, tmpCadena.length() - 1);
            tmpCadena = prefijo + tmpCadena + valorEntero;
        } else {
            String ctmp = "" + valorEntero;
            if (ctmp.length() > tmpCadena.length()) {
                System.out.println("Valor maximo alcansado: no se puede generar mas codigos");
                return null;
            }
            if (ctmp.length() == tmpCadena.length()) {
                tmpCadena = pre + ctmp;
            } else {

                int posicion = tmpCadena.length() - ctmp.length();
                String ceros = tmpCadena.substring(0, posicion);
                tmpCadena = pre + ceros + ctmp;
            }
        }
        return tmpCadena;
    }

    /*
     * Cuenta el numero de letras de cualquier cadena dada.
     */
    private static int contadorLetras(String cadena) {
        char[] letras = cadena.toCharArray();
        int contador = 0;
        for (int i = 0; i < letras.length; i++) {
            if (Character.isLetter(letras[i])) {
                contador++;
            }
        }
        return contador;
    }

}
