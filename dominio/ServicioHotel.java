/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import java.math.BigDecimal;
import java.io.Serializable;

public class ServicioHotel implements Serializable {
    private BigDecimal idServicio;
    private String nombre;
    private String descripcion;
    private BigDecimal costo;
    private boolean activo;

    public ServicioHotel(BigDecimal idServicio, String nombre, String descripcion, BigDecimal costo, boolean activo) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.activo = activo;
    }

    public void actualizarCosto(BigDecimal nuevoCosto) {
        this.costo = nuevoCosto;
    }

    public String obtenerInformacion() {
        return "Servicio: " + nombre + " | Costo: $" + costo + " | Activo: " + (activo ? "Sí" : "No");
    }

    // Getters y Setters
    public BigDecimal getIdServicio() { return idServicio; }
    public void setIdServicio(BigDecimal idServicio) { this.idServicio = idServicio; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}