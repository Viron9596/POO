package dominio;
import java.time.LocalDate;

public class Empresa extends Cliente {
    private String razonSocial;
    private String nit;
    private String nombreContacto;
    private String cargoContacto;

    public Empresa(String idCliente, String telefono, String correo, String direccion, 
                   LocalDate fechaRegistro, boolean activo, IFidelidad fidelidad, 
                   String razonSocial, String nit, String nombreContacto, String cargoContacto) {
        super(idCliente, telefono, correo, direccion, fechaRegistro, activo, fidelidad);
        this.razonSocial = razonSocial;
        this.nit = nit;
        this.nombreContacto = nombreContacto;
        this.cargoContacto = cargoContacto;
    }

    @Override
    public String obtenerNombreCompleto() {
        return this.razonSocial;
    }

    @Override
    public String obtenerIdentificacion() {
        return this.nit;
    }

    // Getters y Setters específicos
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }
    public String getCargoContacto() { return cargoContacto; }
    public void setCargoContacto(String cargoContacto) { this.cargoContacto = cargoContacto; }
}