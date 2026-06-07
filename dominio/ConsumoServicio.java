package dominio;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConsumoServicio {
    private String idConsumo;
    private LocalDate fechaConsumo;
    private int cantidad;
    private String observaciones;
    private BigDecimal subtotal;
    
    // Agregación con ServicioHotel para operar la lógica de negocio interna
    private ServicioHotel servicio;

    public ConsumoServicio(String idConsumo, LocalDate fechaConsumo, int cantidad, String observaciones, ServicioHotel servicio) {
        this.idConsumo = idConsumo;
        this.fechaConsumo = fechaConsumo;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.servicio = servicio;
        this.subtotal = calcularSubtotal();
    }

    public BigDecimal calcularSubtotal() {
        if (this.servicio != null) {
            this.subtotal = this.servicio.getCosto().multiply(new BigDecimal(this.cantidad));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
        return this.subtotal;
    }

    // Getters y Setters
    public String getIdConsumo() { return idConsumo; }
    public void setIdConsumo(String idConsumo) { this.idConsumo = idConsumo; }
    public LocalDate getFechaConsumo() { return fechaConsumo; }
    public void setFechaConsumo(LocalDate fechaConsumo) { this.fechaConsumo = fechaConsumo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public ServicioHotel getServicio() { return servicio; }
    public void setServicio(ServicioHotel servicio) { this.servicio = servicio; this.calcularSubtotal(); }
}
