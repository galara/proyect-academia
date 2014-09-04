package Capa_Negocio;

import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;

/**
 * importare
 *
 * @author Glara
 */
public class AddForms {

    /*  Este metodo administra agrega JInternalFrame al JDesktopPane del form principal (MDI)
     * @param dp , JDesktopPane del form principal 
     * qparam vnt , nombre del componente JInternalFrame a agregar al form principal
     */
    public static void adminInternalFrame(JDesktopPane dp, JInternalFrame vnt) {

        if (vnt != null && !vnt.isShowing()) {

            if (vnt.isIcon() == true) {
                try {
                    vnt.setIcon(false);
                    vnt.toFront();
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(AddForms.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else {
                vnt.show();
                dp.remove(vnt);
                try {
                    dp.add(vnt, JLayeredPane.DEFAULT_LAYER);
                    vnt.toFront();
                } catch (IllegalArgumentException ex) {
                    dp.add(vnt, JLayeredPane.DEFAULT_LAYER);
                }
            }
        }
    }
}
