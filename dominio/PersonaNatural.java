/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;
import java.time.LocalDate;

public class PersonaNatural extends Cliente {
    private String nombre;
    private String apellido;
    private String cedula;
    private LocalDate fechaNacimiento;

    public PersonaNatural(String idCliente, String telefono, String correo, String direccion, 
                          LocalDate fechaRegistro, boolean activo, IFidelidad fidelidad, 
                          String nombre, String apellido, String cedula, LocalDate fechaNacimiento) {
        super(idCliente, telefono, correo, direccion, fechaRegistro, activo, fidelidad);
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public String obtenerNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    @Override
    public String obtenerIdentificacion() {
        return this.cedula;
    }

    // Getters y Setters específicos
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}