package presentacion;

import persistencia.GestorArchivos;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Diálogo que permite al usuario crear y configurar archivos binarios
 * antes de realizar operaciones de gestión
 */
public class DialogoConfigurarArchivo extends JDialog {
    private JTextField txtNombreArchivo;
    private JTextField txtRutaBase;
    private JTextArea txtResultado;
    private boolean archivoCreadoExitosamente = false;
    private String rutaArchivo = "";

    public DialogoConfigurarArchivo(JFrame padre, String tipoGestion) {
        super(padre, "Configurar Almacenamiento - " + tipoGestion, true);
        configurarComponentes(tipoGestion);
        setVisible(true);
    }

    private void configurarComponentes(String tipoGestion) {
        setSize(600, 400);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        // Panel superior: Configuración
        JPanel pnlConfiguracion = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlConfiguracion.setBorder(BorderFactory.createTitledBorder("Configuración de Archivo Binario"));

        // Ruta base
        pnlConfiguracion.add(new JLabel("Ruta Base (carpeta externa):"));
        txtRutaBase = new JTextField(GestorArchivos.getRutaBase());
        txtRutaBase.setEditable(true);
        pnlConfiguracion.add(txtRutaBase);

        // Nombre del archivo
        pnlConfiguracion.add(new JLabel("Nombre del Archivo:"));
        txtNombreArchivo = new JTextField(tipoGestion.toLowerCase() + ".bin");
        pnlConfiguracion.add(txtNombreArchivo);

        // Botones de acción
        JPanel pnlBotonesAccion = new JPanel(new FlowLayout());
        JButton btnExaminar = new JButton("📁 Examinar");
        JButton btnCrear = new JButton("✅ Crear/Usar Archivo");

        btnExaminar.addActionListener(e -> examinarCarpeta());
        btnCrear.addActionListener(e -> crearArchivo());

        pnlBotonesAccion.add(btnExaminar);
        pnlBotonesAccion.add(btnCrear);
        pnlConfiguracion.add(pnlBotonesAccion);

        add(pnlConfiguracion, BorderLayout.NORTH);

        // Panel central: Resultado y estado
        JPanel pnlResultado = new JPanel(new BorderLayout());
        pnlResultado.setBorder(BorderFactory.createTitledBorder("Estado del Archivo"));

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtResultado.setBackground(new Color(240, 240, 240));

        String mensajeInicial = "📋 INFORMACIÓN INICIAL:\n\n" +
                "Ruta Base: " + GestorArchivos.getRutaBase() + "\n\n" +
                "1. Personaliza la ruta base si lo deseas\n" +
                "2. Ingresa un nombre para el archivo binario\n" +
                "3. Haz clic en 'Crear/Usar Archivo'\n" +
                "4. El sistema creará o usará el archivo automáticamente\n\n" +
                "Los archivos se guardarán FUERA del proyecto,\n" +
                "en la carpeta que especifiques.";

        txtResultado.setText(mensajeInicial);

        pnlResultado.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        add(pnlResultado, BorderLayout.CENTER);

        // Panel inferior: Botón de cierre
        JPanel pnlCierre = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnContinuar = new JButton("✔ Continuar");
        JButton btnCancelar = new JButton("✖ Cancelar");

        btnContinuar.setEnabled(false);
        btnContinuar.addActionListener(e -> {
            archivoCreadoExitosamente = true;
            dispose();
        });

        btnCancelar.addActionListener(e -> {
            archivoCreadoExitosamente = false;
            dispose();
        });

        pnlCierre.add(btnContinuar);
        pnlCierre.add(btnCancelar);
        add(pnlCierre, BorderLayout.SOUTH);

        // Guardar referencia al botón continuar para habilitarlo después
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
            GestorArchivos.setRutaBase(rutaSeleccionada);
            txtResultado.append("\n✅ Ruta actualizada: " + rutaSeleccionada);
        }
    }

    private void crearArchivo() {
        String nombreArchivo = txtNombreArchivo.getText().trim();

        if (nombreArchivo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa un nombre para el archivo.",
                    "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String rutaBase = txtRutaBase.getText().trim();
            GestorArchivos.setRutaBase(rutaBase);

            // Crear estructura de carpetas
            boolean carpetasCreadas = GestorArchivos.crearEstructuraCarpetas();

            // Construir ruta completa
            String rutaCompleta = rutaBase + File.separator + nombreArchivo;

            // Crear archivo si no existe
            File archivo = new File(rutaCompleta);
            if (!archivo.exists()) {
                archivo.createNewFile();
                txtResultado.setText("✅ ARCHIVO CREADO EXITOSAMENTE\n\n" +
                        "Nombre: " + nombreArchivo + "\n" +
                        "Ruta: " + rutaCompleta + "\n" +
                        "Tamaño: " + archivo.length() + " bytes\n" +
                        "Espacio disponible: " + GestorArchivos.getEspacioDisponibleMB() + " MB\n\n" +
                        "✔ Ahora puedes continuar con la gestión.");
            } else {
                txtResultado.setText("ℹ️  ARCHIVO EXISTENTE\n\n" +
                        "Nombre: " + nombreArchivo + "\n" +
                        "Ruta: " + rutaCompleta + "\n" +
                        "Tamaño: " + archivo.length() + " bytes\n\n" +
                        "✔ Se usará el archivo existente.");
            }

            rutaArchivo = rutaCompleta;
            GestorArchivos.registrarMovimiento("CREAR_ARCHIVO", nombreArchivo + " en " + rutaBase);

            // Habilitar botón Continuar
            Component[] componentes = getContentPane().getComponents();
            for (Component comp : componentes) {
                if (comp instanceof JPanel) {
                    Component[] subComponentes = ((JPanel) comp).getComponents();
                    for (Component subComp : subComponentes) {
                        if (subComp instanceof JButton) {
                            JButton btn = (JButton) subComp;
                            if ("Continuar".equals(btn.getText())) {
                                btn.setEnabled(true);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            txtResultado.setText("❌ ERROR AL CREAR ARCHIVO\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Verifica permisos en la carpeta.");
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "Error de Creación", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean estaConfigurado() {
        return archivoCreadoExitosamente;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
