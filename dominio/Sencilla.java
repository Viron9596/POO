/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Sencilla extends Habitacion {
    private boolean incluyeEscritorio;

    public Sencilla(String numeroHabitacion, String descripcion, BigDecimal precioPorNoche,
                    EstadoHabitacion estado, int capacidadMaxima, int piso, ArrayList<String> caracteristicas,
                    boolean incluyeEscritorio) {
        super(numeroHabitacion, descripcion, precioPorNoche, estado, capacidadMaxima, piso, caracteristicas);
        this.incluyeEscritorio = incluyeEscritorio;
    }

    @Override
    public BigDecimal calcularCosto(int noches) {
        // Cálculo base: precio por noche * cantidad de noches
        BigDecimal costoTotal = this.getPrecioPorNoche().multiply(new BigDecimal(noches));

        // Aplicación del atributo específico
        if (this.incluyeEscritorio) {
            BigDecimal cargoEscritorio = new BigDecimal("10.00"); // Tarifa fija por comodidad de oficina
            costoTotal = costoTotal.add(cargoEscritorio);
        }

        return costoTotal;
    }

    public boolean isIncluyeEscritorio() { return incluyeEscritorio; }
    public void setIncluyeEscritorio(boolean incluyeEscritorio) { this.incluyeEscritorio = incluyeEscritorio; }
}