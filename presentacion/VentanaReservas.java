package presentacion;

import dominio.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import persistencia.*;

public class VentanaReservas extends JDialog {
    private Hotel hotel;
    private JTextField txtIdReserva, txtIdCliente, txtNumHab, txtNoches, txtIdServicio;
    private JTextArea txtAreaOutput;

    public VentanaReservas(JFrame padre, Hotel hotel) {
        super(padre, "Gestión Operativa de Reservas", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(600, 480);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Control Operativo de Reserva"));

        pnlForm.add(new JLabel("ID Reserva:"));
        txtIdReserva = new JTextField(); pnlForm.add(txtIdReserva);

        pnlForm.add(new JLabel("ID Cliente Hospedado:"));
        txtIdCliente = new JTextField(); pnlForm.add(txtIdCliente);

        pnlForm.add(new JLabel("Número Habitación:"));
        txtNumHab = new JTextField(); pnlForm.add(txtNumHab);

        pnlForm.add(new JLabel("Cantidad de Noches:"));
        txtNoches = new JTextField("2"); pnlForm.add(txtNoches);
        
        pnlForm.add(new JLabel("ID Servicio a Cargar (Opcional):"));
        txtIdServicio = new JTextField(); pnlForm.add(txtIdServicio);
        add(pnlForm, BorderLayout.NORTH);

        txtAreaOutput = new JTextArea();
        txtAreaOutput.setEditable(false);
        add(new JScrollPane(txtAreaOutput), BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new GridLayout(2, 3, 5, 5));
        JButton btnCrear = new JButton("Crear Reserva");
        JButton btnCancelar = new JButton("Cancelar Reserva");
        JButton btnDisp = new JButton("Consultar Disponibles");
        JButton btnAddHab = new JButton("+ Agregar Habitación");
        JButton btnAddServ = new JButton("+ Agregar Servicio");

        pnlBotones.add(btnCrear);
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnDisp);
        pnlBotones.add(btnAddHab);
        pnlBotones.add(btnAddServ);
        add(pnlBotones, BorderLayout.SOUTH);

        btnCrear.addActionListener(e -> crearReserva());
        btnCancelar.addActionListener(e -> cancelarReserva());
        btnDisp.addActionListener(e -> consultarDisponibilidad());
        btnAddHab.addActionListener(e -> vincularHabitacionReserva());
        btnAddServ.addActionListener(e -> agregarServicioReserva());
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

public void crearReserva() {
    // 1. Validación de interfaz
    String idReserva = txtIdReserva.getText().trim();
    String idCliente = txtIdCliente.getText().trim();
    String nochesStr = txtNoches.getText().trim();

    if (idReserva.isEmpty() || idCliente.isEmpty() || nochesStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
        return;
    }

    try {
        int noches = Integer.parseInt(nochesStr);
        if (noches <= 0) throw new NumberFormatException();

        // 2. Validación de existencia del cliente (usando DAO)
        ClienteDAO clienteDAO = new ClienteDAO("clientes.dat", MetodoPersistencia.SERIALIZACION);
        Cliente c = clienteDAO.buscarPorId(idCliente);
        
        if (c == null) {
            JOptionPane.showMessageDialog(this, "El cliente ingresado no existe.");
            return;
        }

        // 3. Crear y persistir usando ÚNICAMENTE el DAO
        Reserva res = new Reserva(idReserva, LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(noches), noches, c);
        
        ReservaDAO reservaDAO = new ReservaDAO("reservas.dat", MetodoPersistencia.SERIALIZACION);
        reservaDAO.guardar(res); // El DAO se encarga de serializar y actualizar la lista en memoria

        txtAreaOutput.setText("Reserva [" + idReserva + "] creada y persistida en disco exitosamente.");
        
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "El campo 'Noches' debe ser un número entero mayor a cero.");
    }
}

    public void cancelarReserva() {
        String idReserva = txtIdReserva.getText().trim();
        if (idReserva.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID de la Reserva que desea cancelar.");
            return;
        }

        // Usamos el DAO como única fuente de verdad para la operación
        ReservaDAO reservaDAO = new ReservaDAO("reservas.dat", MetodoPersistencia.SERIALIZACION);

        // Al eliminar del DAO, el método interno ya maneja la sincronización del archivo (.dat)
        boolean exito = reservaDAO.eliminar(idReserva);

        if (exito) {
            // Opcional: Si el objeto 'hotel' debe mantenerse sincronizado en RAM,
            // deberías refrescar la lista del hotel desde el DAO después de la eliminación.
            txtAreaOutput.setText("Reserva [" + idReserva + "] cancelada exitosamente del sistema.");
            txtIdReserva.setText(""); // Limpiamos el campo tras el éxito
        } else {
            JOptionPane.showMessageDialog(this, "La Reserva solicitada no existe o no pudo ser eliminada.");
        }
    }

    public void consultarDisponibilidad() {
        // Redirige al método interno de lógica
        java.util.List<Habitacion> habs = hotel.consultarHabitacionesDisponibles("TODAS");
        txtAreaOutput.setText("Habitaciones listas para reservar: " + habs.size());
    }

    // Reemplazar estos métodos en tu VentanaReservas.java:

    public void vincularHabitacionReserva() {
    ReservaDAO reservaDAO = new ReservaDAO("reservas.dat", MetodoPersistencia.SERIALIZACION);
    HabitacionDAO habitacionDAO = new HabitacionDAO("habitaciones.dat", MetodoPersistencia.SERIALIZACION);

    Reserva res = reservaDAO.buscarPorId(txtIdReserva.getText());
    // HabitacionDAO maneja llaves String (número de habitación)
    Habitacion hab = habitacionDAO.buscarPorId(txtNumHab.getText()); 

    if (res != null && hab != null) {
        res.getHabitaciones().add(hab); // Agregación directa a la lista interna de la reserva
        reservaDAO.guardar(res); // Sincroniza y persiste el estado modificado en disco
        txtAreaOutput.setText("Habitación " + hab.getNumeroHabitacion() + " vinculada y persistida en la Reserva " + res.getIdReserva());
    } else {
        JOptionPane.showMessageDialog(this, "Verifique existencia de la Reserva y de la Habitación en disco.");
    }
}

public void agregarServicioReserva() {
    ReservaDAO reservaDAO = new ReservaDAO("reservas.dat", MetodoPersistencia.SERIALIZACION);
    ServicioDAO servicioDAO = new ServicioDAO("servicios.dat", MetodoPersistencia.SERIALIZACION);

    Reserva res = reservaDAO.buscarPorId(txtIdReserva.getText());
    // ServicioDAO busca por String internamente
    ServicioHotel sh = servicioDAO.buscarPorId(txtIdServicio.getText()); 

    if (res != null && sh != null) {
        // Creamos el consumo de servicio vinculando la instancia del servicio corporativo hallado
        ConsumoServicio cs = new ConsumoServicio(
            "CON-" + (System.currentTimeMillis() % 1000), 
            LocalDate.now(), 
            1, 
            "Consumo desde UI", 
            sh
        );
        
        res.getConsumosServicios().add(cs); // Adición a la lista de consumos de la Reserva
        reservaDAO.guardar(res); // Sincroniza el nuevo grafo de objetos binariamente en el archivo
        txtAreaOutput.setText("Servicio institucional '" + sh.getNombre() + "' cargado y guardado en la Reserva " + res.getIdReserva());
    } else {
        JOptionPane.showMessageDialog(this, "Verifique existencia de la Reserva y del Servicio en disco.");
    }
}
}