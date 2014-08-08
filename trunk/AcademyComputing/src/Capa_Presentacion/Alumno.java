/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.https://www.youtube.com/watch?v=ICF-RldvSIo
 */
package Capa_Presentacion;

import Capa_Negocio.FiltroCampos;
import Capa_Negocio.FormatoDecimal;
import Capa_Negocio.FormatoFecha;
import Capa_Negocio.JOptionMessage;
import static Capa_Negocio.JOptionMessage.*;
import Capa_Negocio.Peticiones;
import Capa_Negocio.TipoFiltro;
import Capa_Negocio.Utilidades;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author GLARA
 */
public class Alumno extends javax.swing.JInternalFrame {

    /*El modelo se define en : Jtable-->propiedades-->model--> <User Code> */
    DefaultTableModel model;
    String[] titulos = {"Codigo", "Nombre Cliente", "Dirección", "Nit", "Estado"};//Titulos para Jtabla

    /*Se hace una instancia de la clase que recibira las peticiones de esta capa de aplicación*/
    Peticiones peticiones = new Peticiones();

    /*Se hace una instancia de la clase que recibira las peticiones de mensages de la capa de aplicación*/
    public static JOptionMessage msg = new JOptionMessage();

    /**
     * Creates new form Cliente
     */
    public Alumno() {
        initComponents();
        setFiltroTexto();
        addEscapeKey();
    }

    /*addEscapeKey agrega a este JInternalFrame un evento de cerrarVentana() al presionar la tecla "ESC" */
    private void addEscapeKey() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarVentana();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    /*Este metodo visualiza una mensage de cinfirmación al usuario antes de Cerrar la ventana,
     * si por eror se intento cerrar el formulario devera indicar que "NO" para no perder los datos
     * que no haya Guardado de lo contrario presiona "SI" y se cerrara la ventana sin Guardar ningun dato. */
    private void cerrarVentana() {
        int nu = JOptionPane.showInternalConfirmDialog(this, "Todos los datos que no se ha guardadox "
                + "se perderan.\n"
                + "¿Desea Cerrar esta ventana?", "Cerrar ventana", JOptionPane.YES_NO_OPTION);
        if (nu == JOptionPane.YES_OPTION || nu == 0) {
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            this.bntGuardar.setEnabled(false);
            this.bntModificar.setEnabled(false);
            this.bntEliminar.setEnabled(false);
            this.bntNuevo.setEnabled(true);
            removejtable();
            busqueda.setText("");
            busqueda.requestFocus();
            this.dispose();
        }
    }

    /* La funcion de este metodo es limpiar y desabilitar campos que se encuentren en un contenedor
     * ejem: los JTextFiel de un panel, se envian a la capa de negocio "Utilidades.setEditableTexto()" 
     * para que este los limpie,habilite o desabilite dichos componentes */
    public void limpiar() {
        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
    }

    /* Para no sobrecargar la memoria y hacer una instancia cada vez que actualizamos la JTable se hace una
     * sola instancia y lo unico que se hace antes de actualizar la JTable es limpiar el modelo y enviarle los
     * nuevos datos a mostrar en la JTable  */
    public void removejtable() {
        while (clientes.getRowCount() != 0) {
            model.removeRow(0);
        }
    }

