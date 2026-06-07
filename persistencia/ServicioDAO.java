package persistencia;

import dominio.ServicioHotel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO extends DAOBase implements IDAO<ServicioHotel> {
    
    private List<ServicioHotel> memoriaServicios = new ArrayList<>();

    public ServicioDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(ServicioHotel servicio) {
        if (servicio == null) return;
        eliminar(servicio.getIdServicio().toString());
        memoriaServicios.add(servicio);
        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) { guardarPorSerializacion(); }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaServicios.removeIf(s -> s.getIdServicio().toString().equals(id));
        if (eliminado && this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) { guardarPorSerializacion(); }
        return eliminado;
    }

    @Override
    public ServicioHotel buscarPorId(String id) {
        for (ServicioHotel s : memoriaServicios) {
            if (s.getIdServicio().toString().equals(id)) return s;
        }
        return null;
    }

    @Override
    public List<ServicioHotel> listarTodo() { return new ArrayList<>(memoriaServicios); }

    private void guardarPorSerializacion() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            oos.writeObject(memoriaServicios);
        } catch (IOException e) { System.err.println("[ERROR] " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        if (!archivo.exists() || this.metodoPersistencia != MetodoPersistencia.SERIALIZACION) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            memoriaServicios = (List<ServicioHotel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) { System.err.println("[ERROR] " + e.getMessage()); }
    }
}
