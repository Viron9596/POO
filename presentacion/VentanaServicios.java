package presentacion;

import dominio.Hotel;
import dominio.ServicioHotel;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class VentanaServicios extends JDialog {
    private Hotel hotel;
    private JTextField txtIdServicio, txtNombre, txtCosto;
    private JTextArea txtAreaOutput;
    
    // Referencia al diálogo de configuración
    private DialogoConfigurarArchivo dialogoConfiguracion;

    public VentanaServicios(JFrame padre, Hotel hotel) {
        super(padre, "Catálogo de Servicios Adicionales", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(500, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Ficha del Servicio"));

        pnlForm.add(new JLabel("ID Servicio (Numérico):"));
        txtIdServicio = new JTextField(); pnlForm.add(txtIdServicio);

        pnlForm.add(new JLabel("Nombre del Servicio:"));
        txtNombre = new JTextField(); pnlForm.add(txtNombre);

        pnlForm.add(new JLabel("Costo Unitario ($):"));
        txtCosto = new JTextField(); pnlForm.add(txtCosto);
        add(pnlForm, BorderLayout.NORTH);

        txtAreaOutput = new JTextArea();
        txtAreaOutput.setEditable(false);
        add(new JScrollPane(txtAreaOutput), BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout());
        JButton btnReg = new JButton("Registrar Servicio");
        JButton btnEdit = new JButton("Editar Costo");
        JButton btnElim = new JButton("Eliminar");
        JButton btnGuardar = new JButton("💾 Guardar y Finalizar");

        pnlBotones.add(btnReg);
        pnlBotones.add(btnEdit);
        pnlBotones.add(btnElim);
        pnlBotones.add(new JSeparator(JSeparator.VERTICAL));
        pnlBotones.add(btnGuardar);
        add(pnlBotones, BorderLayout.SOUTH);

        btnReg.addActionListener(e -> registrarServicio());
        btnEdit.addActionListener(e -> editarServicio());
        btnElim.addActionListener(e -> eliminarServicio());
        btnGuardar.addActionListener(e -> guardarYFinalizar());
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

    public void registrarServicio() {
        try {
            BigDecimal id = new BigDecimal(txtIdServicio.getText());
            BigDecimal costo = new BigDecimal(txtCosto.getText());
            ServicioHotel serv = new ServicioHotel(id, txtNombre.getText(), "Servicio de valor agregado", costo, true);
            hotel.registrarServicio(serv);
            txtAreaOutput.setText("✅ Servicio '" + serv.getNombre() + "' adicionado al catálogo.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Datos numéricos incorrectos.");
        }
    }

    public void editarServicio() {
        try {
            BigDecimal nuevoCosto = new BigDecimal(txtCosto.getText().trim());
            String idStr = txtIdServicio.getText().trim();
            boolean ok = hotel.actualizarCostoServicio(idStr, nuevoCosto);
            if (ok) {
                txtAreaOutput.setText("✅ Costo actualizado en el catálogo: $" + nuevoCosto);
            } else {
                JOptionPane.showMessageDialog(this, "ID de Servicio no encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID y el costo deben ser valores numéricos válidos.");
        }
    }

    public void eliminarServicio() {
        try {
            String idStr = txtIdServicio.getText().trim();
            boolean removido = hotel.eliminarServicio(idStr);
            if (removido) {
                txtAreaOutput.setText("✅ Servicio revocado del catálogo.");
                txtIdServicio.setText("");
                txtCosto.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "El Servicio no existe.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "El ID de Servicio debe ser un valor numérico válido.");
        }
    }

    private void guardarYFinalizar() {
        // Obtener la referencia del diálogo de configuración desde el padre
        JFrame padre = (JFrame) getOwner();
        if (padre instanceof SistemaHotelGUI) {
            // Crear instancia temporal del diálogo para acceder a su método
            dialogoConfiguracion = new DialogoConfigurarArchivo(padre, "servicios");
            if (dialogoConfiguracion.estaConfigurado()) {
                if (dialogoConfiguracion.crearArchivoBinario()) {
                    txtAreaOutput.setText("✅ ARCHIVO BINARIO CREADO EXITOSAMENTE\n\n" +
                            "Ubicación: " + dialogoConfiguracion.getRutaArchivo() + "\n" +
                            "Nombre: " + dialogoConfiguracion.getNombreArchivo() + "\n\n" +
                            "Todos los datos de servicios han sido guardados.");
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
