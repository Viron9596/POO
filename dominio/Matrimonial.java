/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Matrimonial extends Habitacion {
    private boolean incluyeJacuzzi;

    public Matrimonial(String numeroHabitacion, String descripcion, BigDecimal precioPorNoche,
                       EstadoHabitacion estado, int capacidadMaxima, int piso, ArrayList<String> caracteristicas,
                       boolean incluyeJacuzzi) {
        super(numeroHabitacion, descripcion, precioPorNoche, estado, capacidadMaxima, piso, caracteristicas);
        this.incluyeJacuzzi = incluyeJacuzzi;
    }

    @Override
    public BigDecimal calcularCosto(int noches) {
        BigDecimal costoBase = this.getPrecioPorNoche().multiply(new BigDecimal(noches));

        // Aplicación del atributo específico: incremento porcentual por lujo de Jacuzzi
        if (this.incluyeJacuzzi) {
            BigDecimal porcentajePremium = new BigDecimal("0.15"); // 15% de recargo
            BigDecimal recargoJacuzzi = costoBase.multiply(porcentajePremium);

            costoBase = costoBase.add(recargoJacuzzi);
        }

        return costoBase;
    }

    public boolean isIncluyeJacuzzi() { return incluyeJacuzzi; }
    public void setIncluyeJacuzzi(boolean incluyeJacuzzi) { this.incluyeJacuzzi = incluyeJacuzzi; }
}