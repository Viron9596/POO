package persistencia;

import dominio.Cliente;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO extends DAOBase implements IDAO<Cliente> {
    
    // Lista por instancia (no compartida entre instancias) para evitar estado global impredecible
    private List<Cliente> memoriaClientes = new ArrayList<>();

    public ClienteDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        // Al arrancar el DAO, cargamos lo que esté guardado en el disco
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Cliente cliente) {
        if (cliente == null) return;
        
        // Limpiamos si ya existía para actualizarlo
        eliminar(cliente.obtenerIdentificacion());
        memoriaClientes.add(cliente);
        
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_TXT -> guardarEnTextoPlano(); // (Para implementar después)
            case ARCHIVO_BINARIO -> guardarEnBinario(); // (Para implementar después)
        }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaClientes.removeIf(c -> c.obtenerIdentificacion().equals(id));
        if (eliminado) {
            // Si eliminamos de la RAM, actualizamos el archivo físico inmediatamente
            actualizarArchivoFisico();
        }
        return eliminado;
    }

    @Override
    public Cliente buscarPorId(String id) {
        for (Cliente c : memoriaClientes) {
            if (c.obtenerIdentificacion().equals(id)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Cliente> listarTodo() {
        return new ArrayList<>(memoriaClientes);
    }

    // =======================================================
    // MÉTODOS PRIVADOS DE OPERACIÓN FÍSICA (I/O STREAMS)
    // =======================================================

    private void guardarPorSerializacion() {
        // Uso de try-with-resources: asegura el cierre automático del archivo
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            // Guardamos la lista completa de clientes de un solo golpe en el archivo
            oos.writeObject(memoriaClientes);
            System.out.println("[DAO] Datos guardados físicamente en '" + this.rutaArchivo + "' vía Serialización.");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        // Si el archivo no existe (primera vez que corre el sistema), no hacemos nada
        if (!archivo.exists()) return;

        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                memoriaClientes = (List<Cliente>) ois.readObject();
                System.out.println("[DAO] " + memoriaClientes.size() + " clientes cargados con éxito desde el disco.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[ERROR DAO] Error al cargar datos: " + e.getMessage());
            }
        }
    }

    private void actualizarArchivoFisico() {
        if (this.metodoPersistencia == MetodoPersistencia.SERIALIZACION) {
            guardarPorSerializacion();
        }
    }

    // Plantillas vacías para cumplir con el contrato del Enum si el usuario cambia la estrategia
    private void guardarEnTextoPlano() {
        System.out.println("[DAO] Pendiente codificar escritura de texto plano (CSV)");
    }

    private void guardarEnBinario() {
        System.out.println("[DAO] Pendiente codificar escritura binaria pura");
    }
}
