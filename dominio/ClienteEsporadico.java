package dominio;
import java.math.BigDecimal;

public class ClienteEsporadico implements IFidelidad {

    @Override
    public BigDecimal calcularDescuento(BigDecimal subtotal) {
        return BigDecimal.ZERO; // Cero descuentos sin perder precisión
    }

    @Override
    public String obtenerTipoFidelidad() {
        return "Esporadico";
    }
}