    /* Este metodo se encarga de filtrar los datos que se deben ingresar en cada uno de los campos del formulario
     * podemos indicar que el usuario ingrese solo numeros , solo letras, numeros y letras, o cualquier caracter
     * tambien podemos validar si se aseptaran espacios en blanco en la cadena ingresada , para mas detalle visualizar
     * la clase TipoFiltro()  */
     private void setFiltroTexto() {

        TipoFiltro.setFiltraEntrada(nombre.getDocument(), FiltroCampos.NUM_LETRAS, 150, true);
        TipoFiltro.setFiltraEntrada(direccion.getDocument(), FiltroCampos.NUM_LETRAS, 200, true);
        TipoFiltro.setFiltraEntrada(nit.getDocument(), FiltroCampos.NUM_LETRAS, 16, true);
        TipoFiltro.setFiltraEntrada(telefono.getDocument(), FiltroCampos.SOLO_NUMEROS, 14, false);
        TipoFiltro.setFiltraEntrada(busqueda.getDocument(), FiltroCampos.NUM_LETRAS, 150, true);
    }
    
    
    /* Este metodo recibe de el campo busqueda un parametro que es el que servirá para realizar la cunsulta
     * de los datos, este envia a la capa de negocio "peticiones.getRegistroPorPks( el modelo de la JTable,
     * el nombre de la tabla, los campos de la tabla a consultar, los campos de condiciones, y el dato a comparar
     * en la(s) condicion(es) de la busqueda) .
     *   
     * Nota: si el campo busqueda no contiene ningun dato devolvera todos los datos de la tabla o un mensage
     * indicando que no hay datos para la busqueda  
     *
     * @param Dato , dato a buscar
     * @return 
     */ 
    private void MostrarDatos(String Dato) {
        //String sql = "";
        String[] campos = {"idclientes", "nombre", "direccion", "nit", "Estado"};
        //Para consulta simple con un where
        String[] condiciones = {"idclientes"};
        String[] Id = {Dato};

        //Para consulta compuesta para varios where
        String[] Id2 = {Dato, "T"};
        String[] condiciones2 = {"idclientes", "Estado"};

        if (this.rbNit.isSelected()) {
            if (!Dato.isEmpty()) {
                removejtable();
                Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                Utilidades.esObligatorio(this.JPanelCampos, false);
                model = peticiones.getRegistroPorPks(model, "clientes", campos, condiciones, Id);
                //model = cl.getRegistroPorPks(model, "clientes", campos, condiciones2, Id2);
            } else {

            }
        }
        if (this.rbNombre.isSelected()) {
            removejtable();
            Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
            Utilidades.esObligatorio(this.JPanelCampos, false);
            model = peticiones.getRegistroPorLike(model, "clientes", campos, "nombre", Dato);
        }
        Utilidades.ajustarAnchoColumnas(clientes);
    }

    //modificar solo pedir datos n oestablecer coneccion aca
    private void filaseleccionada() {
        int fila = clientes.getSelectedRow();

        String id = "" + clientes.getValueAt(fila, 0);
        if (clientes.getValueAt(fila, 0) != null) {

            String[] campos = {"idclientes", "nombre", "direccion", "correo", "nit", "telefono", "Estado", "fec_reg"};
            Component[] cmps = {codigo, nombre, direccion, correo, nit, telefono, rbEstado, dcFecha};
            Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
            peticiones.getRegistroSeleccionado(cmps, "clientes", campos, "idclientes", id);

            this.bntGuardar.setEnabled(false);
            this.bntModificar.setEnabled(true);
            this.bntEliminar.setEnabled(true);
            this.bntNuevo.setEnabled(false);
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
        java.awt.GridBagConstraints gridBagConstraints;

        panelImage = new elaprendiz.gui.panel.PanelImage();
        pnlActionButtons = new javax.swing.JPanel();
        bntNuevo = new elaprendiz.gui.button.ButtonRect();
        bntModificar = new elaprendiz.gui.button.ButtonRect();
        bntGuardar = new elaprendiz.gui.button.ButtonRect();
        bntEliminar = new elaprendiz.gui.button.ButtonRect();
        bntCancelar = new elaprendiz.gui.button.ButtonRect();
        bntSalir = new elaprendiz.gui.button.ButtonRect();
        JPanelCampos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        nombre = new elaprendiz.gui.textField.TextField();
        nit = new elaprendiz.gui.textField.TextField();
        correo = new elaprendiz.gui.textField.TextField();
        telefono = new elaprendiz.gui.textField.TextField();
        direccion = new elaprendiz.gui.textField.TextField();
        dcFecha = new com.toedter.calendar.JDateChooser();
        rbEstado = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        codigo = new elaprendiz.gui.textField.TextField();
        jTextField1 = new javax.swing.JTextField();
        precioC = new javax.swing.JFormattedTextField();
        categoria = new elaprendiz.gui.comboBox.ComboBoxRectIcon();
        jComboBox1 = new javax.swing.JComboBox();
        JPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        clientes = new javax.swing.JTable();
        JPanelBusqueda = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        busqueda = new elaprendiz.gui.textField.TextField();
        rbNit = new javax.swing.JRadioButton();
        rbNombre = new javax.swing.JRadioButton();
        pnlPaginador = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 0, 0));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setForeground(new java.awt.Color(0, 0, 0));
        setIconifiable(true);
        setTitle("Alumnos");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelImage.setLayout(null);

