package dominio;

import java.util.ArrayList;
import java.util.List;
import persistencia.ClienteDAO;
import persistencia.HabitacionDAO;
import persistencia.ReservaDAO;
import persistencia.ServicioDAO;
import persistencia.MetodoPersistencia;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    // Persistencia configurable
    private MetodoPersistencia metodoPersistencia = MetodoPersistencia.SERIALIZACION;
    private String carpetaDatos = ""; // opción para prefijo de ruta si se desea

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
     * Construye el nombre de archivo apropiado según la estrategia de persistencia.
     */
    private String archivoPara(String base) {
        String ext;
        switch (this.metodoPersistencia) {
            case ARCHIVO_BINARIO -> ext = ".bin";
            default -> ext = ".dat"; // SERIALIZACION
        }
        return (this.carpetaDatos == null || this.carpetaDatos.isEmpty()) ? base + ext : this.carpetaDatos + base + ext;
    }

    /**
     * Sincroniza dinámicamente los ArrayList de la clase con el estado actual
     * de los DAOs y el almacenamiento físico en disco.
     */
    private void refrescarDesdePersistencia() {
        ClienteDAO clienteDAO = new ClienteDAO(archivoPara("clientes"), this.metodoPersistencia);
        HabitacionDAO habitacionDAO = new HabitacionDAO(archivoPara("habitaciones"), this.metodoPersistencia);
        ReservaDAO reservaDAO = new ReservaDAO(archivoPara("reservas"), this.metodoPersistencia);
        ServicioDAO servicioDAO = new ServicioDAO(archivoPara("servicios"), this.metodoPersistencia);

        this.clientes = new ArrayList<>(clienteDAO.listarTodo());
        this.habitaciones = new ArrayList<>(habitacionDAO.listarTodo());
        this.reservas = new ArrayList<>(reservaDAO.listarTodo());
        this.servicios = new ArrayList<>(servicioDAO.listarTodo());
    }

    // Permite cambiar la estrategia en tiempo de ejecución y recargar desde disco
    public void setMetodoPersistencia(MetodoPersistencia metodo) {
        if (metodo == null) return;
        this.metodoPersistencia = metodo;
        refrescarDesdePersistencia();
    }

    public MetodoPersistencia getMetodoPersistencia() { return this.metodoPersistencia; }

    public void setCarpetaDatos(String carpeta) {
        this.carpetaDatos = carpeta == null ? "" : carpeta;
    }

    // --- MÉTODOS DE REGISTRO Y PERSISTENCIA ---

    public void registrarCliente(Cliente cliente) { 
        refrescarDesdePersistencia();
        if (cliente != null && buscarCliente(cliente.obtenerIdentificacion()) == null) {
            this.clientes.add(cliente);
            ClienteDAO dao = new ClienteDAO(archivoPara("clientes"), this.metodoPersistencia);
            dao.guardar(cliente);
        }
    }

    public void registrarHabitacion(Habitacion habitacion) {
        refrescarDesdePersistencia();
        if (habitacion != null && buscarHabitacion(habitacion.getNumeroHabitacion()) == null) {
            this.habitaciones.add(habitacion);
            HabitacionDAO dao = new HabitacionDAO(archivoPara("habitaciones"), this.metodoPersistencia);
            dao.guardar(habitacion);
        }
    }

    public void registrarReserva(Reserva reserva) {
        refrescarDesdePersistencia();
        if (reserva != null && buscarReserva(reserva.getIdReserva()) == null) {
            this.reservas.add(reserva);
            ReservaDAO dao = new ReservaDAO(archivoPara("reservas"), this.metodoPersistencia);
            dao.guardar(reserva);
        }
    }

    public void registrarServicio(ServicioHotel servicio) {
        refrescarDesdePersistencia();
        if (servicio != null && buscarServicio(servicio.getIdServicio()) == null) {
            this.servicios.add(servicio);
            ServicioDAO dao = new ServicioDAO(archivoPara("servicios"), this.metodoPersistencia);
            dao.guardar(servicio);
        }
    }

    // --- NUEVAS OPERACIONES DE GESTIÓN (para mantener consistencia entre UI y persistencia) ---

    public boolean modificarCliente(Cliente cliente) {
        if (cliente == null) return false;
        ClienteDAO dao = new ClienteDAO(archivoPara("clientes"), this.metodoPersistencia);
        dao.guardar(cliente); // guardar() actualiza o inserta
        refrescarDesdePersistencia();
        return true;
    }

    public boolean eliminarCliente(String id) {
        ClienteDAO dao = new ClienteDAO(archivoPara("clientes"), this.metodoPersistencia);
        boolean res = dao.eliminar(id);
        refrescarDesdePersistencia();
        return res;
    }

    public boolean eliminarHabitacion(String numero) {
        HabitacionDAO dao = new HabitacionDAO(archivoPara("habitaciones"), this.metodoPersistencia);
        boolean res = dao.eliminar(numero);
        refrescarDesdePersistencia();
        return res;
    }

    public boolean actualizarPrecioHabitacion(String numero, BigDecimal nuevoPrecio) {
        Habitacion h = buscarHabitacion(numero);
        if (h == null) return false;
        h.setPrecioPorNoche(nuevoPrecio);
        HabitacionDAO dao = new HabitacionDAO(archivoPara("habitaciones"), this.metodoPersistencia);
        dao.guardar(h);
        refrescarDesdePersistencia();
        return true;
    }

    public boolean eliminarReserva(String id) {
        ReservaDAO dao = new ReservaDAO(archivoPara("reservas"), this.metodoPersistencia);
        boolean res = dao.eliminar(id);
        refrescarDesdePersistencia();
        return res;
    }

    public boolean cancelarReserva(String id) {
        Reserva r = buscarReserva(id);
        if (r == null) return false;
        r.cancelarReserva();
        ReservaDAO dao = new ReservaDAO(archivoPara("reservas"), this.metodoPersistencia);
        dao.guardar(r);
        refrescarDesdePersistencia();
        return true;
    }

    public boolean agregarHabitacionAReserva(String idReserva, String numeroHabitacion) {
        Reserva r = buscarReserva(idReserva);
        Habitacion h = buscarHabitacion(numeroHabitacion);
        if (r == null || h == null) return false;
        r.asignarHabitacion(h);
        ReservaDAO dao = new ReservaDAO(archivoPara("reservas"), this.metodoPersistencia);
        dao.guardar(r);
        HabitacionDAO hdao = new HabitacionDAO(archivoPara("habitaciones"), this.metodoPersistencia);
        hdao.guardar(h);
        refrescarDesdePersistencia();
        return true;
    }

    public boolean agregarServicioAReserva(String idReserva, String idServicioStr) {
        Reserva r = buscarReserva(idReserva);
        ServicioDAO sdao = new ServicioDAO(archivoPara("servicios"), this.metodoPersistencia);
        ServicioHotel s = sdao.buscarPorId(idServicioStr);
        if (r == null || s == null) return false;
        ConsumoServicio cs = new ConsumoServicio("CON-" + (System.currentTimeMillis() % 1000), LocalDate.now(), 1, "Consumo desde UI", s);
        r.agregarServicio(cs);
        ReservaDAO rdao = new ReservaDAO(archivoPara("reservas"), this.metodoPersistencia);
        rdao.guardar(r);
        refrescarDesdePersistencia();
        return true;
    }

    public boolean actualizarCostoServicio(String idServicioStr, BigDecimal nuevoCosto) {
        ServicioDAO sdao = new ServicioDAO(archivoPara("servicios"), this.metodoPersistencia);
        ServicioHotel s = sdao.buscarPorId(idServicioStr);
        if (s == null) return false;
        s.setCosto(nuevoCosto);
        sdao.guardar(s);
        refrescarDesdePersistencia();
        return true;
    }

    public boolean eliminarServicio(String idServicioStr) {
        ServicioDAO sdao = new ServicioDAO(archivoPara("servicios"), this.metodoPersistencia);
        boolean res = sdao.eliminar(idServicioStr);
        refrescarDesdePersistencia();
        return res;
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
