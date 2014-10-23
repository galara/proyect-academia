package Capa_Presentacion;

import Capa_Negocio.AddForms;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * @author Glara
 */
public class Principal extends javax.swing.JFrame {

    private static Profesor frmProfesor = new Profesor();
    private static Horario frmHorario = new Horario();
    private static Curso frmCurso = new Curso();
    private static Usuario frmUsuario = new Usuario();
    private static Grupo frmGrupo = new Grupo();
    private static Alumno frmAlumno = new Alumno();
    private static Ciclo frmCiclo = new Ciclo();
    private static OtrosPagos frmOtrosPagos = new OtrosPagos();
    private static Carrera frmCarrera = new Carrera();
    
    public Principal() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(getClass().getResource("/Recursos/milogo.png")).getImage());

    }

    private void cerrarVentana() {
        
//        int count = dp.getComponentCount();
//        if (count == 0) {
//            int nu = JOptionPane.showConfirmDialog(this, "¿Desea Cerrar esta ventana?", "Cerrar ventana", JOptionPane.YES_NO_OPTION);
//
//            if (nu == JOptionPane.YES_OPTION || nu == 0) {
//                System.exit(0);
//            }else{
//                
//            }
//
//        }
//        else if (count > 0) {
//            JOptionPane.showMessageDialog(null, "Para cerrar el Systema primero debe cerrar los formularios abiertos "+"( "+count+" )");
//        }
        
        int nu = JOptionPane.showConfirmDialog(this, "Todos los datos que no se ha guardadooo "
                + "se perderan.\n"
                + "¿Desea Cerrar esta ventana?", "Cerrar ventana", JOptionPane.YES_NO_OPTION);

        if (nu == JOptionPane.YES_OPTION || nu == 0) {
            System.exit(0);
        } else {
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

        dp = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        sistema = new javax.swing.JLabel();
        usuario = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        msalir = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mprofesor = new javax.swing.JMenuItem();
        mhorario = new javax.swing.JMenuItem();
        mcurso = new javax.swing.JMenuItem();
        mgrupo = new javax.swing.JMenuItem();
        malumno = new javax.swing.JMenuItem();
        motrospagos = new javax.swing.JMenuItem();
        mcarrera = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        musuario = new javax.swing.JMenuItem();
        mciclo = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        dp.setBackground(new java.awt.Color(153, 153, 153));
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
                .addContainerGap(210, Short.MAX_VALUE))
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

        jMenuBar1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenu1.setText("File");

        msalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/salir.png"))); // NOI18N
        msalir.setText("Salir");
        msalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msalirActionPerformed(evt);
            }
        });
        jMenu1.add(msalir);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Mantenimiento");

        mprofesor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/profesor.png"))); // NOI18N
        mprofesor.setText("Profesor");
        mprofesor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mprofesorActionPerformed(evt);
            }
        });
        jMenu3.add(mprofesor);

        mhorario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/horario.png"))); // NOI18N
        mhorario.setText("Grupo/Horario");
        mhorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mhorarioActionPerformed(evt);
            }
        });
        jMenu3.add(mhorario);

        mcurso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/carrera.png"))); // NOI18N
        mcurso.setText("Curso");
        mcurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mcursoActionPerformed(evt);
            }
        });
        jMenu3.add(mcurso);

        mgrupo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/grupos.png"))); // NOI18N
        mgrupo.setText("??????");
        mgrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mgrupoActionPerformed(evt);
            }
        });
        jMenu3.add(mgrupo);

        malumno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/alumno.png"))); // NOI18N
        malumno.setText("Alumno");
        malumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                malumnoActionPerformed(evt);
            }
        });
        jMenu3.add(malumno);

        motrospagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/productos.png"))); // NOI18N
        motrospagos.setText("Otros Pagos");
        motrospagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                motrospagosActionPerformed(evt);
            }
        });
        jMenu3.add(motrospagos);

        mcarrera.setText("Carrera");
        mcarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mcarreraActionPerformed(evt);
            }
        });
        jMenu3.add(mcarrera);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Sistema");

        musuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/login.png"))); // NOI18N
        musuario.setText("Usuario");
        musuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                musuarioActionPerformed(evt);
            }
        });
        jMenu2.add(musuario);

        mciclo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Recursos/ciclo.png"))); // NOI18N
        mciclo.setText("Ciclo Escolar");
        mciclo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mcicloActionPerformed(evt);
            }
        });
        jMenu2.add(mciclo);

        jMenuBar1.add(jMenu2);

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
        if (frmProfesor == null) {
            frmProfesor = new Profesor();
        }
        AddForms.adminInternalFrame(dp, frmProfesor);
    }//GEN-LAST:event_mprofesorActionPerformed

    private void mhorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mhorarioActionPerformed
        // TODO add your handling code here:
        if (frmHorario == null) {
            frmHorario = new Horario();
        }
        AddForms.adminInternalFrame(dp, frmHorario);
    }//GEN-LAST:event_mhorarioActionPerformed

    private void mcursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcursoActionPerformed
        // TODO add your handling code here:
        if (frmCurso == null) {
            frmCurso = new Curso();
        }
        AddForms.adminInternalFrame(dp, frmCurso);
    }//GEN-LAST:event_mcursoActionPerformed

    private void musuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_musuarioActionPerformed
        // TODO add your handling code here:
        if (frmUsuario == null) {
            frmUsuario = new Usuario();
        }
        AddForms.adminInternalFrame(dp, frmUsuario);
    }//GEN-LAST:event_musuarioActionPerformed

    private void mgrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mgrupoActionPerformed
        // TODO add your handling code here:
//        if (frmGrupo == null) {
//            frmGrupo = new Grupo();
//        }
//        AddForms.adminInternalFrame(dp, frmGrupo);
    }//GEN-LAST:event_mgrupoActionPerformed

    private void malumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_malumnoActionPerformed
        // TODO add your handling code here:
        if (frmAlumno == null) {
            frmAlumno = new Alumno();
        }
        AddForms.adminInternalFrame(dp, frmAlumno);
    }//GEN-LAST:event_malumnoActionPerformed

    private void mcicloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcicloActionPerformed
        // TODO add your handling code here:
        if (frmCiclo == null) {
            frmCiclo = new Ciclo();
        }
        AddForms.adminInternalFrame(dp, frmCiclo);
    }//GEN-LAST:event_mcicloActionPerformed

    private void motrospagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_motrospagosActionPerformed
        // TODO add your handling code here:
        if (frmOtrosPagos == null) {
            frmOtrosPagos = new OtrosPagos();
        }
        AddForms.adminInternalFrame(dp, frmOtrosPagos);
    }//GEN-LAST:event_motrospagosActionPerformed

    private void mcarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mcarreraActionPerformed
        // TODO add your handling code here:
        if (frmCarrera == null) {
            frmCarrera = new Carrera();
        }
        AddForms.adminInternalFrame(dp, frmCarrera);
    }//GEN-LAST:event_mcarreraActionPerformed

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
    private javax.swing.JMenuBar jMenuBar1;
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
