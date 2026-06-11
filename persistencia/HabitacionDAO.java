package persistencia;

import dominio.Habitacion;
import dominio.Sencilla;
import dominio.Doble;
import dominio.Matrimonial;
import dominio.EstadoHabitacion;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class HabitacionDAO extends DAOBase implements IDAO<Habitacion> {

    private List<Habitacion> memoriaHabitaciones = new ArrayList<>();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public HabitacionDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Habitacion habitacion) {
        if (habitacion == null) return;
        eliminar(habitacion.getNumeroHabitacion());
        memoriaHabitaciones.add(habitacion);
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaHabitaciones.removeIf(h -> h.getNumeroHabitacion().equals(id));
        if (eliminado) actualizarArchivoFisico();
        return eliminado;
    }

    @Override
    public Habitacion buscarPorId(String id) {
        for (Habitacion h : memoriaHabitaciones) {
            if (h.getNumeroHabitacion().equals(id)) return h;
        }
        return null;
    }

    @Override
    public List<Habitacion> listarTodo() { return new ArrayList<>(memoriaHabitaciones); }

    private void guardarPorSerializacion() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            oos.writeObject(memoriaHabitaciones);
            System.out.println("[DAO] Habitaciones guardadas en disco: '" + this.rutaArchivo + "'");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] No se pudo guardar habitaciones: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        if (!archivo.exists()) return;
        try {
            switch (this.metodoPersistencia) {
                case SERIALIZACION -> {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        memoriaHabitaciones = (List<Habitacion>) ois.readObject();
                        System.out.println("[DAO] " + memoriaHabitaciones.size() + " habitaciones cargadas desde disco.");
                    }
                }
                case ARCHIVO_BINARIO -> cargarDesdeBinario();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ERROR DAO] Error al cargar habitaciones: " + e.getMessage());
        }
    }

    private void actualizarArchivoFisico() {
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    private void guardarEnBinario() {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.rutaArchivo)))) {
            dos.writeInt(memoriaHabitaciones.size());
            for (Habitacion h : memoriaHabitaciones) {
                if (h instanceof Sencilla) {
                    dos.writeInt(1);
                    Sencilla s = (Sencilla) h;
                    dos.writeUTF(s.getNumeroHabitacion());
                    dos.writeUTF(s.getDescripcion() == null ? "" : s.getDescripcion());
                    dos.writeUTF(s.getPrecioPorNoche() == null ? "0" : s.getPrecioPorNoche().toPlainString());
                    dos.writeInt(s.getCapacidadMaxima());
                    dos.writeInt(s.getPiso());
                    dos.writeUTF(s.getEstado() == null ? "DISPONIBLE" : s.getEstado().name());
                    dos.writeBoolean(s.isIncluyeEscritorio());
                } else if (h instanceof Doble) {
                    dos.writeInt(2);
                    Doble d = (Doble) h;
                    dos.writeUTF(d.getNumeroHabitacion());
                    dos.writeUTF(d.getDescripcion() == null ? "" : d.getDescripcion());
                    dos.writeUTF(d.getPrecioPorNoche() == null ? "0" : d.getPrecioPorNoche().toPlainString());
                    dos.writeInt(d.getCapacidadMaxima());
                    dos.writeInt(d.getPiso());
                    dos.writeUTF(d.getEstado() == null ? "DISPONIBLE" : d.getEstado().name());
                    dos.writeInt(d.getCantidadCamas());
                } else if (h instanceof Matrimonial) {
                    dos.writeInt(3);
                    Matrimonial m = (Matrimonial) h;
                    dos.writeUTF(m.getNumeroHabitacion());
                    dos.writeUTF(m.getDescripcion() == null ? "" : m.getDescripcion());
                    dos.writeUTF(m.getPrecioPorNoche() == null ? "0" : m.getPrecioPorNoche().toPlainString());
                    dos.writeInt(m.getCapacidadMaxima());
                    dos.writeInt(m.getPiso());
                    dos.writeUTF(m.getEstado() == null ? "DISPONIBLE" : m.getEstado().name());
                    dos.writeBoolean(m.isIncluyeJacuzzi());
                } else {
                    System.err.println("[DAO] Tipo de habitación no soportado para binario: " + h.getClass().getName());
                }
            }
            dos.flush();
            System.out.println("[DAO] Habitaciones guardadas en binario: '" + this.rutaArchivo + "'");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al escribir habitaciones binario: " + e.getMessage());
        }
    }

    private void cargarDesdeBinario() {
        File f = new File(this.rutaArchivo);
        if (!f.exists()) return;
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            int n = dis.readInt();
            memoriaHabitaciones = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int tipo = dis.readInt();
                String numero = dis.readUTF();
                String descripcion = dis.readUTF();
                String precioStr = dis.readUTF();
                BigDecimal precio = new BigDecimal(precioStr);
                int capacidad = dis.readInt();
                int piso = dis.readInt();
                String estadoStr = dis.readUTF();
                EstadoHabitacion estado = EstadoHabitacion.valueOf(estadoStr);
                if (tipo == 1) {
                    boolean incluyeEscritorio = dis.readBoolean();
                    Sencilla s = new Sencilla(numero, descripcion, precio, estado, capacidad, piso, new ArrayList<>(), incluyeEscritorio);
                    memoriaHabitaciones.add(s);
                } else if (tipo == 2) {
                    int camas = dis.readInt();
                    Doble d = new Doble(numero, descripcion, precio, estado, capacidad, piso, new ArrayList<>(), camas);
                    memoriaHabitaciones.add(d);
                } else if (tipo == 3) {
                    boolean jacuzzi = dis.readBoolean();
                    Matrimonial m = new Matrimonial(numero, descripcion, precio, estado, capacidad, piso, new ArrayList<>(), jacuzzi);
                    memoriaHabitaciones.add(m);
                } else {
                    System.err.println("[DAO] Tipo desconocido al leer habitación binaria: " + tipo);
                }
            }
            System.out.println("[DAO] " + memoriaHabitaciones.size() + " habitaciones cargadas desde binario.");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al leer habitaciones binario: " + e.getMessage());
        }
    }
}
