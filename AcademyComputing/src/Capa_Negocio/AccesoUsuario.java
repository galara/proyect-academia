package Capa_Negocio;

import Capa_Datos.BdConexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author GLARA
 */
public class AccesoUsuario  {

    private static String usuario = null;
    PreparedStatement ps = null;
    static java.sql.Connection conn;//getConnection intentara establecer una conexión.
    static boolean activo = false;

    public static enum Estado {
        ERROR, NO_EXISTE, ERROR_CLAVE, ACCESO_OK, USR_INACTICVO
    };

//    public AccesoUsuario() //throws ParserConfigurationException
//    {
////        DocumentBuilderFactory dbc = DocumentBuilderFactory.newInstance();
////        DocumentBuilder db = dbc.newDocumentBuilder();
////        Document doc = db.newDocument();
//
//    }
    public static Estado configUsuario(String login, String clave) {

        usuario = null;
        if (usuario == null) {
            return initUsuario(login, clave);
        } else {
            return Estado.ERROR;
        }
    }

    public static String getUsuario() {
        return usuario;
    }

    private static Estado initUsuario(String login, String clave) {

        conn = BdConexion.getConexion();
        ResultSet rs;
        String sql = "SELECT usuario,password,estado FROM usuario where usuario='" + login + "'";
        String password = null;
        rs = BdConexion.getResultSet(sql);

        //Busca el usuario y llena las variables usuario y password
        if (rs != null) {
            try {
                if (rs.next()) {//verifica si esta vacio, pero desplaza el puntero al siguiente elemento
                    rs.beforeFirst();//regresa el puntero al primer registro
                    while (rs.next()) {//mientras tenga registros que haga lo siguiente
                        usuario = (rs.getString(1));
                        password = (rs.getString(2));
                        activo = rs.getBoolean(3);
                    }
                } else {
                    usuario = null;
                    password = null;
                    activo = false;
                }
                rs.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        //*********************************************************************

        if (usuario == null) {
            return Estado.NO_EXISTE;
        } else {
            if (activo == true) {
                if (login.equals(usuario) && clave.equals(password)) {
                    return Estado.ACCESO_OK;
                } else {
                    return Estado.ERROR_CLAVE;
                }
            } else {
                return Estado.USR_INACTICVO;
            }
        }
    }

}
