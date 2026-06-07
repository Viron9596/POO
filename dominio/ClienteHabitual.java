package dominio;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ClienteHabitual implements IFidelidad {
    private double porcentajeDescuento;
    private int puntosAcumulados;

    public ClienteHabitual(double porcentajeDescuento, int puntosAcumulados) {
        this.porcentajeDescuento = porcentajeDescuento;
        this.puntosAcumulados = puntosAcumulados;
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal subtotal) {
        // Convertimos el porcentaje a BigDecimal de forma segura
        BigDecimal porcentaje = BigDecimal.valueOf(this.porcentajeDescuento);
        BigDecimal cien = new BigDecimal("100");
        
        // Operación: (subtotal * porcentaje) / 100
        // Seteamos escala a 2 decimales y redondeo simétrico (HALF_UP)
        return subtotal.multiply(porcentaje)
                       .divide(cien, 2, RoundingMode.HALF_UP);
    }

    @Override
    public String obtenerTipoFidelidad() {
        return "Habitual";
    }

    // Getters y Setters necesarios para la gestión interna de la estrategia
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
    public int getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }
}