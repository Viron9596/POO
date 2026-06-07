package dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Iterator;

public class Reserva implements Serializable {
    private String idReserva;
    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int numeroNoches;
    private EstadoReserva estado;
    private BigDecimal subtotalHabitaciones;
    private BigDecimal subtotalServicios;
    private BigDecimal descuentoAplicado;
    private BigDecimal costoFinal;

    // Estructuras para soportar Composición/Agregación del UML
    private Cliente cliente;
    private ArrayList<Habitacion> habitaciones;
    private ArrayList<ConsumoServicio> consumosServicios;

    public Reserva(String idReserva, LocalDate fechaReserva, LocalDate fechaInicio, LocalDate fechaFin, int numeroNoches, Cliente cliente) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroNoches = numeroNoches;
        this.cliente = cliente;
        this.estado = EstadoReserva.PENDIENTE;
        this.habitaciones = new ArrayList<>();
        this.consumosServicios = new ArrayList<>();
        this.subtotalHabitaciones = BigDecimal.ZERO;
        this.subtotalServicios = BigDecimal.ZERO;
        this.descuentoAplicado = BigDecimal.ZERO;
        this.costoFinal = BigDecimal.ZERO;
    }

    public boolean validarSolapamiento(LocalDate inicio, LocalDate fin) {
        // Valida si el rango solicitado intersecta con las fechas de esta reserva
        return !(fin.isBefore(this.fechaInicio) || inicio.isAfter(this.fechaFin));
    }

    public void asignarHabitacion(Habitacion habitacion) {
        if (habitacion != null) {
            this.habitaciones.add(habitacion);
            habitacion.reservar();
            calcularCostoFinal();
        }
    }

    public void agregarServicio(ConsumoServicio HallConsumo) {
        if (HallConsumo != null) {
            this.consumosServicios.add(HallConsumo);
            calcularCostoFinal();
        }
    }

    public boolean removerHabitacion(String numeroHabitacion) {
        Iterator<Habitacion> it = habitaciones.iterator();
        while (it.hasNext()) {
            Habitacion hab = it.next();
            if (hab.getNumeroHabitacion().equals(numeroHabitacion)) {
                hab.cambiarEstado(EstadoHabitacion.DISPONIBLE);
                it.remove(); // seguro al usar iterator
                calcularCostoFinal();
                return true;
            }
        }
        return false;
    }

    public void cancelarReserva() {
        this.estado = EstadoReserva.CANCELADA;
        for (Habitacion hab : habitaciones) {
            hab.cambiarEstado(EstadoHabitacion.DISPONIBLE);
        }
    }

    public void confirmarReserva() {
        this.estado = EstadoReserva.CONFIRMADA;
        for (Habitacion hab : habitaciones) {
            hab.cambiarEstado(EstadoHabitacion.RESERVADA);
        }
    }

    public void finalizarReserva() {
        this.estado = EstadoReserva.FINALIZADA;
        for (Habitacion hab : habitaciones) {
            hab.cambiarEstado(EstadoHabitacion.DISPONIBLE);
        }
    }

    public BigDecimal calcularSubtotalHabitaciones() {
        BigDecimal suma = BigDecimal.ZERO;
        for (Habitacion hab : habitaciones) {
            suma = suma.add(hab.calcularCosto(this.numeroNoches));
        }
        this.subtotalHabitaciones = suma;
        return this.subtotalHabitaciones;
    }

    public BigDecimal calcularSubtotalServicios() {
        BigDecimal suma = BigDecimal.ZERO;
        for (ConsumoServicio cs : consumosServicios) {
            // Aseguramos que subtotal esté actualizado
            suma = suma.add(cs.getSubtotal() != null ? cs.getSubtotal() : BigDecimal.ZERO);
        }
        this.subtotalServicios = suma;
        return this.subtotalServicios;
    }

    public void calcularCostoFinal() {
        // Calcular subtotales actualizados
        BigDecimal subtotalHab = BigDecimal.ZERO;
        for (Habitacion hab : habitaciones) {
            subtotalHab = subtotalHab.add(hab.calcularCosto(numeroNoches));
        }
        this.subtotalHabitaciones = subtotalHab;

        // Calcular subtotal de servicios
        this.subtotalServicios = calcularSubtotalServicios();

        // Descuento: aplicar sobre el total (habitaciones + servicios)
        BigDecimal subtotalGeneral = this.subtotalHabitaciones.add(this.subtotalServicios);

        this.descuentoAplicado = (this.cliente != null) ? this.cliente.calcularDescuento(subtotalGeneral) : BigDecimal.ZERO;

        this.costoFinal = subtotalGeneral.subtract(this.descuentoAplicado);
    }

    public Factura generarFactura() {
        calcularCostoFinal();

        String idFact = "FAC-" + this.idReserva;
        LocalDate emision = LocalDate.now();
        String nomCli = (this.cliente != null) ? this.cliente.obtenerNombreCompleto() : "Anonimo";
        String idenCli = (this.cliente != null) ? this.cliente.obtenerIdentificacion() : "N/A";

        ArrayList<String> detHab = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            detHab.add("Habitacion " + h.getNumeroHabitacion() + " (" + h.getClass().getSimpleName() + ") x" + numeroNoches + " noches");
        }

        ArrayList<String> detServ = new ArrayList<>();
        for (ConsumoServicio cs : consumosServicios) {
            detServ.add(cs.getServicio().getNombre() + " x" + cs.getCantidad() + " ($" + cs.getSubtotal() + ")");
        }

        BigDecimal sub = this.subtotalHabitaciones.add(this.subtotalServicios);
        // Generamos un impuesto informativo estándar del 19% (IVA) aplicable sobre el neto
        BigDecimal imp = sub.subtract(this.descuentoAplicado)
                    .multiply(new BigDecimal("0.19"))
                    .setScale(2, RoundingMode.HALF_UP);
        BigDecimal tot = sub.subtract(this.descuentoAplicado).add(imp);

        return new Factura(idFact, emision, nomCli, idenCli, detHab, detServ, sub, this.descuentoAplicado, imp, tot);
    }

    // Getters y Setters
    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public int getNumeroNoches() { return numeroNoches; }
    public void setNumeroNoches(int numeroNoches) { this.numeroNoches = numeroNoches; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public BigDecimal getSubtotalHabitaciones() { return subtotalHabitaciones; }
    public BigDecimal getSubtotalServicios() { return subtotalServicios; }
    public BigDecimal getDescuentoAplicado() { return descuentoAplicado; }
    public BigDecimal getCostoFinal() { return costoFinal; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public ArrayList<Habitacion> getHabitaciones() { return habitaciones; }
    public ArrayList<ConsumoServicio> getConsumosServicios() { return consumosServicios; }
}
