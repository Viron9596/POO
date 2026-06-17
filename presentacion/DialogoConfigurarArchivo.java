package presentacion;

import persistencia.GestorArchivos;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Diálogo que permite al usuario configurar la ruta base para los archivos binarios
 * La creación del archivo se realizará al final, después de las gestiones
 */
public class DialogoConfigurarArchivo extends JDialog {
    private JTextField txtNombreArchivo;
    private JTextField txtRutaBase;
    private JTextArea txtResultado;
    private boolean rutaConfigurada = false;
    private String rutaArchivo = "";
    private String nombreArchivo = "";

    public DialogoConfigurarArchivo(JFrame padre, String tipoGestion) {
        super(padre, "Configurar Almacenamiento - " + tipoGestion, true);
        this.nombreArchivo = tipoGestion.toLowerCase() + ".bin";
        configurarComponentes(tipoGestion);
        setVisible(true);
    }

    private void configurarComponentes(String tipoGestion) {
        setSize(600, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        // Panel superior: Configuración
        JPanel pnlConfiguracion = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlConfiguracion.setBorder(BorderFactory.createTitledBorder("Configuración de Ruta Base"));

        // Ruta base
        pnlConfiguracion.add(new JLabel("Ruta Base (carpeta externa):"));
        txtRutaBase = new JTextField(GestorArchivos.getRutaBase());
        txtRutaBase.setEditable(true);
        pnlConfiguracion.add(txtRutaBase);

        // Nombre del archivo
        pnlConfiguracion.add(new JLabel("Nombre del Archivo:"));
        txtNombreArchivo = new JTextField(nombreArchivo);
        txtNombreArchivo.setEditable(true);
        pnlConfiguracion.add(txtNombreArchivo);

        // Botones de acción
        JPanel pnlBotonesAccion = new JPanel(new FlowLayout());
        JButton btnExaminar = new JButton("📁 Examinar");
        JButton btnValidar = new JButton("✅ Validar Configuración");

        btnExaminar.addActionListener(e -> examinarCarpeta());
        btnValidar.addActionListener(e -> validarConfiguracion());

        pnlBotonesAccion.add(btnExaminar);
        pnlBotonesAccion.add(btnValidar);
        pnlConfiguracion.add(pnlBotonesAccion);

        add(pnlConfiguracion, BorderLayout.NORTH);

        // Panel central: Resultado y estado
        JPanel pnlResultado = new JPanel(new BorderLayout());
        pnlResultado.setBorder(BorderFactory.createTitledBorder("Estado de Configuración"));

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtResultado.setBackground(new Color(240, 240, 240));

        String mensajeInicial = "📋 INSTRUCCIONES:\n\n" +
                "1. La ruta base es donde se guardará el archivo binario\n" +
                "2. Personaliza la ruta si lo deseas (o usa la predeterminada)\n" +
                "3. Modifica el nombre del archivo si es necesario\n" +
                "4. Haz clic en 'Validar Configuración'\n" +
                "5. El archivo se creará al FINAL, después de ingresar datos\n\n" +
                "Ruta Base Actual: " + GestorArchivos.getRutaBase() + "\n" +
                "Nombre Archivo: " + nombreArchivo;

        txtResultado.setText(mensajeInicial);

        pnlResultado.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        add(pnlResultado, BorderLayout.CENTER);

        // Panel inferior: Botón de cierre
        JPanel pnlCierre = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnContinuar = new JButton("✔ Continuar");
        JButton btnCancelar = new JButton("✖ Cancelar");

        btnContinuar.setEnabled(false);
        btnContinuar.addActionListener(e -> {
            rutaConfigurada = true;
            dispose();
        });

        btnCancelar.addActionListener(e -> {
            rutaConfigurada = false;
            dispose();
        });

        pnlCierre.add(btnContinuar);
        pnlCierre.add(btnCancelar);
        add(pnlCierre, BorderLayout.SOUTH);

        // Guardar referencia al botón continuar
        btnContinuar.setName("btnContinuar");
    }

    private void examinarCarpeta() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(GestorArchivos.getRutaBase()));

        int resultado = fc.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String rutaSeleccionada = fc.getSelectedFile().getAbsolutePath();
            txtRutaBase.setText(rutaSeleccionada);
            txtResultado.append("\n✅ Ruta seleccionada: " + rutaSeleccionada);
        }
    }

    private void validarConfiguracion() {
        String rutaBase = txtRutaBase.getText().trim();
        nombreArchivo = txtNombreArchivo.getText().trim();

        if (rutaBase.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa una ruta base.",
                    "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nombreArchivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa un nombre para el archivo.",
                    "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Validar que la carpeta exista o se pueda crear
            File carpetaBase = new File(rutaBase);
            if (!carpetaBase.exists()) {
                if (carpetaBase.mkdirs()) {
                    txtResultado.setText("✅ CONFIGURACIÓN VÁLIDA\n\n" +
                            "Ruta Base: " + rutaBase + "\n" +
                            "Nombre Archivo: " + nombreArchivo + "\n\n" +
                            "ℹ️  La carpeta fue creada correctamente.\n\n" +
                            "✔ Ya puedes continuar para ingresar datos.\n" +
                            "El archivo binario se creará al final.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se puede crear la carpeta.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                txtResultado.setText("✅ CONFIGURACIÓN VÁLIDA\n\n" +
                        "Ruta Base: " + rutaBase + "\n" +
                        "Nombre Archivo: " + nombreArchivo + "\n" +
                        "Tamaño disponible: " + GestorArchivos.getEspacioDisponibleMB() + " MB\n\n" +
                        "✔ Ya puedes continuar para ingresar datos.\n" +
                        "El archivo binario se creará al final.");
            }

            // Actualizar configuración global
            GestorArchivos.setRutaBase(rutaBase);
            rutaArchivo = rutaBase + File.separator + nombreArchivo;

            // Habilitar botón Continuar
            habilitarBotonContinuar();

        } catch (Exception e) {
            txtResultado.setText("❌ ERROR EN VALIDACIÓN\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Verifica la ruta y los permisos.");
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void habilitarBotonContinuar() {
        Component[] componentes = getContentPane().getComponents();
        for (Component comp : componentes) {
            if (comp instanceof JPanel) {
                Component[] subComponentes = ((JPanel) comp).getComponents();
                for (Component subComp : subComponentes) {
                    if (subComp instanceof JButton) {
                        JButton btn = (JButton) subComp;
                        if (btn.getName() != null && btn.getText().contains("Continuar")) {
                            btn.setEnabled(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Crea el archivo binario en la ruta configurada
     * Se llama al final, después de las gestiones
     */
    public boolean crearArchivoBinario() {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists()) {
                archivo.createNewFile();
                GestorArchivos.registrarMovimiento("CREAR_ARCHIVO", nombreArchivo + " en " + GestorArchivos.getRutaBase());
                return true;
            }
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al crear archivo binario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean estaConfigurado() {
        return rutaConfigurada;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
}
