package dominio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;

public abstract class Cliente implements Serializable {
    private String idCliente;
    private String telefono;
    private String correo;
    private String direccion;
    private LocalDate fechaRegistro;
    private boolean activo;
    private IFidelidad fidelidad;

    public Cliente(String idCliente, String telefono, String correo, String direccion, LocalDate fechaRegistro, boolean activo, IFidelidad fidelidad) {
        this.idCliente = idCliente;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
        this.fidelidad = fidelidad;
    }

    // Métodos abstractos obligatorios por el diagrama UML
    public abstract String obtenerIdentificacion();
    public abstract String obtenerNombreCompleto();

    public void actualizarDatos() {
        // Lógica para actualizar estados o refrescar datos básicos de contacto
    }

    // Cambiar este método dentro de la clase abstracta Cliente
    public BigDecimal calcularDescuento(BigDecimal subtotal) {
        if (this.fidelidad != null) {
            return this.fidelidad.calcularDescuento(subtotal);
        }
        return BigDecimal.ZERO; // En lugar de 0.0, retornamos el objeto estático ZERO
    }

    public void asignarFidelidad(IFidelidad fidelidad) {
        this.fidelidad = fidelidad;
    }

    public boolean esClienteHabitual() {
        if (this.fidelidad != null) {
            return "Habitual".equalsIgnoreCase(this.fidelidad.obtenerTipoFidelidad());
        }
        return false;
    }

    // Métodos de encapsulamiento básicos (Getters y Setters)
    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public IFidelidad getFidelidad() { return fidelidad; }
}
