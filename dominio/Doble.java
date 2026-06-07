package dominio;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Doble extends Habitacion {
    private int cantidadCamas;

    public Doble(String numeroHabitacion, String descripcion, BigDecimal precioPorNoche,
                 EstadoHabitacion estado, int capacidadMaxima, int piso, ArrayList<String> caracteristicas,
                 int cantidadCamas) {
        super(numeroHabitacion, descripcion, precioPorNoche, estado, capacidadMaxima, piso, caracteristicas);
        this.cantidadCamas = cantidadCamas;
    }

    @Override
    public BigDecimal calcularCosto(int noches) {
        BigDecimal costoTotal = this.getPrecioPorNoche().multiply(new BigDecimal(noches));

        // Aplicación del atributo específico: cargo extra por camas adicionales por noche
        if (this.cantidadCamas > 1) {
            BigDecimal costoCamaExtra = new BigDecimal("5.00");
            BigDecimal camasAdicionales = new BigDecimal(this.cantidadCamas - 1);
            BigDecimal recargoCamas = costoCamaExtra.multiply(camasAdicionales).multiply(new BigDecimal(noches));

            costoTotal = costoTotal.add(recargoCamas);
        }

        return costoTotal;
    }

    public int getCantidadCamas() { return cantidadCamas; }
    public void setCantidadCamas(int cantidadCamas) { this.cantidadCamas = cantidadCamas; }
}