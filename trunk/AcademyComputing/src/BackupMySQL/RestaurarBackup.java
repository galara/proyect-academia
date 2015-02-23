/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * principal.java
 *
 * Created on 20-02-2015
 */
package BackupMySQL;

import Capa_Datos.BdConexion;
import Capa_Negocio.AccesoUsuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author GLARA
 */
public class RestaurarBackup extends javax.swing.JInternalFrame {

    JFileChooser RestaurarBackupMySQL = new JFileChooser();

    /**
     * Creates new form principal
     */
    public RestaurarBackup() {
        initComponents();
    }

    void GenerarBackupMySQL() {
        int resp;
        Runtime runtime = Runtime.getRuntime();
        RestaurarBackupMySQL.setFileSelectionMode(JFileChooser.FILES_ONLY);

        resp = RestaurarBackupMySQL.showOpenDialog(this);//JFileChooser de nombre RealizarBackupMySQL

        if (resp == JFileChooser.APPROVE_OPTION) {//Si el usuario presiona aceptar; se genera el Backup
            try {

                File backupFile = new File(String.valueOf(RestaurarBackupMySQL.getSelectedFile().toString()));
                String ubicacion = backupFile.getAbsolutePath();
                Process child = runtime.exec("C:\\Archivos de programa\\MySQL\\MySQL Server 5.6\\bin\\mysql -u "+BdConexion.user+" -p"+BdConexion.pass+" --default-character_set=utf8 "+BdConexion.dataBase);
                OutputStream os = child.getOutputStream();

                FileInputStream fis = new FileInputStream(ubicacion);
                byte[] buffer = new byte[1000];

                int leido = fis.read(buffer);
                while (leido > 0) {
                    os.write(buffer, 0, leido);
                    leido = fis.read(buffer);
                }
                os.flush();
                os.close();
                fis.close();
            } catch (IOException e) {
                JOptionPane.showInternalMessageDialog(this, "Error no se Restauro el Backup por el siguiente motivo:" + e.getMessage(), "Verificar", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showInternalMessageDialog(this, "El Backup se ha Restaurado Correctamente", "Mensage", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } else if (resp == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showInternalMessageDialog(this, "Ha sido cancelada la Restauración del Backup");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonAction1 = new elaprendiz.gui.button.ButtonAction();

        setClosable(true);
        setMinimumSize(new java.awt.Dimension(299, 93));
        setName("Restaurar Backup"); // NOI18N
        setPreferredSize(new java.awt.Dimension(299, 93));
        setRequestFocusEnabled(false);
        setVisible(true);

        buttonAction1.setText("Restaurar Backup BD");
        buttonAction1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        buttonAction1.setName("Restaurar Backup Backup"); // NOI18N
        buttonAction1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAction1ActionPerformed(evt);
            }
        });
        getContentPane().add(buttonAction1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAction1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAction1ActionPerformed
        // TODO add your handling code here:
        if (AccesoUsuario.AccesosUsuario(buttonAction1.getName()) == true) {
            GenerarBackupMySQL();
        } else {
            JOptionPane.showInternalMessageDialog(this, "No tiene Acceso para realizar esta operación ");

        }

    }//GEN-LAST:event_buttonAction1ActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private elaprendiz.gui.button.ButtonAction buttonAction1;
    // End of variables declaration//GEN-END:variables
}
