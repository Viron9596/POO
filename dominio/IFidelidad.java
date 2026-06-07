/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dominio;

import java.math.BigDecimal;

public interface IFidelidad {
    // ANTES: double calcularDescuento(double subtotal);
    BigDecimal calcularDescuento(BigDecimal subtotal);
    String obtenerTipoFidelidad();
}