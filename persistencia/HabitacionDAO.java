package persistencia;

import dominio.Habitacion;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO extends DAOBase implements IDAO<Habitacion> {
    
    // Lista por instancia (no compartida entre instancias)
    private List<Habitacion> memoriaHabitaciones = new ArrayList<>();

    public HabitacionDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Habitacion habitacion) {
        if (habitacion == null) return;
        
        eliminar(habitacion.getNumeroHabitacion());
        memoriaHabitaciones.add(habitacion);
        
        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            guardarPorSerializacion();
        }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaHabitaciones.removeIf(h -> h.getNumeroHabitacion().equals(id));
        if (eliminado && this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            guardarPorSerializacion();
        }
        return eliminado;
    }

    @Override
    public Habitacion buscarPorId(String id) {
        for (Habitacion h : memoriaHabitaciones) {
            if (h.getNumeroHabitacion().equals(id)) {
                return h;
            }
        }
        return null;
    }

    @Override
    public List<Habitacion> listarTodo() {
        return new ArrayList<>(memoriaHabitaciones);
    }

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

        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                memoriaHabitaciones = (List<Habitacion>) ois.readObject();
                System.out.println("[DAO] " + memoriaHabitaciones.size() + " habitaciones cargadas desde disco.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[ERROR DAO] Error al cargar habitaciones: " + e.getMessage());
            }
        }
    }
}
