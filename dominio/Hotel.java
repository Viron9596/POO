package dominio;

import java.util.ArrayList;
import java.util.List;
import persistencia.ClienteDAO;
import persistencia.HabitacionDAO;
import persistencia.ReservaDAO;
import persistencia.ServicioDAO;
import persistencia.MetodoPersistencia;

/**
 * Clase que actúa como controlador y orquestador central del sistema del hotel.
 * Concentra los inventarios, registros y las reglas de negocio globales,
 * garantizando sincronización en tiempo real con la capa de persistencia.
 */
public class Hotel {
    // Atributos definidos en el diagrama UML (Todos Privados)
    private String idHotel; 
    private String nombre; 
    private String direccion; 
    private String telefono; 
    private ArrayList<Habitacion> habitaciones; 
    private ArrayList<Cliente> clientes; 
    private ArrayList<Reserva> reservas; 
    private ArrayList<ServicioHotel> servicios; 

    public Hotel(String idHotel, String nombre, String direccion, String telefono) {
        this.idHotel = idHotel; 
        this.nombre = nombre; 
        this.direccion = direccion; 
        this.telefono = telefono; 
        this.habitaciones = new ArrayList<>(); 
        this.clientes = new ArrayList<>(); 
        this.reservas = new ArrayList<>(); 
        this.servicios = new ArrayList<>(); 
        refrescarDesdePersistencia();
    }

    /**
     * Sincroniza dinámicamente los ArrayList de la clase con el estado actual
     * de los DAOs estáticos y el almacenamiento físico en disco.
     */
    private void refrescarDesdePersistencia() {
        ClienteDAO clienteDAO = new ClienteDAO("clientes.dat", MetodoPersistencia.SERIALIZACION);
        HabitacionDAO habitacionDAO = new HabitacionDAO("habitaciones.dat", MetodoPersistencia.SERIALIZACION);
        ReservaDAO reservaDAO = new ReservaDAO("reservas.dat", MetodoPersistencia.SERIALIZACION);
        ServicioDAO servicioDAO = new ServicioDAO("servicios.dat", MetodoPersistencia.SERIALIZACION);

        this.clientes = new ArrayList<>(clienteDAO.listarTodo());
        this.habitaciones = new ArrayList<>(habitacionDAO.listarTodo());
        this.reservas = new ArrayList<>(reservaDAO.listarTodo());
        this.servicios = new ArrayList<>(servicioDAO.listarTodo());
    }

    // --- MÉTODOS DE REGISTRO Y PERSISTENCIA ---

    public void registrarCliente(Cliente cliente) { 
        refrescarDesdePersistencia();
        if (cliente != null && buscarCliente(cliente.obtenerIdentificacion()) == null) {
            this.clientes.add(cliente);
            ClienteDAO dao = new ClienteDAO("clientes.dat", MetodoPersistencia.SERIALIZACION);
            dao.guardar(cliente);
        }
    }

    public void registrarHabitacion(Habitacion habitacion) {
        refrescarDesdePersistencia();
        if (habitacion != null && buscarHabitacion(habitacion.getNumeroHabitacion()) == null) {
            this.habitaciones.add(habitacion);
            HabitacionDAO dao = new HabitacionDAO("habitaciones.dat", MetodoPersistencia.SERIALIZACION);
            dao.guardar(habitacion);
        }
    }

    public void registrarReserva(Reserva reserva) {
        refrescarDesdePersistencia();
        if (reserva != null && buscarReserva(reserva.getIdReserva()) == null) {
            this.reservas.add(reserva);
            ReservaDAO dao = new ReservaDAO("reservas.dat", MetodoPersistencia.SERIALIZACION);
            dao.guardar(reserva);
        }
    }

    public void registrarServicio(ServicioHotel servicio) {
        refrescarDesdePersistencia();
        if (servicio != null && buscarServicio(servicio.getIdServicio()) == null) {
            this.servicios.add(servicio);
            ServicioDAO dao = new ServicioDAO("servicios.dat", MetodoPersistencia.SERIALIZACION);
            dao.guardar(servicio);
        }
    }

    // --- MÉTODOS DE BÚSQUEDA INTERNA ---

    public Cliente buscarCliente(String id) {
        refrescarDesdePersistencia();
        for (Cliente c : clientes) {
            if (c.obtenerIdentificacion().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public Habitacion buscarHabitacion(String numero) {
        refrescarDesdePersistencia();
        for (Habitacion h : habitaciones) {
            if (h.getNumeroHabitacion().equals(numero)) {
                return h;
            }
        }
        return null;
    }

    public Reserva buscarReserva(String id) {
        refrescarDesdePersistencia();
        for (Reserva r : reservas) {
            if (r.getIdReserva().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public ServicioHotel buscarServicio(java.math.BigDecimal id) {
        refrescarDesdePersistencia();
        for (ServicioHotel s : servicios) {
            if (s.getIdServicio().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // --- LÓGICA DE NEGOCIO GLOBAL ---

    public List<Habitacion> consultarHabitacionesDisponibles(String tipo) {
        refrescarDesdePersistencia();
        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            if (h.consultarDisponibilidad()) {
                if (tipo == null || tipo.equalsIgnoreCase("TODAS") || h.getClass().getSimpleName().equalsIgnoreCase(tipo)) {
                    disponibles.add(h);
                }
            }
        }
        return disponibles;
    }

    public List<Cliente> listarClientesHabituales() { 
        refrescarDesdePersistencia();
        List<Cliente> habituales = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.esClienteHabitual()) {
                habituales.add(c);
            }
        }
        return habituales;
    }

    // --- GENERACIÓN DE TRANSACCIONES ---

    public Factura generarFacturaReserva(String idReserva) { 
        refrescarDesdePersistencia();
        Reserva res = buscarReserva(idReserva);
        if (res != null) {
            return res.generarFactura();
        }
        return null;
    }

    // --- GETTERS Y SETTERS ---

    public String getIdHotel() { return idHotel; }
    public void setIdHotel(String idHotel) { this.idHotel = idHotel; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public ArrayList<Habitacion> getHabitaciones() { 
        refrescarDesdePersistencia();
        return habitaciones; 
    }
    
    public ArrayList<Cliente> getClientes() { 
        refrescarDesdePersistencia();
        return clientes; 
    }
    
    public ArrayList<Reserva> getReservas() { 
        refrescarDesdePersistencia();
        return reservas; 
    }
    
    public ArrayList<ServicioHotel> getServicios() { 
        refrescarDesdePersistencia();
        return servicios; 
    }
}