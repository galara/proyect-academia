package Capa_Negocio;

import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

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

        JInternalFrame[] cm = dp.getAllFrames();
        boolean estado = false;
        
        //Verifica si ya esta abierto o minimizado  estado=true
        for (JInternalFrame cm1 : cm) {
            
            if (cm1.getName().equals(vnt.getName())) {
                estado = true;
                JOptionPane.showMessageDialog(null, "EL FORMULARIO YA ESTA ABIERTO \n");
                cm1.toFront();

                if (cm1 != null && !cm1.isShowing()) {
                    if (cm1.isIcon() == true) {
                        try {
                            cm1.setIcon(false);
                            cm1.toFront();
                        } catch (PropertyVetoException ex) {
                            Logger.getLogger(AddForms.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }

            }
        }
        //Verifica Si no esta abiero o minimizado estado=false
        if (estado != true) {
            if (vnt != null && !vnt.isShowing()) {
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
