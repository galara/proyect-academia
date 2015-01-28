package Capa_Presentacion;

import Capa_Negocio.AddForms;
import Capa_Negocio.CalcularMoras;
import inicio.start;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Glara
 */
public class Principal extends javax.swing.JFrame {

    public Principal() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(getClass().getResource("/Recursos/milogo.png")).getImage());
    }

    private void cerrarVentana() {
        int count = dp.getComponentCount();
        JInternalFrame[] cm = dp.getAllFrames();

        for (int i = 0; i < cm.length; i++) {
            System.out.print(cm[i].getName() + "\n");
        }

        if (count == 0) {
            int nu = JOptionPane.showConfirmDialog(this, "¿Desea Cerrar esta ventana?", "Cerrar Sistema", JOptionPane.YES_NO_OPTION);

            if (nu == JOptionPane.YES_OPTION || nu == 0) {
                System.exit(0);
            } else {

            }

        } else if (count > 0) {
            JOptionPane.showMessageDialog(null, "Para cerrar el Systema primero debe cerrar los formularios abiertos " + "( " + (count) + " )");
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

        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        dp = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        sistema = new javax.swing.JLabel();
        usuario = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        msalir = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mprofesor = new javax.swing.JMenuItem();
        mcarrera = new javax.swing.JMenuItem();
        mhorario = new javax.swing.JMenuItem();
        mcurso = new javax.swing.JMenuItem();
        motrospagos = new javax.swing.JMenuItem();
        mgrupo = new javax.swing.JMenuItem();
        malumno = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        musuario = new javax.swing.JMenuItem();
        mciclo = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        jMenuItem2.setText("jMenuItem2");

        jMenuItem4.setText("jMenuItem4");

        jMenuItem5.setText("jMenuItem5");

        jMenuItem6.setText("jMenuItem6");

        jMenuItem7.setText("jMenuItem7");

        jMenuItem15.setText("jMenuItem15");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SYSTEMA DE GESTIÓN COMPUVISIÓN");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        dp.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout dpLayout = new javax.swing.GroupLayout(dp);
        dp.setLayout(dpLayout);
        dpLayout.setHorizontalGroup(
            dpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 950, Short.MAX_VALUE)
        );
        dpLayout.setVerticalGroup(
            dpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 545, Short.MAX_VALUE)
        );

        getContentPane().add(dp);

        jPanel1.setPreferredSize(new java.awt.Dimension(549, 30));

        sistema.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sistema.setForeground(new java.awt.Color(51, 51, 255));
        sistema.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sistema.setText("\"BIENVENIDOS AL SISTEMA\" Usuario :");

        usuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        usuario.setForeground(new java.awt.Color(51, 51, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sistema, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(602, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sistema, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 5, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1);

        jMenuBar1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        jMenu1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu1.setText("Archivo");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        msalir.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        msalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/salir.png"))); // NOI18N
        msalir.setText("Salir");
        msalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msalirActionPerformed(evt);
            }
        });
        jMenu1.add(msalir);

        jMenuBar1.add(jMenu1);

        jMenu3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu3.setText("Mantenimiento");
        jMenu3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        mprofesor.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        mprofesor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/profesor.png"))); // NOI18N
        mprofesor.setText("Profesor");
        mprofesor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mprofesorActionPerformed(evt);
            }
        });
        jMenu3.add(mprofesor);

        mcarrera.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        mcarrera.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/carrera.png"))); // NOI18N
        mcarrera.setText("Carrera");
        mcarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mcarreraActionPerformed(evt);
            }
        });
        jMenu3.add(mcarrera);

        mhorario.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        mhorario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/grupos.png"))); // NOI18N
        mhorario.setText("Grupo/Horario");
        mhorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhorarioActionPerformed(evt);
            }
        });
        jMenu3.add(mhorario);

        mcurso.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        mcurso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/ciclo.png"))); // NOI18N
        mcurso.setText("Curso");
        mcurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mcursoActionPerformed(evt);
            }
        });
        jMenu3.add(mcurso);

        motrospagos.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        motrospagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/productos.png"))); // NOI18N
        motrospagos.setText("Otros Pagos");
        motrospagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                motrospagosActionPerformed(evt);
            }
        });
        jMenu3.add(motrospagos);

        mgrupo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        mgrupo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/tipopago.png"))); // NOI18N
        mgrupo.setText("Tipo de Pago");
        mgrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mgrupoActionPerformed(evt);
            }
        });
        jMenu3.add(mgrupo);

        malumno.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        malumno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/alumno.png"))); // NOI18N
        malumno.setText("Alumno");
        malumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                malumnoActionPerformed(evt);
            }
        });
        jMenu3.add(malumno);

        jMenuItem3.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/horario3.png"))); // NOI18N
        jMenuItem3.setText("Pensum");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        jMenu8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu8.setText("Pagos");
        jMenu8.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jMenuItem10.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Pago_UR.png"))); // NOI18N
        jMenuItem10.setText("Registro de Pagos");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem10);

        jMenuBar1.add(jMenu8);

        jMenu2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu2.setText("Sistema");
        jMenu2.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        musuario.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        musuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/login.png"))); // NOI18N
        musuario.setText("Usuario");
        musuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                musuarioActionPerformed(evt);
            }
        });
        jMenu2.add(musuario);

        mciclo.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        mciclo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/horario.png"))); // NOI18N
        mciclo.setText("Ciclo Escolar");
        mciclo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mcicloActionPerformed(evt);
            }
        });
        jMenu2.add(mciclo);

        jMenu7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/iconobackup.jpg"))); // NOI18N
        jMenu7.setText("Gestionar BD");
        jMenu7.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jMenuItem14.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jMenuItem14.setText("Backup");
        jMenu7.add(jMenuItem14);

        jMenuItem16.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jMenuItem16.setText("Restaurar");
        jMenu7.add(jMenuItem16);

        jMenuItem13.setText("Calcular Mora");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem13);

        jMenu2.add(jMenu7);

        jMenuBar1.add(jMenu2);

        jMenu5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu5.setText("Informes");
        jMenu5.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/checklist-icon-2.jpg"))); // NOI18N
        jMenu6.setText("Reporte de Alumnos");
        jMenu6.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jMenuItem11.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jMenuItem11.setText("Listado de Alumnos Grupo");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem11);

        jMenuItem12.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jMenuItem12.setText("Pagos Diarios");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem12);

        jMenu5.add(jMenu6);

        jMenuBar1.add(jMenu5);

        jMenu4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenu4.setText("Ayuda");
        jMenu4.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        jMenuItem1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/Help.png"))); // NOI18N
        jMenuItem1.setText("Acerca de");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem1);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void msalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msalirActionPerformed
        // TODO add your handling code here:
        cerrarVentana();

    }//GEN-LAST:event_msalirActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        cerrarVentana();
    }//GEN-LAST:event_formWindowClosing

    private void mprofesorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mprofesorActionPerformed
        // TODO add your handling code here:
        Profesor frmProfesor = new Profesor();
        if (frmProfesor == null) {
            frmProfesor = new Profesor();
        }
        AddForms.adminInternalFrame(dp, frmProfesor);
    }//GEN-LAST:event_mprofesorActionPerformed

    private void mhorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhorarioActionPerformed
        // TODO add your handling code here:
        Horario frmHorario = new Horario();
        if (frmHorario == null) {
            frmHorario = new Horario();
        }
        AddForms.adminInternalFrame(dp, frmHorario);
    }//GEN-LAST:event_mhorarioActionPerformed

    private void mcursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcursoActionPerformed
        // TODO add your handling code here:
        Curso frmCurso = new Curso();
        if (frmCurso == null) {
            frmCurso = new Curso();
        }
        AddForms.adminInternalFrame(dp, frmCurso);
    }//GEN-LAST:event_mcursoActionPerformed

    private void musuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_musuarioActionPerformed
        // TODO add your handling code here:
        Usuario frmUsuario = new Usuario();
        if (frmUsuario == null) {
            frmUsuario = new Usuario();
        }
        AddForms.adminInternalFrame(dp, frmUsuario);
    }//GEN-LAST:event_musuarioActionPerformed

    private void mgrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mgrupoActionPerformed
        // TODO add your handling code here:
        TipoPago frmTipoPago = new TipoPago();
        if (frmTipoPago == null) {
            frmTipoPago = new TipoPago();
        }
        AddForms.adminInternalFrame(dp, frmTipoPago);
    }//GEN-LAST:event_mgrupoActionPerformed

    private void malumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_malumnoActionPerformed
        // TODO add your handling code here:
        Alumno frmAlumno = new Alumno();
        if (frmAlumno == null) {
            frmAlumno = new Alumno();
        }
        AddForms.adminInternalFrame(dp, frmAlumno);
    }//GEN-LAST:event_malumnoActionPerformed

    private void mcicloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcicloActionPerformed
        // TODO add your handling code here:
        Ciclo frmCiclo = new Ciclo();
        if (frmCiclo == null) {
            frmCiclo = new Ciclo();
        }
        AddForms.adminInternalFrame(dp, frmCiclo);
    }//GEN-LAST:event_mcicloActionPerformed

    private void motrospagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_motrospagosActionPerformed
        // TODO add your handling code here:
        OtrosPagos frmOtrosPagos = new OtrosPagos();
        if (frmOtrosPagos == null) {
            frmOtrosPagos = new OtrosPagos();
        }
        AddForms.adminInternalFrame(dp, frmOtrosPagos);
    }//GEN-LAST:event_motrospagosActionPerformed

    private void mcarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcarreraActionPerformed
        // TODO add your handling code here:
        Carrera frmCarrera = new Carrera();
        if (frmCarrera == null) {
            frmCarrera = new Carrera();
        }
        AddForms.adminInternalFrame(dp, frmCarrera);
    }//GEN-LAST:event_mcarreraActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        Ayuda frmAyuda = new Ayuda();
        if (frmAyuda == null) {
            frmAyuda = new Ayuda();
        }
        AddForms.adminInternalFrame(dp, frmAyuda);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        PensumDetalle frmPensum = new PensumDetalle();
        if (frmPensum == null) {
            frmPensum = new PensumDetalle();
        }
        AddForms.adminInternalFrame(dp, frmPensum);
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        Pagos frmPagos = new Pagos();
        if (frmPagos == null) {
            frmPagos = new Pagos();
        }
        AddForms.adminInternalFrame(dp, frmPagos);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
        CalcularMoras.moras();
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        ListadoAlumnos frmListadoAlumnos = new ListadoAlumnos();
        if (frmListadoAlumnos == null) {
            frmListadoAlumnos = new ListadoAlumnos();
        }
        AddForms.adminInternalFrame(dp, frmListadoAlumnos);        
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        PagosDiarios frmPagosDiarios = new PagosDiarios();
        if (frmPagosDiarios == null) {
            frmPagosDiarios = new PagosDiarios();
        }
        AddForms.adminInternalFrame(dp, frmPagosDiarios); 
        
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */

        /*
         * Create and display the form
         */
        //java.awt.EventQueue.invokeLater(() -> {
        new Principal().setVisible(true);
        
        //});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JDesktopPane dp;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuItem malumno;
    private javax.swing.JMenuItem mcarrera;
    private javax.swing.JMenuItem mciclo;
    private javax.swing.JMenuItem mcurso;
    private javax.swing.JMenuItem mgrupo;
    private javax.swing.JMenuItem mhorario;
    private javax.swing.JMenuItem motrospagos;
    private javax.swing.JMenuItem mprofesor;
    private javax.swing.JMenuItem msalir;
    private javax.swing.JMenuItem musuario;
    private javax.swing.JLabel sistema;
    private javax.swing.JLabel usuario;
    // End of variables declaration//GEN-END:variables
}
