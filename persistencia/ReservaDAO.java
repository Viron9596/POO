package persistencia;

import dominio.Reserva;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO extends DAOBase implements IDAO<Reserva> {
    
    private static List<Reserva> memoriaReservas = new ArrayList<>();

    public ReservaDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Reserva reserva) {
        if (reserva == null) return;
        
        eliminar(reserva.getIdReserva());
        memoriaReservas.add(reserva);
        
        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            guardarPorSerializacion();
        }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaReservas.removeIf(r -> r.getIdReserva().equals(id));
        if (eliminado && this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            guardarPorSerializacion();
        }
        return eliminado;
    }

    @Override
    public Reserva buscarPorId(String id) {
        for (Reserva r : memoriaReservas) {
            if (r.getIdReserva().equals(id)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Reserva> listarTodo() {
        return new ArrayList<>(memoriaReservas);
    }

    private void guardarPorSerializacion() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            oos.writeObject(memoriaReservas);
            System.out.println("[DAO] Reservas resguardadas físicamente en: '" + this.rutaArchivo + "'");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al serializar reservas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        if (!archivo.exists()) return;

        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                memoriaReservas = (List<Reserva>) ois.readObject();
                System.out.println("[DAO] " + memoriaReservas.size() + " reservas activas cargadas desde disco.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[ERROR DAO] Error al cargar histórico de reservas: " + e.getMessage());
            }
        }
    }
}