package presentacion;

import dominio.*;
import javax.swing.*;
import java.awt.*;
import persistencia.*;

public class VentanaFacturacion extends JDialog {
    private Hotel hotel;
    private JTextField txtIdReserva;
    private JTextArea txtAreaFactura;
    
    // Referencia al diálogo de configuración
    private DialogoConfigurarArchivo dialogoConfiguracion;

    public VentanaFacturacion(JFrame padre, Hotel hotel) {
        super(padre, "Módulo de Facturación Informativa", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(550, 520);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel pnlFiltro = new JPanel(new FlowLayout());
        pnlFiltro.add(new JLabel("Ingrese ID Reserva para Liquidación:"));
        txtIdReserva = new JTextField(12);
        pnlFiltro.add(txtIdReserva);
        
        JButton btnCalcular = new JButton("Generar Factura Informativa");
        pnlFiltro.add(btnCalcular);
        add(pnlFiltro, BorderLayout.NORTH);

        txtAreaFactura = new FontAndAreaConfig().getArea();
        add(new JScrollPane(txtAreaFactura), BorderLayout.CENTER);

        JPanel pnlBotonesInf = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("💾 Guardar y Finalizar");
        pnlBotonesInf.add(btnGuardar);
        add(pnlBotonesInf, BorderLayout.SOUTH);

        btnCalcular.addActionListener(e -> procesarFacturacionCompleta());
        btnGuardar.addActionListener(e -> guardarYFinalizar());
    }

    private void procesarFacturacionCompleta() {
        ReservaDAO reservaDAO = new ReservaDAO("reservas.dat", MetodoPersistencia.SERIALIZACION);
        FacturaDAO facturaDAO = new FacturaDAO("facturas.dat", MetodoPersistencia.SERIALIZACION);

        // Obtenemos la reserva real con todo su grafo de habitaciones y servicios cargados del disco
        Reserva res = reservaDAO.buscarPorId(txtIdReserva.getText());

        if (res != null) {
            // Lógica de cálculo delegada al dominio (Hotel / Reserva / Factura)
            Factura fac = hotel.generarFacturaReserva(res.getIdReserva()); 
            if (fac != null) {
                fac.emitirFactura(); 
                facturaDAO.guardar(fac); // Guardado físico de la transacción en facturas.dat
                txtAreaFactura.setText(fac.exportarFactura());
            } else {
                JOptionPane.showMessageDialog(this, "Error al procesar los subtotales de la factura.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "La Reserva solicitada no existe.");
        }
    }

    private void guardarYFinalizar() {
        // Obtener la referencia del diálogo de configuración desde el padre
        JFrame padre = (JFrame) getOwner();
        if (padre instanceof SistemaHotelGUI) {
            // Crear instancia temporal del diálogo para acceder a su método
            dialogoConfiguracion = new DialogoConfigurarArchivo(padre, "facturas");
            if (dialogoConfiguracion.estaConfigurado()) {
                if (dialogoConfiguracion.crearArchivoBinario()) {
                    txtAreaFactura.setText("✅ ARCHIVO BINARIO CREADO EXITOSAMENTE\n\n" +
                            "Ubicación: " + dialogoConfiguracion.getRutaArchivo() + "\n" +
                            "Nombre: " + dialogoConfiguracion.getNombreArchivo() + "\n\n" +
                            "Todos los datos de facturación han sido guardados.");
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

    // Clase utilitaria estática interna para manejar la tipografía tipo ticket
    private static class FontAndAreaConfig {
        public JTextArea getArea() {
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            area.setBackground(new Color(245, 247, 250));
            return area;
        }
    }
}
