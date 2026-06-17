package presentacion;

import dominio.Cliente;
import dominio.PersonaNatural;
import dominio.ClienteHabitual;
import dominio.ClienteEsporadico;
import dominio.Hotel;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaClientes extends JDialog {
    private Hotel hotel;
    private JTextField txtId, txtNombre, txtCorreo, txtTelefono;
    private JTextArea txtAreaOutput;

    // Fidelidad
    private JComboBox<String> cmbTipoCliente;
    private JTextField txtPorcentajeDescuento;
    private JSpinner spnPuntos;
    
    // Referencia al diálogo de configuración
    private DialogoConfigurarArchivo dialogoConfiguracion;

    public VentanaClientes(JFrame padre, Hotel hotel) {
        super(padre, "Gestión de Clientes", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(600, 520);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // Ampliamos el formulario para incluir fidelidad
        JPanel pnlForm = new JPanel(new GridLayout(7, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        
        pnlForm.add(new JLabel("Identificación (ID / RUT):"));
        txtId = new JTextField(); pnlForm.add(txtId);
        
        pnlForm.add(new JLabel("Nombre Completo / Razón Social:"));
        txtNombre = new JTextField(); pnlForm.add(txtNombre);
        
        pnlForm.add(new JLabel("Correo Electrónico:"));
        txtCorreo = new JTextField(); pnlForm.add(txtCorreo);
        
        pnlForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField(); pnlForm.add(txtTelefono);

        pnlForm.add(new JLabel("Tipo de Cliente:"));
        cmbTipoCliente = new JComboBox<>(new String[] {"Esporadico", "Habitual"});
        pnlForm.add(cmbTipoCliente);

        pnlForm.add(new JLabel("% Descuento (si Habitual):"));
        txtPorcentajeDescuento = new JTextField("10"); // default 10%
        pnlForm.add(txtPorcentajeDescuento);

        pnlForm.add(new JLabel("Puntos Acumulados (si Habitual):"));
        spnPuntos = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        pnlForm.add(spnPuntos);

        add(pnlForm, BorderLayout.NORTH);

        // Consola de Resultados Interna
        txtAreaOutput = new JTextArea();
        txtAreaOutput.setEditable(false);
        add(new JScrollPane(txtAreaOutput), BorderLayout.CENTER);

        // Botonera de acciones UML
        JPanel pnlBotones = new JPanel(new FlowLayout());
        JButton btnReg = new JButton("Registrar");
        JButton btnEdit = new JButton("Editar");
        JButton btnElim = new JButton("Eliminar");
        JButton btnList = new JButton("Listar Todos");
        JButton btnListHab = new JButton("Habituales");
        JButton btnListEsp = new JButton("Esporádicos");
        JButton btnGuardar = new JButton("💾 Guardar y Finalizar");

        pnlBotones.add(btnReg);
        pnlBotones.add(btnEdit);
        pnlBotones.add(btnElim);
        pnlBotones.add(btnList);
        pnlBotones.add(btnListHab);
        pnlBotones.add(btnListEsp);
        pnlBotones.add(new JSeparator(JSeparator.VERTICAL));
        pnlBotones.add(btnGuardar);
        add(pnlBotones, BorderLayout.SOUTH);

        // Vinculación de eventos
        btnReg.addActionListener(e -> registrarCliente());
        btnEdit.addActionListener(e -> editarCliente());
        btnElim.addActionListener(e -> eliminarCliente());
        btnList.addActionListener(e -> listarClientes());
        btnListHab.addActionListener(e -> listarClientesHabituales());
        btnListEsp.addActionListener(e -> listarClientesEsporadicos());
        btnGuardar.addActionListener(e -> guardarYFinalizar());

        cmbTipoCliente.addActionListener(e -> actualizarCamposFidelidad());
        // Inicializar estado
        actualizarCamposFidelidad();
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

            // Persistimos el cambio
            hotel.modificarCliente(c);
            txtAreaOutput.setText("✅ Datos de contacto y fidelidad actualizados para: " + c.obtenerNombreCompleto());
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    public void listarClientes() {
        StringBuilder sb = new StringBuilder("=== TODOS LOS CLIENTES REGISTRADOS ===\n\n");
        for (Cliente c : hotel.getClientes()) {
            sb.append("ID: ").append(c.obtenerIdentificacion())
              .append(" | Nombre: ").append(c.obtenerNombreCompleto())
              .append(" | Email: ").append(c.getCorreo());
            if (c.getFidelidad() != null) {
                sb.append(" | Fidelidad: ").append(c.getFidelidad().obtenerTipoFidelidad());
                if (c.getFidelidad() instanceof ClienteHabitual) {
                    ClienteHabitual ch = (ClienteHabitual) c.getFidelidad();
                    sb.append(" (Desc: ").append(ch.getPorcentajeDescuento()).append("%)");
                }
            }
            sb.append("\n");
        }
        txtAreaOutput.setText(sb.toString());
    }

    public void eliminarCliente() {
        boolean removido = hotel.eliminarCliente(txtId.getText().trim());
        if (removido) {
            txtAreaOutput.setText("✅ Cliente [" + txtId.getText() + "] eliminado correctamente del sistema.");
            listarClientes(); // Refrescar componentes de la pantalla
        } else {
            JOptionPane.showMessageDialog(this, "La identificación ingresada no se encuentra registrada.");
        }
    }

    public void listarClientesHabituales() {
        List<Cliente> habituales = hotel.listarClientesHabituales();
        StringBuilder sb = new StringBuilder("=== CLIENTES HABITUALES (Estrategia de Descuento Activa) ===\n\n");
        for (Cliente c : habituales) {
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
        StringBuilder sb = new StringBuilder("=== CLIENTES ESPORADICOS ===\n\n");
        for (Cliente c : hotel.getClientes()) {
            // Consideramos esporádicos a los que tengan estrategia ClienteEsporadico o no tengan estrategia
            if (c.getFidelidad() == null || c.getFidelidad() instanceof ClienteEsporadico) {
                sb.append(" - ").append(c.obtenerNombreCompleto()).append(" (ID: ").append(c.obtenerIdentificacion()).append(")\n");
            }
        }
        txtAreaOutput.setText(sb.toString());
    }

    private void guardarYFinalizar() {
        // Obtener la referencia del diálogo de configuración desde el padre
        JFrame padre = (JFrame) getOwner();
        if (padre instanceof SistemaHotelGUI) {
            // Crear instancia temporal del diálogo para acceder a su método
            dialogoConfiguracion = new DialogoConfigurarArchivo(padre, "clientes");
            if (dialogoConfiguracion.estaConfigurado()) {
                if (dialogoConfiguracion.crearArchivoBinario()) {
                    txtAreaOutput.setText("✅ ARCHIVO BINARIO CREADO EXITOSAMENTE\n\n" +
                            "Ubicación: " + dialogoConfiguracion.getRutaArchivo() + "\n" +
                            "Nombre: " + dialogoConfiguracion.getNombreArchivo() + "\n\n" +
                            "Todos los datos de clientes han sido guardados.");
                    JOptionPane.showMessageDialog(this, "Datos guardados correctamente en archivo binario", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al crear archivo binario", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
