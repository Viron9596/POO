package presentacion;
import persistencia.*;

import dominio.Hotel;
import dominio.Habitacion;
import dominio.Sencilla;
import dominio.EstadoHabitacion;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;


public class VentanaHabitaciones extends JDialog {
    private Hotel hotel;
    private JTextField txtNumero, txtPrecio, txtPiso;
    private JComboBox<EstadoHabitacion> cmbEstado;
    private JTextArea txtAreaOutput;
    
    // Referencia al diálogo de configuración
    private DialogoConfigurarArchivo dialogoConfiguracion;

    public VentanaHabitaciones(JFrame padre, Hotel hotel) {
        super(padre, "Gestión de Habitaciones", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(550, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Datos de Habitación"));

        pnlForm.add(new JLabel("Número Habitación:"));
        txtNumero = new JTextField(); pnlForm.add(txtNumero);

        pnlForm.add(new JLabel("Precio base por Noche ($):"));
        txtPrecio = new JTextField("50000"); pnlForm.add(txtPrecio);

        pnlForm.add(new JLabel("Piso / Planta:"));
        txtPiso = new JTextField("1"); pnlForm.add(txtPiso);

        pnlForm.add(new JLabel("Estado Inicial:"));
        cmbEstado = new JComboBox<>(EstadoHabitacion.values());
        pnlForm.add(cmbEstado);
        add(pnlForm, BorderLayout.NORTH);

        txtAreaOutput = new JTextArea();
        txtAreaOutput.setEditable(false);
        add(new JScrollPane(txtAreaOutput), BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout());
        JButton btnReg = new JButton("Registrar Sencilla");
        JButton btnEdit = new JButton("Editar Precio");
        JButton btnElim = new JButton("Eliminar");
        JButton btnDisp = new JButton("Consultar Disponibilidad");
        JButton btnGuardar = new JButton("💾 Guardar y Finalizar");

        pnlBotones.add(btnReg);
        pnlBotones.add(btnEdit);
        pnlBotones.add(btnElim);
        pnlBotones.add(btnDisp);
        pnlBotones.add(new JSeparator(JSeparator.VERTICAL));
        pnlBotones.add(btnGuardar);
        add(pnlBotones, BorderLayout.SOUTH);

        btnReg.addActionListener(e -> registrarHabitacion());
        btnEdit.addActionListener(e -> editarHabitacion());
        btnElim.addActionListener(e -> eliminarHabitacion());
        btnDisp.addActionListener(e -> consultarDisponibilidad());
        btnGuardar.addActionListener(e -> guardarYFinalizar());
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

    public void registrarHabitacion() {
        try {
            BigDecimal precio = new BigDecimal(txtPrecio.getText());
            int piso = Integer.parseInt(txtPiso.getText());
            // Instanciamos Sencilla por defecto técnico
            Sencilla hab = new Sencilla(txtNumero.getText(), "Habitación Estándar", precio, 
                    (EstadoHabitacion) cmbEstado.getSelectedItem(), 2, piso, new ArrayList<>(), true);
            
            hotel.registrarHabitacion(hab);
            txtAreaOutput.setText("✅ Habitación " + hab.getNumeroHabitacion() + " registrada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en formato numérico.");
        }
    }

    public void editarHabitacion() {
        try {
            BigDecimal nuevo = new BigDecimal(txtPrecio.getText());
            boolean ok = hotel.actualizarPrecioHabitacion(txtNumero.getText(), nuevo);
            if (ok) {
                txtAreaOutput.setText("✅ Tarifa actualizada con éxito: $" + nuevo + " para la Habitación " + txtNumero.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Habitación no encontrada en los registros.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato de precio inválido.");
        }
    }

    public void eliminarHabitacion() {
        boolean removido = hotel.eliminarHabitacion(txtNumero.getText());
        if (removido) {
            txtAreaOutput.setText("✅ Habitación Nº " + txtNumero.getText() + " removida del inventario.");
        } else {
            JOptionPane.showMessageDialog(this, "Número de habitación inexistente.");
        }
    }

    public void consultarDisponibilidad() {
        java.util.List<Habitacion> libres = hotel.consultarHabitacionesDisponibles("TODAS");
        StringBuilder sb = new StringBuilder("=== HABITACIONES DISPONIBLES EN EL SISTEMA ===\n\n");
        for (Habitacion h : libres) {
            sb.append("Habitación Nº: ").append(h.getNumeroHabitacion())
              .append(" | Tipo: ").append(h.getClass().getSimpleName())
              .append(" | Tarifa Base: $").append(h.getPrecioPorNoche()).append("\n");
        }
        if (libres.isEmpty()) sb.append("Sin disponibilidad de habitaciones actualmente.");
        txtAreaOutput.setText(sb.toString());
    }

    private void guardarYFinalizar() {
        // Obtener la referencia del diálogo de configuración desde el padre
        JFrame padre = (JFrame) getOwner();
        if (padre instanceof SistemaHotelGUI) {
            // Crear instancia temporal del diálogo para acceder a su método
            dialogoConfiguracion = new DialogoConfigurarArchivo(padre, "habitaciones");
            if (dialogoConfiguracion.estaConfigurado()) {
                if (dialogoConfiguracion.crearArchivoBinario()) {
                    txtAreaOutput.setText("✅ ARCHIVO BINARIO CREADO EXITOSAMENTE\n\n" +
                            "Ubicación: " + dialogoConfiguracion.getRutaArchivo() + "\n" +
                            "Nombre: " + dialogoConfiguracion.getNombreArchivo() + "\n\n" +
                            "Todos los datos de habitaciones han sido guardados.");
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
