package presentacion;

import dominio.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import persistencia.*;

public class VentanaReservas extends JDialog {
    private Hotel hotel;
    private JTextField txtIdReserva, txtIdCliente, txtNumHab, txtNoches, txtIdServicio;
    private JTextArea txtAreaOutput;
    
    // Referencia al diálogo de configuración
    private DialogoConfigurarArchivo dialogoConfiguracion;

    public VentanaReservas(JFrame padre, Hotel hotel) {
        super(padre, "Gestión Operativa de Reservas", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(600, 520);
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
        JButton btnGuardar = new JButton("💾 Guardar y Finalizar");

        pnlBotones.add(btnCrear);
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnDisp);
        pnlBotones.add(btnAddHab);
        pnlBotones.add(btnAddServ);
        pnlBotones.add(btnGuardar);
        add(pnlBotones, BorderLayout.SOUTH);

        btnCrear.addActionListener(e -> crearReserva());
        btnCancelar.addActionListener(e -> cancelarReserva());
        btnDisp.addActionListener(e -> consultarDisponibilidad());
        btnAddHab.addActionListener(e -> vincularHabitacionReserva());
        btnAddServ.addActionListener(e -> agregarServicioReserva());
        btnGuardar.addActionListener(e -> guardarYFinalizar());
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

            // 2. Validación de existencia del cliente (usando Hotel)
            Cliente c = hotel.buscarCliente(idCliente);
            
            if (c == null) {
                JOptionPane.showMessageDialog(this, "El cliente ingresado no existe.");
                return;
            }

            // 3. Crear y persistir usando el Hotel
            Reserva res = new Reserva(idReserva, LocalDate.now(), LocalDate.now(), LocalDate.now().plusDays(noches), noches, c);
            
            hotel.registrarReserva(res);

            txtAreaOutput.setText("✅ Reserva [" + idReserva + "] creada correctamente.");
            
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

        boolean exito = hotel.cancelarReserva(idReserva);

        if (exito) {
            txtAreaOutput.setText("✅ Reserva [" + idReserva + "] cancelada exitosamente.");
            txtIdReserva.setText(""); // Limpiamos el campo tras el éxito
        } else {
            JOptionPane.showMessageDialog(this, "La Reserva solicitada no existe o no pudo ser eliminada.");
        }
    }

    public void consultarDisponibilidad() {
        // Redirige al método interno de lógica
        java.util.List<Habitacion> habs = hotel.consultarHabitacionesDisponibles("TODAS");
        txtAreaOutput.setText("✅ Habitaciones listas para reservar: " + habs.size());
    }

    public void vincularHabitacionReserva() {
        boolean ok = hotel.agregarHabitacionAReserva(txtIdReserva.getText(), txtNumHab.getText());
        if (ok) {
            txtAreaOutput.setText("✅ Habitación " + txtNumHab.getText() + " vinculada a la Reserva " + txtIdReserva.getText());
        } else {
            JOptionPane.showMessageDialog(this, "Verifique existencia de la Reserva y de la Habitación.");
        }
    }

    public void agregarServicioReserva() {
        boolean ok = hotel.agregarServicioAReserva(txtIdReserva.getText(), txtIdServicio.getText());
        if (ok) {
            txtAreaOutput.setText("✅ Servicio cargado en la Reserva " + txtIdReserva.getText());
        } else {
            JOptionPane.showMessageDialog(this, "Verifique existencia de la Reserva y del Servicio.");
        }
    }

    private void guardarYFinalizar() {
        // Obtener la referencia del diálogo de configuración desde el padre
        JFrame padre = (JFrame) getOwner();
        if (padre instanceof SistemaHotelGUI) {
            // Crear instancia temporal del diálogo para acceder a su método
            dialogoConfiguracion = new DialogoConfigurarArchivo(padre, "reservas");
            if (dialogoConfiguracion.estaConfigurado()) {
                if (dialogoConfiguracion.crearArchivoBinario()) {
                    txtAreaOutput.setText("✅ ARCHIVO BINARIO CREADO EXITOSAMENTE\n\n" +
                            "Ubicación: " + dialogoConfiguracion.getRutaArchivo() + "\n" +
                            "Nombre: " + dialogoConfiguracion.getNombreArchivo() + "\n\n" +
                            "Todos los datos de reservas han sido guardados.");
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
