package persistencia;

import dominio.Factura;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO extends DAOBase implements IDAO<Factura> {
    
    private static List<Factura> memoriaFacturas = new ArrayList<>();

    public FacturaDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Factura factura) {
        if (factura == null) return;
        eliminar(factura.getIdFactura());
        memoriaFacturas.add(factura);
        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) { guardarPorSerializacion(); }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaFacturas.removeIf(f -> f.getIdFactura().equals(id));
        if (eliminado && this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) { guardarPorSerializacion(); }
        return eliminado;
    }

    @Override
    public Factura buscarPorId(String id) {
        for (Factura f : memoriaFacturas) {
            if (f.getIdFactura().equals(id)) return f;
        }
        return null;
    }

    @Override
    public List<Factura> listarTodo() { return new ArrayList<>(memoriaFacturas); }

    private void guardarPorSerializacion() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            oos.writeObject(memoriaFacturas);
        } catch (IOException e) { System.err.println("[ERROR] " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        if (!archivo.exists() || this.metodoPersistencia != MetodoPersistencia.SERIALIZACION) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            memoriaFacturas = (List<Factura>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) { System.err.println("[ERROR] " + e.getMessage()); }
    }
}