        pnlActionButtons.setBackground(java.awt.SystemColor.activeCaption);
        pnlActionButtons.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 153, 255), 1, true));
        pnlActionButtons.setForeground(new java.awt.Color(204, 204, 204));
        pnlActionButtons.setPreferredSize(new java.awt.Dimension(786, 52));
        pnlActionButtons.setLayout(new java.awt.GridBagLayout());

        bntNuevo.setBackground(new java.awt.Color(51, 153, 255));
        bntNuevo.setMnemonic(KeyEvent.VK_N);
        bntNuevo.setText("Nuevo");
        bntNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntNuevoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 84, 12, 0);
        pnlActionButtons.add(bntNuevo, gridBagConstraints);

        bntModificar.setBackground(new java.awt.Color(51, 153, 255));
        bntModificar.setMnemonic(KeyEvent.VK_M);
        bntModificar.setText("Modificar");
        bntModificar.setEnabled(false);
        bntModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntModificarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntModificar, gridBagConstraints);

        bntGuardar.setBackground(new java.awt.Color(51, 153, 255));
        bntGuardar.setMnemonic(KeyEvent.VK_G);
        bntGuardar.setText("Guardar");
        bntGuardar.setEnabled(false);
        bntGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntGuardarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntGuardar, gridBagConstraints);

        bntEliminar.setBackground(new java.awt.Color(51, 153, 255));
        bntEliminar.setMnemonic(KeyEvent.VK_E);
        bntEliminar.setText("Eliminar");
        bntEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntEliminarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntEliminar, gridBagConstraints);

        bntCancelar.setBackground(new java.awt.Color(51, 153, 255));
        bntCancelar.setMnemonic(KeyEvent.VK_X);
        bntCancelar.setText("Cancelar");
        bntCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntCancelarActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 0);
        pnlActionButtons.add(bntCancelar, gridBagConstraints);

        bntSalir.setBackground(new java.awt.Color(51, 153, 255));
        bntSalir.setText("Salir");
        bntSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntSalirActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 5, 12, 93);
        pnlActionButtons.add(bntSalir, gridBagConstraints);

        panelImage.add(pnlActionButtons);
        pnlActionButtons.setBounds(0, 430, 880, 50);

        JPanelCampos.setBackground(java.awt.SystemColor.activeCaption);
        JPanelCampos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        JPanelCampos.setForeground(new java.awt.Color(204, 204, 204));
        JPanelCampos.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("*Nombre:");
        JPanelCampos.add(jLabel1);
        jLabel1.setBounds(45, 40, 80, 20);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Correo:");
        JPanelCampos.add(jLabel2);
        jLabel2.setBounds(45, 65, 80, 20);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("*Dirección:");
        JPanelCampos.add(jLabel3);
        jLabel3.setBounds(45, 90, 80, 20);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("Telefono:");
        JPanelCampos.add(jLabel4);
        jLabel4.setBounds(400, 65, 80, 20);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Nit:");
        JPanelCampos.add(jLabel5);
        jLabel5.setBounds(400, 40, 80, 20);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("*Fecha de registro:");
        JPanelCampos.add(jLabel6);
        jLabel6.setBounds(680, 40, 140, 21);

        nombre.setEditable(false);
        nombre.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        nombre.setName("nombre"); // NOI18N
        nombre.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(nombre);
        nombre.setBounds(140, 40, 250, 21);

        nit.setEditable(false);
        nit.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        nit.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(nit);
        nit.setBounds(490, 40, 150, 21);

        correo.setEditable(false);
        correo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        correo.setInputVerifier(new Capa_Negocio.FormatoEmail(125,Capa_Negocio.FormatoEmail.EMAIL));
        correo.setName("correo"); // NOI18N
        correo.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(correo);
        correo.setBounds(140, 65, 250, 21);

        telefono.setEditable(false);
        telefono.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        telefono.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(telefono);
        telefono.setBounds(490, 65, 150, 21);

        direccion.setEditable(false);
        direccion.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        direccion.setName("dirección"); // NOI18N
        direccion.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(direccion);
        direccion.setBounds(140, 90, 500, 21);

        dcFecha.setDate(Calendar.getInstance().getTime());
        dcFecha.setDateFormatString("dd/MM/yyyy");
        dcFecha.setEnabled(false);
        dcFecha.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        dcFecha.setMaxSelectableDate(new java.util.Date(3093496470100000L));
        dcFecha.setMinSelectableDate(new java.util.Date(-62135744300000L));
        dcFecha.setPreferredSize(new java.awt.Dimension(120, 22));
        JPanelCampos.add(dcFecha);
        dcFecha.setBounds(680, 65, 140, 21);

        rbEstado.setBackground(new java.awt.Color(51, 153, 255));
        rbEstado.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        rbEstado.setForeground(new java.awt.Color(255, 255, 255));
        rbEstado.setText("Activo");
        rbEstado.setEnabled(false);
        rbEstado.setName("JRadioButton"); // NOI18N
        JPanelCampos.add(rbEstado);
        rbEstado.setBounds(680, 90, 140, 21);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Codigo:");
        JPanelCampos.add(jLabel10);
        jLabel10.setBounds(45, 15, 80, 17);

        codigo.setEditable(false);
        codigo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        codigo.setPreferredSize(new java.awt.Dimension(120, 21));
        JPanelCampos.add(codigo);
        codigo.setBounds(140, 15, 120, 21);

        jTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 204, 0)))); // NOI18N
        jTextField1.setCaretColor(new java.awt.Color(255, 204, 0));
        jTextField1.setName("textt"); // NOI18N
        JPanelCampos.add(jTextField1);
        jTextField1.setBounds(510, 10, 60, 20);

        precioC.setEditable(false);
        precioC.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new FormatoDecimal("#####0.00",true))));
        precioC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        precioC.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        precioC.setName("precioalmayor"); // NOI18N
        precioC.setPreferredSize(new java.awt.Dimension(80, 23));
        JPanelCampos.add(precioC);
        precioC.setBounds(750, 10, 80, 23);

        categoria.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "uno", "dos", "tres" }));
        categoria.setName("combo1"); // NOI18N
        categoria.setPreferredSize(new java.awt.Dimension(420, 22));
        JPanelCampos.add(categoria);
        categoria.setBounds(600, 10, 130, 20);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "Item 1", "Item 2", "Item 3", "Item 4", "" }));
        jComboBox1.setSelectedIndex(-1);
        jComboBox1.setName("combo2"); // NOI18N
        JPanelCampos.add(jComboBox1);
        jComboBox1.setBounds(410, 10, 80, 20);

        panelImage.add(JPanelCampos);
        JPanelCampos.setBounds(0, 40, 880, 140);

        JPanelTable.setOpaque(false);
        JPanelTable.setPreferredSize(new java.awt.Dimension(786, 402));
        JPanelTable.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        clientes.setForeground(new java.awt.Color(51, 51, 51));
        clientes.setModel(model = new DefaultTableModel(null, titulos)
            {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            });
            clientes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            clientes.setFocusCycleRoot(true);
            clientes.setGridColor(new java.awt.Color(51, 51, 255));
            clientes.setRowHeight(22);
            clientes.setSelectionBackground(java.awt.SystemColor.activeCaption);
            clientes.setSurrendersFocusOnKeystroke(true);
            clientes.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    clientesMouseClicked(evt);
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    clientesMouseClicked(evt);
                }
            });
            clientes.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    clientesKeyPressed(evt);
                }
            });
            jScrollPane1.setViewportView(clientes);
            clientes.getAccessibleContext().setAccessibleName("");

            JPanelTable.add(jScrollPane1, java.awt.BorderLayout.CENTER);

            panelImage.add(JPanelTable);
            JPanelTable.setBounds(0, 250, 880, 180);

            JPanelBusqueda.setBackground(java.awt.SystemColor.inactiveCaption);
            JPanelBusqueda.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            JPanelBusqueda.setLayout(null);

            jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            jLabel7.setText("Buscar Por:");
            JPanelBusqueda.add(jLabel7);
            jLabel7.setBounds(210, 10, 80, 17);

            busqueda.setPreferredSize(new java.awt.Dimension(250, 27));
            busqueda.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    busquedaActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(busqueda);
            busqueda.setBounds(300, 10, 250, 27);

            rbNit.setBackground(new java.awt.Color(51, 153, 255));
            rbNit.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            rbNit.setForeground(new java.awt.Color(255, 255, 255));
            rbNit.setText("Codigo");
            rbNit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rbNitActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(rbNit);
            rbNit.setBounds(330, 40, 80, 25);

            rbNombre.setBackground(new java.awt.Color(51, 153, 255));
            rbNombre.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
            rbNombre.setForeground(new java.awt.Color(255, 255, 255));
            rbNombre.setSelected(true);
            rbNombre.setText("Nombre");
            rbNombre.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rbNombreActionPerformed(evt);
                }
            });
            JPanelBusqueda.add(rbNombre);
            rbNombre.setBounds(450, 40, 81, 25);

            panelImage.add(JPanelBusqueda);
            JPanelBusqueda.setBounds(0, 179, 880, 70);

            pnlPaginador.setBackground(new java.awt.Color(57, 104, 163));
            pnlPaginador.setPreferredSize(new java.awt.Dimension(786, 40));
            pnlPaginador.setLayout(new java.awt.GridBagLayout());

            jLabel8.setFont(new java.awt.Font("Script MT Bold", 1, 32)); // NOI18N
            jLabel8.setForeground(new java.awt.Color(255, 255, 255));
            jLabel8.setText("<--Alumnos-->");
            pnlPaginador.add(jLabel8, new java.awt.GridBagConstraints());

            panelImage.add(pnlPaginador);
            pnlPaginador.setBounds(0, 0, 880, 40);

            getContentPane().add(panelImage, java.awt.BorderLayout.CENTER);

            setBounds(0, 0, 893, 512);
        }// </editor-fold>//GEN-END:initComponents

    private void bntNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntNuevoActionPerformed
        // TODO add your handling code here:
        Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
        this.bntGuardar.setEnabled(true);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(false);
        nombre.requestFocus();

    }//GEN-LAST:event_bntNuevoActionPerformed

    private void bntGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntGuardarActionPerformed
        // TODO add your handling code here:
        if (Utilidades.esObligatorio(this.JPanelCampos, true)) {
            msg.Error(CamposObligatorios, TituloCamposObligatorios);
            return;
        }

        int resp = msg.Confirm(GuardarConfirm, ConfirmTitulo);
        if (resp == 0) {
            String ps = "ps_cliente";
            boolean seguardo = false;

            Object[] valores = {nombre.getText(), direccion.getText(),
                correo.getText(), nit.getText(),
                telefono.getText(), FormatoFecha.getFormato(dcFecha.getCalendar().getTime(), FormatoFecha.A_M_D)
            };
            try {
                seguardo = peticiones.guardarRegistro(valores, ps);

                if (seguardo) {
                    Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                    MostrarDatos(busqueda.getText());
                    busqueda.requestFocus();
                    msg.Mensage(Guardar, TituloGuardar);
                }
            } catch (Exception e) {
                msg.Error(ErrorGuardar + ": " + e, TituloGuardar);
            }
        }
        //}
    }//GEN-LAST:event_bntGuardarActionPerformed

    private void bntSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntSalirActionPerformed
        cerrarVentana();
    }//GEN-LAST:event_bntSalirActionPerformed

    private void clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientesMouseClicked
        // TODO add your handling code here:
        filaseleccionada();

    }//GEN-LAST:event_clientesMouseClicked

    private void bntEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntEliminarActionPerformed
        // TODO add your handling code here:
        int resp = msg.Confirm(EliminarConfirm, ConfirmTitulo);
        if (resp == 0) {

            int fila = clientes.getSelectedRow();
            String idc = (String) "" + clientes.getValueAt(fila, 0);
            char est = 'F';
            String ps = "sp_modificaCliente", ps2 = "sp_eliminaCliente";
            boolean seguardo = false;

            Object[] valores2 = {idc};
            Object[] valores = {idc, est};

            try {
                seguardo = peticiones.guardarRegistro(valores2, ps2);
            } catch (Exception e) {
                msg.Error(ErrorEliminar + ": " + e, TituloEliminar);
            }

            if (seguardo) {
                Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
                MostrarDatos(busqueda.getText());
                busqueda.requestFocus();
                msg.Mensage(Eliminar, TituloEliminar);
            } else {
                seguardo = peticiones.guardarRegistro(valores, ps);
                if (seguardo) {
                    Utilidades.setEditableTexto(this.JPanelCampos, true, null, true, "");
                    MostrarDatos(busqueda.getText());
                    busqueda.requestFocus();
                    msg.Mensage(Eliminar, TituloEliminar);
                }
            }
        }
    }//GEN-LAST:event_bntEliminarActionPerformed

    private void bntModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntModificarActionPerformed
        // TODO add your handling code here:

        if (Utilidades.esObligatorio(this.JPanelCampos, true)) {
            msg.Error(CamposObligatorios, TituloCamposObligatorios);
            return;
        }

        int resp = msg.Confirm(ModificarConfirm, ConfirmTitulo);
        if (resp == 0) {

            String ps = "sp_actualizaCliente", estado;
            boolean seguardo = false;
            int fila = clientes.getSelectedRow();
            String id = (String) "" + clientes.getValueAt(fila, 0);

            if (this.rbEstado.isSelected()) {
                estado = "T";
            } else {
                estado = "F";
            }

            Object[] valores = {nombre.getText(), direccion.getText(),
                correo.getText(), nit.getText(),
                telefono.getText(), FormatoFecha.getFormato(dcFecha.getCalendar().getTime(), FormatoFecha.A_M_D),
                estado, id
            };
            try {
                seguardo = peticiones.guardarRegistro(valores, ps);

                if (seguardo) {
                    Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
                    MostrarDatos(busqueda.getText());
                    busqueda.requestFocus();
                    msg.Mensage(Modificar, TituloModificar);
                }
            } catch (Exception e) {
                msg.Error(ErrorModificar + ": " + e, TituloModificar);
            }

        }
    }//GEN-LAST:event_bntModificarActionPerformed

    private void bntCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntCancelarActionPerformed
        // TODO add your handling code here:
        Utilidades.setEditableTexto(this.JPanelCampos, false, null, true, "");
        Utilidades.esObligatorio(this.JPanelCampos, false);
        removejtable();
        this.bntGuardar.setEnabled(false);
        this.bntModificar.setEnabled(false);
        this.bntEliminar.setEnabled(false);
        this.bntNuevo.setEnabled(true);
        removejtable();
        busqueda.requestFocus();

    }//GEN-LAST:event_bntCancelarActionPerformed

    private void rbNitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNitActionPerformed
        // TODO add your handling code here:
        rbNombre.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbNitActionPerformed

    private void rbNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNombreActionPerformed
        // TODO add your handling code here:
        rbNit.setSelected(false);
        busqueda.requestFocus();
    }//GEN-LAST:event_rbNombreActionPerformed

    private void busquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busquedaActionPerformed
        // TODO add your handling code here:
        MostrarDatos(busqueda.getText());
    }//GEN-LAST:event_busquedaActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        // TODO add your handling code here:
        cerrarVentana();
    }//GEN-LAST:event_formInternalFrameClosing

    private void clientesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_clientesKeyPressed
        // TODO add your handling code here:
        int key = evt.getKeyCode();
        if (key == java.awt.event.KeyEvent.VK_SPACE) {
            filaseleccionada();
        }
        if (key == java.awt.event.KeyEvent.VK_DOWN || key == java.awt.event.KeyEvent.VK_UP) {
            limpiar();
        }
    }//GEN-LAST:event_clientesKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanelBusqueda;
    private javax.swing.JPanel JPanelCampos;
    private javax.swing.JPanel JPanelTable;
    private elaprendiz.gui.button.ButtonRect bntCancelar;
    private elaprendiz.gui.button.ButtonRect bntEliminar;
    private elaprendiz.gui.button.ButtonRect bntGuardar;
    private elaprendiz.gui.button.ButtonRect bntModificar;
    private elaprendiz.gui.button.ButtonRect bntNuevo;
    private elaprendiz.gui.button.ButtonRect bntSalir;
    private elaprendiz.gui.textField.TextField busqueda;
    private elaprendiz.gui.comboBox.ComboBoxRectIcon categoria;
    private javax.swing.JTable clientes;
    private elaprendiz.gui.textField.TextField codigo;
    private elaprendiz.gui.textField.TextField correo;
    private com.toedter.calendar.JDateChooser dcFecha;
    private elaprendiz.gui.textField.TextField direccion;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private elaprendiz.gui.textField.TextField nit;
    private elaprendiz.gui.textField.TextField nombre;
    private elaprendiz.gui.panel.PanelImage panelImage;
    private javax.swing.JPanel pnlActionButtons;
    private javax.swing.JPanel pnlPaginador;
    private javax.swing.JFormattedTextField precioC;
    private javax.swing.JRadioButton rbEstado;
    private javax.swing.JRadioButton rbNit;
    private javax.swing.JRadioButton rbNombre;
    private elaprendiz.gui.textField.TextField telefono;
    // End of variables declaration//GEN-END:variables
}
