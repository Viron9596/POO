/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.io.Serializable;

public abstract class Habitacion implements Serializable {
    private String numeroHabitacion;
    private String descripcion;
    private BigDecimal precioPorNoche;
    private EstadoHabitacion estado;
    private int capacidadMaxima;
    private int piso;
    private ArrayList<String> caracteristicas;

    public Habitacion(String numeroHabitacion, String descripcion, BigDecimal precioPorNoche,
                      EstadoHabitacion estado, int capacidadMaxima, int piso, ArrayList<String> caracteristicas) {
        this.numeroHabitacion = numeroHabitacion;
        this.descripcion = descripcion;
        this.precioPorNoche = precioPorNoche;
        this.estado = estado;
        this.capacidadMaxima = capacidadMaxima;
        this.piso = piso;
        this.caracteristicas = caracteristicas != null ? caracteristicas : new ArrayList<>();
    }

    public boolean consultarDisponibilidad() {
        return this.estado == EstadoHabitacion.DISPONIBLE;
    }

    public void checkin() {
        if (this.estado == EstadoHabitacion.RESERVADA || this.estado == EstadoHabitacion.DISPONIBLE) {
            this.estado = EstadoHabitacion.OCUPADA;
        }
    }

    public void checkOut() {
        if (this.estado == EstadoHabitacion.OCUPADA) {
            this.estado = EstadoHabitacion.DISPONIBLE;
        }
    }

    public void marcarMantenimiento() {
        this.estado = EstadoHabitacion.MANTENIMIENTO;
    }

    public void reservar() {
        if (this.estado == EstadoHabitacion.DISPONIBLE) {
            this.estado = EstadoHabitacion.RESERVADA;
        }
    }

    public void cambiarEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    // Método abstracto obligatorio según el diagrama UML
    public abstract BigDecimal calcularCosto(int noches);

    // Getters y Setters
    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(BigDecimal precioPorNoche) { this.precioPorNoche = precioPorNoche; }
    public EstadoHabitacion getEstado() { return estado; }
    public void setEstado(EstadoHabitacion estado) { this.estado = estado; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    public int getPiso() { return piso; }
    public void setPiso(int piso) { this.piso = piso; }
    public ArrayList<String> getCaracteristicas() { return caracteristicas; }
    public void setCaracteristicas(ArrayList<String> caracteristicas) { this.caracteristicas = caracteristicas; }
}