package dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ClienteHabitual implements IFidelidad {
    private BigDecimal porcentajeDescuento;
    private int puntosAcumulados;

    public ClienteHabitual(BigDecimal porcentajeDescuento, int puntosAcumulados) {
        this.porcentajeDescuento = porcentajeDescuento;
        this.puntosAcumulados = puntosAcumulados;
    }

    @Override
    public BigDecimal calcularDescuento(BigDecimal subtotal) {
        if (porcentajeDescuento == null) return BigDecimal.ZERO;
        BigDecimal cien = new BigDecimal("100");
        return subtotal.multiply(porcentajeDescuento)
                       .divide(cien, 2, RoundingMode.HALF_UP);
    }

    @Override
    public String obtenerTipoFidelidad() {
        return "Habitual";
    }

    // Getters y Setters necesarios para la gestión interna de la estrategia
    public BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
    public int getPuntosAcumulados() { return puntosAcumulados; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }
}
