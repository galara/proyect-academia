/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reportes;

import Capa_Datos.BdConexion;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.*;

/**
 *
 * @author GLARA
 */
public class Pagos_diarios {

    static Connection conn;

    public static void Pagos_diario(String parameter,String parameter2) {
        try {
            String theReport = "ReporteDiario.jasper";
            if (theReport == null) {
                System.exit(2);
            }

            JasperReport masterReport = null;
            try {
                masterReport = (JasperReport) JRLoader.loadObject(theReport);
            } catch (JRException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                System.exit(3);
            }

            conn = BdConexion.getConexion();
            Map parametro = new HashMap();
            String idrecibo = (parameter);
            String idrecibo2 = (parameter2);
            
            parametro.put("desde", idrecibo);
            parametro.put("hasta", idrecibo2);

            JasperPrint impresor = JasperFillManager.fillReport(masterReport, parametro, conn);
            //JasperPrintManager.printReport(impresor, false);
            JasperViewer jviewer = new JasperViewer(impresor, false);
            jviewer.setExtendedState(JasperViewer.MAXIMIZED_BOTH);
            jviewer.setTitle("Comprobante de Pago");
            jviewer.setVisible(true);

        } catch (JRException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

}
