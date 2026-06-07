package presentacion;

import dominio.Hotel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SistemaHotelGUI extends JFrame {
    private Hotel hotel;

    public SistemaHotelGUI() {
        // Inicializamos el hotel con datos informativos estándar
        this.hotel = new Hotel("H001", "Hotel Gran Estancia", "Av. Principal 123", "555-0199");
        configurarVentana();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión Hotelera - " + hotel.getNombre());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de encabezado
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(41, 128, 185));
        JLabel lblTitulo = new JLabel("PANEL DE CONTROL GENERAL");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel de Botones (Botonera central)
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton btnClientes = new JButton("Gestión de Clientes");
        JButton btnHabitaciones = new JButton("Gestión de Habitaciones");
        JButton btnReservas = new JButton("Gestión de Reservas");
        JButton btnServicios = new JButton("Gestión de Servicios");
        JButton btnFacturacion = new JButton("Módulo de Facturación");

        panelBotones.add(btnClientes);
        panelBotones.add(btnHabitaciones);
        panelBotones.add(btnReservas);
        panelBotones.add(btnServicios);
        panelBotones.add(btnFacturacion);

        add(panelBotones, BorderLayout.CENTER);

        // Listeners vinculados estrictamente a los métodos obligatorios del UML
        btnClientes.addActionListener(e -> abrirGestionClientes());
        btnHabitaciones.addActionListener(e -> abrirGestionHabitaciones());
        btnReservas.addActionListener(e -> abrirGestionReservas());
        btnServicios.addActionListener(e -> abrirGestionServicios());
        btnFacturacion.addActionListener(e -> abrirFacturacion());
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

    public void abrirGestionClientes() {
        VentanaClientes vClientes = new VentanaClientes(this, hotel);
        vClientes.setVisible(true);
    }

    public void abrirGestionHabitaciones() {
        VentanaHabitaciones vHabitaciones = new VentanaHabitaciones(this, hotel);
        vHabitaciones.setVisible(true);
    }

    public void abrirGestionReservas() {
        VentanaReservas vReservas = new VentanaReservas(this, hotel);
        vReservas.setVisible(true);
    }

    public void abrirGestionServicios() {
        VentanaServicios vServicios = new VentanaServicios(this, hotel);
        vServicios.setVisible(true);
    }

    public void abrirFacturacion() {
        VentanaFacturacion vFacturacion = new VentanaFacturacion(this, hotel);
        vFacturacion.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaHotelGUI gui = new SistemaHotelGUI();
            gui.setVisible(true);
        });
    }
}