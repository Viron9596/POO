package persistencia;

import dominio.Reserva;
import dominio.ConsumoServicio;
import dominio.Habitacion;
import dominio.ServicioHotel;
import dominio.Cliente;
import dominio.PersonaNatural;
import dominio.ClienteEsporadico;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.math.BigDecimal;

public class ReservaDAO extends DAOBase implements IDAO<Reserva> {

    private List<Reserva> memoriaReservas = new ArrayList<>();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public ReservaDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Reserva reserva) {
        if (reserva == null) return;
        eliminar(reserva.getIdReserva());
        memoriaReservas.add(reserva);
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaReservas.removeIf(r -> r.getIdReserva().equals(id));
        if (eliminado) actualizarArchivoFisico();
        return eliminado;
    }

    @Override
    public Reserva buscarPorId(String id) {
        for (Reserva r : memoriaReservas) {
            if (r.getIdReserva().equals(id)) return r;
        }
        return null;
    }

    @Override
    public List<Reserva> listarTodo() { return new ArrayList<>(memoriaReservas); }

    private void guardarPorSerializacion() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            oos.writeObject(memoriaReservas);
            System.out.println("[DAO] Reservas resguardadas físicamente en: '" + this.rutaArchivo + "'");
        } catch (IOException e) { System.err.println("[ERROR DAO] Error al serializar reservas: " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        if (!archivo.exists()) return;
        try {
            switch (this.metodoPersistencia) {
                case SERIALIZACION -> {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        memoriaReservas = (List<Reserva>) ois.readObject();
                        System.out.println("[DAO] " + memoriaReservas.size() + " reservas activas cargadas desde disco.");
                    }
                }
                case ARCHIVO_BINARIO -> cargarDesdeBinario();
            }
        } catch (IOException | ClassNotFoundException e) { System.err.println("[ERROR DAO] Error al cargar histórico de reservas: " + e.getMessage()); }
    }

    private void actualizarArchivoFisico() {
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    // ARCHIVO_BINARIO
    private void guardarEnBinario() {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.rutaArchivo)))) {
            dos.writeInt(memoriaReservas.size());
            for (Reserva r : memoriaReservas) {
                dos.writeUTF(r.getIdReserva() == null ? "" : r.getIdReserva());
                dos.writeUTF(r.getFechaReserva() == null ? "" : r.getFechaReserva().format(DATE_FMT));
                dos.writeUTF(r.getFechaInicio() == null ? "" : r.getFechaInicio().format(DATE_FMT));
                dos.writeUTF(r.getFechaFin() == null ? "" : r.getFechaFin().format(DATE_FMT));
                dos.writeInt(r.getNumeroNoches());
                dos.writeUTF(r.getCliente() == null ? "" : r.getCliente().obtenerIdentificacion());
                List<Habitacion> habs = r.getHabitaciones();
                dos.writeInt(habs == null ? 0 : habs.size());
                if (habs != null) {
                    for (Habitacion h : habs) {
                        dos.writeUTF(h == null ? "" : h.getNumeroHabitacion());
                    }
                }
                List<ConsumoServicio> consumos = r.getConsumosServicios();
                dos.writeInt(consumos == null ? 0 : consumos.size());
                if (consumos != null) {
                    for (ConsumoServicio cs : consumos) {
                        dos.writeUTF(cs.getIdConsumo() == null ? "" : cs.getIdConsumo());
                        dos.writeUTF(cs.getFechaConsumo() == null ? "" : cs.getFechaConsumo().format(DATE_FMT));
                        dos.writeInt(cs.getCantidad());
                        dos.writeUTF(cs.getObservaciones() == null ? "" : cs.getObservaciones());
                        dos.writeUTF(cs.getServicio() == null || cs.getServicio().getIdServicio() == null ? "" : cs.getServicio().getIdServicio().toPlainString());
                        dos.writeUTF(cs.getSubtotal() == null ? "0" : cs.getSubtotal().toPlainString());
                    }
                }
                dos.writeUTF(r.getEstado() == null ? "PENDIENTE" : r.getEstado().name());
            }
            dos.flush();
            System.out.println("[DAO] Reservas guardadas en binario: '" + this.rutaArchivo + "'");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al escribir reservas binario: " + e.getMessage());
        }
    }

    private void cargarDesdeBinario() {
        File f = new File(this.rutaArchivo);
        if (!f.exists()) return;
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            int n = dis.readInt();
            memoriaReservas = new ArrayList<>();
            File ruta = new File(this.rutaArchivo);
            String base = (ruta.getParent() == null) ? "" : ruta.getParent() + File.separator;
            ClienteDAO clienteDAO = new ClienteDAO(base + "clientes.dat", this.metodoPersistencia);
            HabitacionDAO habitacionDAO = new HabitacionDAO(base + "habitaciones.dat", this.metodoPersistencia);
            ServicioDAO servicioDAO = new ServicioDAO(base + "servicios.dat", this.metodoPersistencia);
            for (int i = 0; i < n; i++) {
                String idRes = dis.readUTF();
                String fechaResStr = dis.readUTF();
                String fechaInicioStr = dis.readUTF();
                String fechaFinStr = dis.readUTF();
                int noches = dis.readInt();
                String idCliente = dis.readUTF();
                LocalDate fechaRes = (fechaResStr == null || fechaResStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(fechaResStr, DATE_FMT);
                LocalDate fechaInicio = (fechaInicioStr == null || fechaInicioStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(fechaInicioStr, DATE_FMT);
                LocalDate fechaFin = (fechaFinStr == null || fechaFinStr.isEmpty()) ? fechaInicio : LocalDate.parse(fechaFinStr, DATE_FMT);
                Cliente cli = (idCliente == null || idCliente.isEmpty()) ? null : clienteDAO.buscarPorId(idCliente);
                Reserva r = new Reserva(idRes, fechaRes, fechaInicio, fechaFin, noches, cli);
                int nHabs = dis.readInt();
                for (int h = 0; h < nHabs; h++) {
                    String numHab = dis.readUTF();
                    Habitacion habit = habitacionDAO.buscarPorId(numHab);
                    if (habit != null) r.asignarHabitacion(habit);
                    else System.err.println("[DAO RESERVA] Habitación referenciada no encontrada: " + numHab);
                }
                int nCons = dis.readInt();
                for (int c = 0; c < nCons; c++) {
                    String idCons = dis.readUTF();
                    String fechaConsStr = dis.readUTF();
                    int cantidad = dis.readInt();
                    String obs = dis.readUTF();
                    String idServStr = dis.readUTF();
                    String subtotalStr = dis.readUTF();
                    LocalDate fechaCons = (fechaConsStr == null || fechaConsStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(fechaConsStr, DATE_FMT);
                    ServicioHotel serv = (idServStr == null || idServStr.isEmpty()) ? null : servicioDAO.buscarPorId(idServStr);
                    ConsumoServicio cs = new ConsumoServicio(idCons, fechaCons, cantidad, obs, serv);
                    if (subtotalStr != null && !subtotalStr.isEmpty()) {
                        try { cs.setSubtotal(new BigDecimal(subtotalStr)); } catch (NumberFormatException ex) {}
                    }
                    r.agregarServicio(cs);
                }
                String estadoStr = dis.readUTF();
                try { r.setEstado(dominio.EstadoReserva.valueOf(estadoStr)); } catch (Exception ex) { /* ignore */ }
                memoriaReservas.add(r);
            }
            System.out.println("[DAO] " + memoriaReservas.size() + " reservas cargadas desde binario.");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al leer reservas binario: " + e.getMessage());
        }
    }
}
