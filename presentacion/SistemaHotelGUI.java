package presentacion;

import dominio.Hotel;
import persistencia.MetodoPersistencia;
import persistencia.GestorArchivos;
import javax.swing.*;
import java.awt.*;

public class SistemaHotelGUI extends JFrame {
    private Hotel hotel;
    private String rutaClientesActual = "";
    private String rutaHabitacionesActual = "";
    private String rutaReservasActual = "";
    private String rutaServiciosActual = "";

    public SistemaHotelGUI() {
        // Inicializamos el hotel con datos informativos estándar
        this.hotel = new Hotel("H001", "Hotel Gran Estancia", "Av. Principal 123", "555-0199");
        
        // Crear estructura de carpetas externa al iniciar
        GestorArchivos.crearEstructuraCarpetas();
        
        configurarVentana();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión Hotelera - " + hotel.getNombre());
        setSize(600, 420);
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

        JButton btnClientes = new JButton("👥 Gestión de Clientes");
        JButton btnHabitaciones = new JButton("🛏️ Gestión de Habitaciones");
        JButton btnReservas = new JButton("📅 Gestión de Reservas");
        JButton btnServicios = new JButton("🛎️ Gestión de Servicios");
        JButton btnFacturacion = new JButton("🧾 Módulo de Facturación");

        // Estilos
        Font fontBotones = new Font("Arial", Font.BOLD, 14);
        btnClientes.setFont(fontBotones);
        btnHabitaciones.setFont(fontBotones);
        btnReservas.setFont(fontBotones);
        btnServicios.setFont(fontBotones);
        btnFacturacion.setFont(fontBotones);

        panelBotones.add(btnClientes);
        panelBotones.add(btnHabitaciones);
        panelBotones.add(btnReservas);
        panelBotones.add(btnServicios);
        panelBotones.add(btnFacturacion);

        add(panelBotones, BorderLayout.CENTER);

        // Panel de ajustes: persistencia y carpeta de datos
        JPanel pnlSettings = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSettings.setBorder(BorderFactory.createTitledBorder("Ajustes de Persistencia"));
        JComboBox<MetodoPersistencia> cmbPersist = new JComboBox<>(MetodoPersistencia.values());
        cmbPersist.setSelectedItem(hotel.getMetodoPersistencia());
        JTextField txtFolder = new JTextField(18);
        txtFolder.setText(GestorArchivos.getRutaBase());
        txtFolder.setToolTipText("Carpeta donde están los archivos de datos (opcional)");
        JButton btnApply = new JButton("Aplicar");

        pnlSettings.add(new JLabel("Estrategia:"));
        pnlSettings.add(cmbPersist);
        pnlSettings.add(new JLabel("Carpeta datos:"));
        pnlSettings.add(txtFolder);
        pnlSettings.add(btnApply);

        add(pnlSettings, BorderLayout.SOUTH);

        // Listeners vinculados estrictamente a los métodos obligatorios del UML
        btnClientes.addActionListener(e -> abrirGestionClientes());
        btnHabitaciones.addActionListener(e -> abrirGestionHabitaciones());
        btnReservas.addActionListener(e -> abrirGestionReservas());
        btnServicios.addActionListener(e -> abrirGestionServicios());
        btnFacturacion.addActionListener(e -> abrirFacturacion());

        btnApply.addActionListener(e -> {
            MetodoPersistencia sel = (MetodoPersistencia) cmbPersist.getSelectedItem();
            String carpeta = txtFolder.getText().trim();
            
            if (!carpeta.isEmpty()) {
                GestorArchivos.setRutaBase(carpeta);
                hotel.setCarpetaDatos(carpeta);
                GestorArchivos.crearEstructuraCarpetas();
            }
            
            hotel.setMetodoPersistencia(sel);
            
            JOptionPane.showMessageDialog(this, 
                "✅ Persistencia aplicada: " + sel + "\n" +
                "📁 Carpeta: '" + (carpeta.isEmpty() ? "(raíz)" : carpeta) + "'\n" +
                "📍 Ruta base: " + GestorArchivos.getRutaBase(),
                "Persistencia Configurada", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

    public void abrirGestionClientes() {
        // Mostrar diálogo de configuración
        DialogoConfigurarArchivo dialogo = new DialogoConfigurarArchivo(this, "Clientes");
        
        if (dialogo.estaConfigurado()) {
            rutaClientesActual = dialogo.getRutaArchivo();
            GestorArchivos.registrarMovimiento("ABRIR_GESTION", "Gestión de Clientes");
            
            VentanaClientes vClientes = new VentanaClientes(this, hotel, rutaClientesActual);
            vClientes.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Operación cancelada. No se configuró el archivo de clientes.",
                "Operación Cancelada", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void abrirGestionHabitaciones() {
        // Mostrar diálogo de configuración
        DialogoConfigurarArchivo dialogo = new DialogoConfigurarArchivo(this, "Habitaciones");
        
        if (dialogo.estaConfigurado()) {
            rutaHabitacionesActual = dialogo.getRutaArchivo();
            GestorArchivos.registrarMovimiento("ABRIR_GESTION", "Gestión de Habitaciones");
            
            VentanaHabitaciones vHabitaciones = new VentanaHabitaciones(this, hotel, rutaHabitacionesActual);
            vHabitaciones.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Operación cancelada. No se configuró el archivo de habitaciones.",
                "Operación Cancelada", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void abrirGestionReservas() {
        // Mostrar diálogo de configuración
        DialogoConfigurarArchivo dialogo = new DialogoConfigurarArchivo(this, "Reservas");
        
        if (dialogo.estaConfigurado()) {
            rutaReservasActual = dialogo.getRutaArchivo();
            GestorArchivos.registrarMovimiento("ABRIR_GESTION", "Gestión de Reservas");
            
            VentanaReservas vReservas = new VentanaReservas(this, hotel, rutaReservasActual);
            vReservas.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Operación cancelada. No se configuró el archivo de reservas.",
                "Operación Cancelada", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void abrirGestionServicios() {
        // Mostrar diálogo de configuración
        DialogoConfigurarArchivo dialogo = new DialogoConfigurarArchivo(this, "Servicios");
        
        if (dialogo.estaConfigurado()) {
            rutaServiciosActual = dialogo.getRutaArchivo();
            GestorArchivos.registrarMovimiento("ABRIR_GESTION", "Gestión de Servicios");
            
            VentanaServicios vServicios = new VentanaServicios(this, hotel, rutaServiciosActual);
            vServicios.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Operación cancelada. No se configuró el archivo de servicios.",
                "Operación Cancelada", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void abrirFacturacion() {
        GestorArchivos.registrarMovimiento("ABRIR_GESTION", "Módulo de Facturación");
        VentanaFacturacion vFacturacion = new VentanaFacturacion(this, hotel);
        vFacturacion.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaHotelGUI gui = new SistemaHotelGUI();
            gui.setVisible(true);
            JOptionPane.showMessageDialog(gui, 
                "✅ Sistema Iniciado\n\n" +
                "📁 Carpeta de datos: " + GestorArchivos.getRutaBase() + "\n" +
                "📝 Los logs se guardarán en: " + GestorArchivos.getRutaLogs() + "\n\n" +
                "💡 Haz clic en cualquier gestión para configurar sus archivos.",
                "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
