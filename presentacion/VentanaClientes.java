package presentacion;

import dominio.Cliente;
import dominio.PersonaNatural;
import dominio.Hotel;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import persistencia.*;

public class VentanaClientes extends JDialog {
    private Hotel hotel;
    private JTextField txtId, txtNombre, txtCorreo, txtTelefono;
    private JTextArea txtAreaOutput;

    public VentanaClientes(JFrame padre, Hotel hotel) {
        super(padre, "Gestión de Clientes", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(550, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // Formulario
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        
        pnlForm.add(new JLabel("Identificación (ID / RUT):"));
        txtId = new JTextField(); pnlForm.add(txtId);
        
        pnlForm.add(new JLabel("Nombre Completo / Razón Social:"));
        txtNombre = new JTextField(); pnlForm.add(txtNombre);
        
        pnlForm.add(new JLabel("Correo Electrónico:"));
        txtCorreo = new JTextField(); pnlForm.add(txtCorreo);
        
        pnlForm.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField(); pnlForm.add(txtTelefono);
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

        pnlBotones.add(btnReg);
        pnlBotones.add(btnEdit);
        pnlBotones.add(btnElim);
        pnlBotones.add(btnList);
        pnlBotones.add(btnListHab);
        add(pnlBotones, BorderLayout.SOUTH);

        // Vinculación de eventos
        btnReg.addActionListener(e -> registrarCliente());
        btnEdit.addActionListener(e -> editarCliente());
        btnElim.addActionListener(e -> eliminarCliente());
        btnList.addActionListener(e -> listarClientes());
        btnListHab.addActionListener(e -> listarClientesHabituales());
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

        public void registrarCliente() {
        if (txtId.getText().isEmpty() || txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID y Nombre son campos obligatorios.");
            return;
        }
        
        String idCliente = txtId.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();
        String direccion = ""; 
        java.time.LocalDate fechaRegistro = java.time.LocalDate.now();
        boolean activo = true;
        
        // CORRECCIÓN: Se especifica el paquete 'dominio' para evitar el error de importación
        dominio.IFidelidad fidelidad = null; 
        
        String nombre = txtNombre.getText();
        String apellido = ""; 
        String cedula = txtId.getText(); 
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
        txtAreaOutput.setText("Cliente registrado exitosamente:\n" + nuevo.obtenerNombreCompleto());
    }

    public void editarCliente() {
        Cliente c = hotel.buscarCliente(txtId.getText());
        if (c != null) {
            c.setCorreo(txtCorreo.getText()); // Corrección: 'setCorreo'
            c.setTelefono(txtTelefono.getText());
            txtAreaOutput.setText("Datos de contacto actualizados para: " + c.obtenerNombreCompleto());
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    // Reemplazar estos métodos en tu VentanaClientes.java:

public void listarClientes() {
    // Instanciamos el DAO de Clientes configurado en Serialización
    ClienteDAO clienteDAO = new ClienteDAO("clientes.dat", MetodoPersistencia.SERIALIZACION);
    
    StringBuilder sb = new StringBuilder("=== TODOS LOS CLIENTES REGISTRADOS ===\n");
    // Leemos directamente los datos físicos reales
    for (Cliente c : clienteDAO.listarTodo()) {
        sb.append("ID: ").append(c.obtenerIdentificacion())
          .append(" | Nombre: ").append(c.obtenerNombreCompleto())
          .append(" | Email: ").append(c.getCorreo()).append("\n");
    }
    txtAreaOutput.setText(sb.toString());
}

    public void eliminarCliente() {
        ClienteDAO clienteDAO = new ClienteDAO("clientes.dat", MetodoPersistencia.SERIALIZACION);

        // Eliminación directa en base de datos binaria
        boolean removido = clienteDAO.eliminar(txtId.getText());
        if (removido) {
            txtAreaOutput.setText("Cliente [" + txtId.getText() + "] eliminado correctamente del sistema y del disco.");
            listarClientes(); // Refrescar componentes de la pantalla
        } else {
            JOptionPane.showMessageDialog(this, "La identificación ingresada no se encuentra registrada.");
        }
    }

    
    public void listarClientesHabituales() {
        List<Cliente> habituales = hotel.listarClientesHabituales();
        StringBuilder sb = new StringBuilder("=== CLIENTES HABITUALES (Estrategia de Descuento Activa) ===\n");
        for (Cliente c : habituales) {
            sb.append(" - ").append(c.obtenerNombreCompleto()).append(" (ID: ").append(c.obtenerIdentificacion()).append(")\n");
        }
        if (habituales.isEmpty()) sb.append("Ninguno cumple los criterios actualmente.");
        txtAreaOutput.setText(sb.toString());
    }
}