package persistencia;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Gestor centralizado para crear y administrar las rutas de archivos binarios
 * FUERA del proyecto (en una carpeta externa configurable)
 */
public class GestorArchivos {
    
    // Ruta base donde se crearán los archivos (fuera del proyecto)
    private static String rutaBase = System.getProperty("user.home") + File.separator + "SistemaHotel_Datos";
    
    // Nombre de las subcarpetas
    private static final String CARPETA_CLIENTES = "clientes";
    private static final String CARPETA_HABITACIONES = "habitaciones";
    private static final String CARPETA_RESERVAS = "reservas";
    private static final String CARPETA_SERVICIOS = "servicios";
    private static final String CARPETA_FACTURAS = "facturas";
    private static final String CARPETA_LOGS = "logs";
    
    /**
     * Configura la ruta base para almacenamiento de datos
     * @param nuevaRuta Ruta absoluta donde se crearán los archivos
     */
    public static void setRutaBase(String nuevaRuta) {
        if (nuevaRuta != null && !nuevaRuta.trim().isEmpty()) {
            rutaBase = nuevaRuta;
        }
    }
    
    /**
     * Obtiene la ruta base configurada
     */
    public static String getRutaBase() {
        return rutaBase;
    }
    
    /**
     * Crea la estructura de carpetas necesaria
     * @return true si se creó exitosamente, false si ya existía
     */
    public static boolean crearEstructuraCarpetas() {
        try {
            File dir = new File(rutaBase);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("[GESTOR] Carpeta base creada: " + rutaBase);
            }
            
            // Crear subcarpetas
            crearCarpeta(CARPETA_CLIENTES);
            crearCarpeta(CARPETA_HABITACIONES);
            crearCarpeta(CARPETA_RESERVAS);
            crearCarpeta(CARPETA_SERVICIOS);
            crearCarpeta(CARPETA_FACTURAS);
            crearCarpeta(CARPETA_LOGS);
            
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR GESTOR] Error al crear estructura: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Crea una subcarpeta dentro de la ruta base
     */
    private static void crearCarpeta(String nombreCarpeta) {
        File carpeta = new File(rutaBase + File.separator + nombreCarpeta);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
            System.out.println("[GESTOR] Subcarpeta creada: " + carpeta.getAbsolutePath());
        }
    }
    
    /**
     * Obtiene la ruta completa para archivos de clientes
     */
    public static String getRutaClientes() {
        return rutaBase + File.separator + CARPETA_CLIENTES + File.separator + "clientes.bin";
    }
    
    /**
     * Obtiene la ruta completa para archivos de habitaciones
     */
    public static String getRutaHabitaciones() {
        return rutaBase + File.separator + CARPETA_HABITACIONES + File.separator + "habitaciones.bin";
    }
    
    /**
     * Obtiene la ruta completa para archivos de reservas
     */
    public static String getRutaReservas() {
        return rutaBase + File.separator + CARPETA_RESERVAS + File.separator + "reservas.bin";
    }
    
    /**
     * Obtiene la ruta completa para archivos de servicios
     */
    public static String getRutaServicios() {
        return rutaBase + File.separator + CARPETA_SERVICIOS + File.separator + "servicios.bin";
    }
    
    /**
     * Obtiene la ruta completa para archivos de facturas
     */
    public static String getRutaFacturas() {
        return rutaBase + File.separator + CARPETA_FACTURAS + File.separator + "facturas.bin";
    }
    
    /**
     * Obtiene la ruta completa para archivo de logs
     */
    public static String getRutaLogs() {
        return rutaBase + File.separator + CARPETA_LOGS + File.separator + "movimientos.log";
    }
    
    /**
     * Verifica si los archivos existen
     */
    public static boolean existenArchivos() {
        return new File(getRutaClientes()).exists() ||
               new File(getRutaHabitaciones()).exists() ||
               new File(getRutaReservas()).exists() ||
               new File(getRutaServicios()).exists();
    }
    
    /**
     * Obtiene el espacio disponible en la ruta base (en MB)
     */
    public static long getEspacioDisponibleMB() {
        File dir = new File(rutaBase);
        return dir.getFreeSpace() / (1024 * 1024);
    }
    
    /**
     * Registra un movimiento en el archivo de logs
     */
    public static void registrarMovimiento(String tipoOperacion, String detalles) {
        try {
            String rutaLog = getRutaLogs();
            String timestamp = java.time.LocalDateTime.now().toString();
            String linea = "[" + timestamp + "] " + tipoOperacion + " - " + detalles + "\n";
            
            Files.write(Paths.get(rutaLog), linea.getBytes(), 
                       java.nio.file.StandardOpenOption.CREATE,
                       java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.err.println("[ERROR LOG] " + e.getMessage());
        }
    }
}
