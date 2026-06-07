/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.Serializable;

public class Factura implements Serializable {
    private String idFactura;
    private LocalDate fechaEmision;
    private String clienteNombre;
    private String clienteIdentificacion;
    private ArrayList<String> detalleHabitaciones;
    private ArrayList<String> detalleServicios;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal impuestos;
    private BigDecimal total;
    private boolean emitida;

    public Factura(String idFactura, LocalDate fechaEmision, String clienteNombre, String clienteIdentificacion,
                   ArrayList<String> detalleHabitaciones, ArrayList<String> detalleServicios,
                   BigDecimal subtotal, BigDecimal descuento, BigDecimal impuestos, BigDecimal total) {
        this.idFactura = idFactura;
        this.fechaEmision = fechaEmision;
        this.clienteNombre = clienteNombre;
        this.clienteIdentificacion = clienteIdentificacion;
        this.detalleHabitaciones = detalleHabitaciones != null ? detalleHabitaciones : new ArrayList<>();
        this.detalleServicios = detalleServicios != null ? detalleServicios : new ArrayList<>();
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.impuestos = impuestos;
        this.total = total;
        this.emitida = false;
    }

    public void emitirFactura() {
        this.emitida = true;
    }

    public String desglosarCargos() {
        StringBuilder sb = new StringBuilder();
        sb.append("Detalle Hospedaje:\n");
        for (String dh : detalleHabitaciones) {
            sb.append(" - ").append(dh).append("\n");
        }
        sb.append("Detalle Consumos Extra:\n");
        for (String ds : detalleServicios) {
            sb.append(" - ").append(ds).append("\n");
        }
        return sb.toString();
    }

    public String exportarFactura() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("          HOTEL - FACTURA INFORMATIVA     \n");
        sb.append("=========================================\n");
        sb.append("ID Factura: ").append(idFactura).append("\n");
        sb.append("Fecha: ").append(fechaEmision).append("\n");
        sb.append("Cliente: ").append(clienteNombre).append("\n");
        sb.append("ID Cliente: ").append(clienteIdentificacion).append("\n");
        sb.append("-----------------------------------------\n");
        sb.append(desglosarCargos());
        sb.append("-----------------------------------------\n");
        sb.append("Subtotal:  $").append(subtotal).append("\n");
        sb.append("Descuento: $").append(descuento).append("\n");
        sb.append("IVA (19%): $").append(impuestos).append("\n");
        sb.append("TOTAL:     $").append(total).append("\n");
        sb.append("Estado:    ").append(emitida ? "EMITIDA" : "PRE-VISUALIZACION").append("\n");
        sb.append("=========================================\n");
        return sb.toString();
    }

    // Getters y Setters
    public String getIdFactura() { return idFactura; }
    public void setIdFactura(String idFactura) { this.idFactura = idFactura; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteIdentificacion() { return clienteIdentificacion; }
    public void setClienteIdentificacion(String clienteIdentificacion) { this.clienteIdentificacion = clienteIdentificacion; }
    public ArrayList<String> getDetalleHabitaciones() { return detalleHabitaciones; }
    public void setDetalleHabitaciones(ArrayList<String> detalleHabitaciones) { this.detalleHabitaciones = detalleHabitaciones; }
    public ArrayList<String> getDetalleServicios() { return detalleServicios; }
    public void setDetalleServicios(ArrayList<String> detalleServicios) { this.detalleServicios = detalleServicios; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public boolean isEmitida() { return emitida; }
    public void setEmitida(boolean emitida) { this.emitida = emitida; }
}
