package Capa_Negocio;

import javax.swing.JOptionPane;

/**
 *
 * @author GLARA
 */

//Pendiente no funciona con JOptionPane.shwInternalFrame ,cambiar mensajes en los JInternalFrame
public class JOptionMessage {

    public static final String Guardar = "El dato se ha Guardado Correctamente ";
    public static final String TituloGuardar = "Guardar ";
    public static final String ErrorGuardar = "El dato no se pudo Guardar : ";
    public static final String GuardarConfirm = "¿Desea Grabar el Registro?";
    
    public static final String Modificar = "El dato se ha Modificado Correctamente ";
    public static final String TituloModificar = "Modificar";
    public static final String ErrorModificar = "El dato no se pudo Modificar : ";
    public static final String ModificarConfirm = "¿Desea Modificar el Registro?";

    public static final String Eliminar = "El dato se Elimino Correctamente";
    public static final String TituloEliminar = "Eliminar";
    public static final String ErrorEliminar = "El dato no se pudo eliminar : ";
    public static final String EliminarConfirm = "¿Desea Eliminar el Registro?";

    public static final String ConfirmTitulo = "Pregunta";
    
    
    public static final String Datos = "No se encontraron datos para";
    public static final String TituloDatos = "Información";
    public static final String ErrorDatos = "Ocurrio un Error";
    public static final String TituloErrDatos = "Error";
    
    public static final String CamposObligatorios = "Los campos marcados son Obligatorios";
    public static final String TituloCamposObligatorios = "Llene los campos obligatorios";
    

    public void Mensage(String message, String titulo){
        JOptionPane.showMessageDialog(null, message, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public void Error(String message, String titulo) {
        JOptionPane.showMessageDialog(null, message, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public int Confirm(String message, String titulo) {
        int opcion = JOptionPane.showConfirmDialog(null, message, titulo, 0);
        return opcion;
    }
}
