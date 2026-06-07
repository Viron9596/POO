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

    public VentanaServicios(JFrame padre, Hotel hotel) {
        super(padre, "Catálogo de Servicios Adicionales", true);
        this.hotel = hotel;
        configurarComponentes();
    }

    private void configurarComponentes() {
        setSize(500, 400);
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

        pnlBotones.add(btnReg);
        pnlBotones.add(btnEdit);
        pnlBotones.add(btnElim);
        add(pnlBotones, BorderLayout.SOUTH);

        btnReg.addActionListener(e -> registrarServicio());
        btnEdit.addActionListener(e -> editarServicio());
        btnElim.addActionListener(e -> eliminarServicio());
    }

    // --- MÉTODOS EXIGIDOS POR EL DIAGRAMA UML ---

    public void registrarServicio() {
        try {
            BigDecimal id = new BigDecimal(txtIdServicio.getText());
            BigDecimal costo = new BigDecimal(txtCosto.getText());
            ServicioHotel serv = new ServicioHotel(id, txtNombre.getText(), "Servicio de valor agregado", costo, true);
            hotel.registrarServicio(serv);
            txtAreaOutput.setText("Servicio institucional '" + serv.getNombre() + "' adicionado al catálogo corporativo.");
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
            txtAreaOutput.setText("Costo actualizado en el catálogo corporativo: $" + nuevoCosto);
        } else {
            JOptionPane.showMessageDialog(this, "ID de Servicio corporativo no encontrado.");
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
            txtAreaOutput.setText("Servicio revocado definitivamente del catálogo institucional.");
            txtIdServicio.setText("");
            txtCosto.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "El Servicio no existe en el almacenamiento.");
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "El ID de Servicio debe ser un valor numérico válido.");
    }
}
}
