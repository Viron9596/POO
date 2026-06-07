package persistencia;

/**
 * Clase abstracta base que encapsula la configuración técnica del medio de almacenamiento.
 */
public abstract class DAOBase {
    protected String rutaArchivo;                       // [cite: 364]
    protected MetodoPersistencia metodoPersistencia;   // Relación de Asociación/Dependencia [cite: 12, 365]

    public DAOBase(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        this.rutaArchivo = rutaArchivo;
        this.metodoPersistencia = metodoPersistencia;
    }

    // Getters y Setters para control de configuración
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public MetodoPersistencia getMetodoPersistencia() { return metodoPersistencia; }
    public void setMetodoPersistencia(MetodoPersistencia metodoPersistencia) { this.metodoPersistencia = metodoPersistencia; }
}