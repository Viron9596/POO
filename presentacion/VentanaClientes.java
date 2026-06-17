package presentacion;

import dominio.Cliente;
import dominio.PersonaNatural;
import dominio.ClienteHabitual;
import dominio.ClienteEsporadico;
import dominio.Hotel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaClientes extends JDialog {
    private Hotel hotel;
    private JTextField txtId, txtNombre, txtCorreo, txtTelefono;
    private JTextArea txtAreaOutput;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;

    // Fidelidad
    private JComboBox<String> cmbTipoCliente;
    private JTextField txtPorcentajeDescuento;
    private JSpinner spnPuntos;

    /**
     * CAMBIO: Constructor simplificado
     * ANTES: VentanaClientes(JFrame padre, Hotel hotel, String rutaClientesActual)
     * AHORA: VentanaClientes(JFrame padre, Hotel hotel)
     * Razón: La persistencia NO debe bloquear la carga de la ventana
     */
    public VentanaClientes(JFrame padre, Hotel hotel) {
        super(padre, "Gestión de Clientes", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(900, 600);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // Panel superior: Formulario de entrada
        JPanel pnlForm = new JPanel(new GridLayout(7, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        
        pnlForm.add(new JLabel("Identificación (ID / RUT):"));
        txtId = new JTextField(); 
        pnlForm.add(txtId);
        
        pnlForm.add(new JLabel("Nombre Completo / Razón Social:"));
        txtNombre = new JTextField(); 
        pnlForm.add(txtNombre);
        
        pnlForm.add(new JLabel("Correo Electrónico:"));
        txtCorreo = new JTextField(); 
        pnlForm.add(txtCorreo);
        
        pnlForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField(); 
        pnlForm.add(txtTelefono);

        pnlForm.add(new JLabel("Tipo de Cliente:"));
        cmbTipoCliente = new JComboBox<>(new String[] {"Esporadico", "Habitual"});
        pnlForm.add(cmbTipoCliente);

        pnlForm.add(new JLabel("% Descuento (si Habitual):"));
        txtPorcentajeDescuento = new JTextField("10");
        pnlForm.add(txtPorcentajeDescuento);

        pnlForm.add(new JLabel("Puntos Acumulados (si Habitual):"));
        spnPuntos = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        pnlForm.add(spnPuntos);

        add(pnlForm, BorderLayout.NORTH);

        // Panel central: Tabla de clientes
        String[] columnas = {"ID", "Nombre", "Email", "Teléfono", "Tipo Fidelidad"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollTabla = new JScrollPane(tablaClientes);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        add(scrollTabla, BorderLayout.CENTER);

        // Panel de resultado (texto)
        txtAreaOutput = new JTextArea();
        txtAreaOutput.setEditable(false);
        txtAreaOutput.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane scrollOutput = new JScrollPane(txtAreaOutput);
        scrollOutput.setBorder(BorderFactory.createTitledBorder("Mensajes"));
        scrollOutput.setPreferredSize(new Dimension(900, 120));
        add(scrollOutput, BorderLayout.SOUTH);

        // Panel de botones: Acciones operativas
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnReg = new JButton("➕ Registrar");
        JButton btnEdit = new JButton("✏️ Editar");
        JButton btnElim = new JButton("🗑️ Eliminar");
        JButton btnList = new JButton("📋 Listar Todos");
        JButton btnListHab = new JButton("👤 Habituales");
        JButton btnListEsp = new JButton("👤 Esporádicos");
        
        // CAMBIO: Aquí SOLO se abre la ventana, sin DialogoConfigurarArchivo
        JButton btnGuardar = new JButton("💾 Guardar Datos");
        JButton btnCerrar = new JButton("❌ Cerrar");

        pnlBotones.add(btnReg);
        pnlBotones.add(btnEdit);
        pnlBotones.add(btnElim);
        pnlBotones.add(new JSeparator(JSeparator.VERTICAL));
        pnlBotones.add(btnList);
        pnlBotones.add(btnListHab);
        pnlBotones.add(btnListEsp);
        pnlBotones.add(new JSeparator(JSeparator.VERTICAL));
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnCerrar);
        
        add(pnlBotones, BorderLayout.NORTH);

        // Vinculación de eventos
        btnReg.addActionListener(e -> registrarCliente());
        btnEdit.addActionListener(e -> editarCliente());
        btnElim.addActionListener(e -> eliminarCliente());
        btnList.addActionListener(e -> listarClientes());
        btnListHab.addActionListener(e -> listarClientesHabituales());
        btnListEsp.addActionListener(e -> listarClientesEsporadicos());
        btnGuardar.addActionListener(e -> guardarDatos());
        btnCerrar.addActionListener(e -> dispose());

        cmbTipoCliente.addActionListener(e -> actualizarCamposFidelidad());
        actualizarCamposFidelidad();
        
        // Cargar clientes al abrir
        listarClientes();
    }

    private void actualizarCamposFidelidad() {
        boolean habitual = "Habitual".equals(cmbTipoCliente.getSelectedItem());
        txtPorcentajeDescuento.setEnabled(habitual);
        spnPuntos.setEnabled(habitual);
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

    public void registrarCliente() {
        if (txtId.getText().isEmpty() || txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID y Nombre son campos obligatorios.");
            return;
        }
        
        String idCliente = txtId.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String direccion = ""; 
        java.time.LocalDate fechaRegistro = java.time.LocalDate.now();
        boolean activo = true;
        
        // Fidelidad
        String tipo = (String) cmbTipoCliente.getSelectedItem();
        dominio.IFidelidad fidelidad = null;
        if ("Habitual".equals(tipo)) {
            try {
                java.math.BigDecimal porcentaje = new java.math.BigDecimal(txtPorcentajeDescuento.getText().trim());
                int puntos = (Integer) spnPuntos.getValue();
                fidelidad = new ClienteHabitual(porcentaje, puntos);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Porcentaje inválido. Use un número (ej. 10 o 12.5).");
                return;
            }
        } else {
            fidelidad = new ClienteEsporadico();
        }
        
        String nombre = txtNombre.getText().trim();
        String apellido = ""; 
        String cedula = txtId.getText().trim(); 
        java.time.LocalDate fechaNacimiento = java.time.LocalDate.now(); 
        
        PersonaNatural nuevo = new PersonaNatural(
            idCliente, 
            telefono, 
            correo, 
            direccion, 
            fechaRegistro, 
            activo, 
            fidelidad, 
            nombre, 
            apellido, 
            cedula, 
            fechaNacimiento
        );
        
        hotel.registrarCliente(nuevo);
        txtAreaOutput.setText("✅ Cliente registrado exitosamente:\n" + nuevo.obtenerNombreCompleto());
        
        // Limpiar campos
        limpiarFormulario();
        
        // Refrescar tabla
        listarClientes();
    }

    public void editarCliente() {
        Cliente c = hotel.buscarCliente(txtId.getText().trim());
        if (c != null) {
            c.setCorreo(txtCorreo.getText().trim());
            c.setTelefono(txtTelefono.getText().trim());

            // Actualizar fidelidad según selección
            String tipo = (String) cmbTipoCliente.getSelectedItem();
            if ("Habitual".equals(tipo)) {
                try {
                    java.math.BigDecimal porcentaje = new java.math.BigDecimal(txtPorcentajeDescuento.getText().trim());
                    int puntos = (Integer) spnPuntos.getValue();
                    ClienteHabitual ch = new ClienteHabitual(porcentaje, puntos);
                    c.asignarFidelidad(ch);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Porcentaje inválido. Use un número (ej. 10 o 12.5).");
                    return;
                }
            } else {
                c.asignarFidelidad(new ClienteEsporadico());
            }

            hotel.modificarCliente(c);
            txtAreaOutput.setText("✅ Datos de contacto y fidelidad actualizados para: " + c.obtenerNombreCompleto());
            limpiarFormulario();
            listarClientes();
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    public void listarClientes() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        List<Cliente> clientes = hotel.getClientes();
        StringBuilder sb = new StringBuilder("=== TODOS LOS CLIENTES REGISTRADOS ===\n\n");
        
        for (Cliente c : clientes) {
            String fidelidad = c.getFidelidad() != null ? c.getFidelidad().obtenerTipoFidelidad() : "Sin fidelidad";
            modeloTabla.addRow(new Object[]{
                c.obtenerIdentificacion(),
                c.obtenerNombreCompleto(),
                c.getCorreo(),
                c.getTelefono(),
                fidelidad
            });
            
            sb.append("ID: ").append(c.obtenerIdentificacion())
              .append(" | Nombre: ").append(c.obtenerNombreCompleto())
              .append(" | Email: ").append(c.getCorreo()).append("\n");
        }
        
        txtAreaOutput.setText(sb.toString());
    }

    public void eliminarCliente() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del cliente a eliminar.");
            return;
        }
        
        boolean removido = hotel.eliminarCliente(id);
        if (removido) {
            txtAreaOutput.setText("✅ Cliente [" + id + "] eliminado correctamente del sistema.");
            limpiarFormulario();
            listarClientes();
        } else {
            JOptionPane.showMessageDialog(this, "La identificación ingresada no se encuentra registrada.");
        }
    }

    public void listarClientesHabituales() {
        modeloTabla.setRowCount(0);
        List<Cliente> habituales = hotel.listarClientesHabituales();
        StringBuilder sb = new StringBuilder("=== CLIENTES HABITUALES (Estrategia de Descuento Activa) ===\n\n");
        
        for (Cliente c : habituales) {
            modeloTabla.addRow(new Object[]{
                c.obtenerIdentificacion(),
                c.obtenerNombreCompleto(),
                c.getCorreo(),
                c.getTelefono(),
                "Habitual"
            });
            
            sb.append(" - ").append(c.obtenerNombreCompleto()).append(" (ID: ").append(c.obtenerIdentificacion()).append(")");
            if (c.getFidelidad() instanceof ClienteHabitual) {
                ClienteHabitual ch = (ClienteHabitual) c.getFidelidad();
                sb.append(" - Desc: ").append(ch.getPorcentajeDescuento()).append("% - Puntos: ").append(ch.getPuntosAcumulados());
            }
            sb.append("\n");
        }
        if (habituales.isEmpty()) sb.append("Ninguno cumple los criterios actualmente.");
        txtAreaOutput.setText(sb.toString());
    }

    public void listarClientesEsporadicos() {
        modeloTabla.setRowCount(0);
        StringBuilder sb = new StringBuilder("=== CLIENTES ESPORADICOS ===\n\n");
        
        for (Cliente c : hotel.getClientes()) {
            if (c.getFidelidad() == null || c.getFidelidad() instanceof ClienteEsporadico) {
                modeloTabla.addRow(new Object[]{
                    c.obtenerIdentificacion(),
                    c.obtenerNombreCompleto(),
                    c.getCorreo(),
                    c.getTelefono(),
                    "Esporádico"
                });
                
                sb.append(" - ").append(c.obtenerNombreCompleto()).append(" (ID: ").append(c.obtenerIdentificacion()).append(")\n");
            }
        }
        txtAreaOutput.setText(sb.toString());
    }

    /**
     * CAMBIO: Nuevo método para guardar datos
     * ANTES: Se llamaba DialogoConfigurarArchivo al cerrar
     * AHORA: Se llama explícitamente cuando el usuario lo solicita
     * Separa COMPLETAMENTE la persistencia de la UI
     */
    private void guardarDatos() {
        DialogoConfigurarArchivo dialogo = new DialogoConfigurarArchivo(this, "clientes");
        
        if (dialogo.estaConfigurado()) {
            if (dialogo.crearArchivoBinario()) {
                txtAreaOutput.setText("✅ ARCHIVO BINARIO CREADO EXITOSAMENTE\n\n" +
                        "Ubicación: " + dialogo.getRutaArchivo() + "\n" +
                        "Nombre: " + dialogo.getNombreArchivo() + "\n\n" +
                        "Todos los datos de clientes han sido guardados.");
                JOptionPane.showMessageDialog(this, "Datos guardados correctamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al crear archivo binario", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        txtPorcentajeDescuento.setText("10");
        spnPuntos.setValue(0);
        cmbTipoCliente.setSelectedIndex(0);
    }
}
