package persistencia;

import dominio.Cliente;
import dominio.PersonaNatural;
import dominio.ClienteHabitual;
import dominio.ClienteEsporadico;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClienteDAO extends DAOBase implements IDAO<Cliente> {
    
    private List<Cliente> memoriaClientes = new ArrayList<>();

    public ClienteDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Cliente cliente) {
        if (cliente == null) return;
        // Limpiamos si ya existía para actualizarlo
        eliminar(cliente.obtenerIdentificacion());
        memoriaClientes.add(cliente);
        // Persistencia según estrategia
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_TXT -> guardarEnTextoPlano();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaClientes.removeIf(c -> c.obtenerIdentificacion().equals(id));
        if (eliminado) {
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
    // SERIALIZACION
    // =======================================================
    private void guardarPorSerializacion() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            oos.writeObject(memoriaClientes);
            System.out.println("[DAO] Datos guardados físicamente en '" + this.rutaArchivo + "' vía Serialización.");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    // =======================================================
    // CARGA / GUARDO desde DISCO
    // =======================================================
    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        if (!archivo.exists()) return;

        try {
            switch (this.metodoPersistencia) {
                case SERIALIZACION -> {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        memoriaClientes = (List<Cliente>) ois.readObject();
                        System.out.println("[DAO] " + memoriaClientes.size() + " clientes cargados con éxito desde el disco (serialización).");
                    }
                }
                case ARCHIVO_BINARIO -> cargarDesdeBinario();
                case ARCHIVO_TXT -> {
                    // futuro: implementar texto
                    System.out.println("[DAO] Carga en texto no implementada aún.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ERROR DAO] Error al cargar datos: " + e.getMessage());
        }
    }

    private void actualizarArchivoFisico() {
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_TXT -> guardarEnTextoPlano();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    // =======================================================
    // ARCHIVO_TXT (placeholder)
    // =======================================================
    private void guardarEnTextoPlano() {
        System.out.println("[DAO] Pendiente codificar escritura de texto plano (CSV/JSON)");
    }

    // =======================================================
    // ARCHIVO_BINARIO (implementación)
    // =======================================================
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private void guardarEnBinario() {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.rutaArchivo)))) {
            dos.writeInt(memoriaClientes.size());
            for (Cliente c : memoriaClientes) {
                // hoy soportamos PersonaNatural
                if (c instanceof PersonaNatural) {
                    dos.writeInt(1); // tipo 1 = PersonaNatural
                    PersonaNatural pn = (PersonaNatural) c;
                    dos.writeUTF(pn.obtenerIdentificacion()); // cedula/id
                    dos.writeUTF(pn.getNombre());
                    dos.writeUTF(pn.getApellido());
                    dos.writeUTF(pn.getTelefono() == null ? "" : pn.getTelefono());
                    dos.writeUTF(pn.getCorreo() == null ? "" : pn.getCorreo());
                    dos.writeUTF(pn.getDireccion() == null ? "" : pn.getDireccion());
                    dos.writeUTF(pn.getFechaRegistro() == null ? "" : pn.getFechaRegistro().format(DATE_FMT));
                    dos.writeBoolean(pn.isActivo());
                    // fidelidad
                    if (pn.getFidelidad() != null && pn.getFidelidad() instanceof ClienteHabitual) {
                        ClienteHabitual ch = (ClienteHabitual) pn.getFidelidad();
                        dos.writeBoolean(true);
                        dos.writeDouble(ch.getPorcentajeDescuento());
                        dos.writeInt(ch.getPuntosAcumulados());
                    } else {
                        dos.writeBoolean(false);
                    }
                } else {
                    // tipos desconocidos: saltar
                    System.err.println("[DAO] Tipo de Cliente no soportado para binario: " + c.getClass().getName());
                }
            }
            dos.flush();
            System.out.println("[DAO] Clientes guardados en binario: '" + this.rutaArchivo + "'");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al escribir binario: " + e.getMessage());
        }
    }

    private void cargarDesdeBinario() {
        File f = new File(this.rutaArchivo);
        if (!f.exists()) return;
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            int n = dis.readInt();
            memoriaClientes = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int tipo = dis.readInt();
                if (tipo == 1) { // PersonaNatural
                    String cedula = dis.readUTF();
                    String nombre = dis.readUTF();
                    String apellido = dis.readUTF();
                    String telefono = dis.readUTF();
                    String correo = dis.readUTF();
                    String direccion = dis.readUTF();
                    String fechaRegStr = dis.readUTF();
                    LocalDate fechaReg = (fechaRegStr == null || fechaRegStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(fechaRegStr, DATE_FMT);
                    boolean activo = dis.readBoolean();
                    boolean tieneFidelidad = dis.readBoolean();
                    PersonaNatural pn;
                    if (tieneFidelidad) {
                        double porcentaje = dis.readDouble();
                        int puntos = dis.readInt();
                        ClienteHabitual ch = new ClienteHabitual(porcentaje, puntos);
                        pn = new PersonaNatural(cedula, telefono, correo, direccion, fechaReg, activo, ch, nombre, apellido, cedula, LocalDate.now());
                    } else {
                        pn = new PersonaNatural(cedula, telefono, correo, direccion, fechaReg, activo, new ClienteEsporadico(), nombre, apellido, cedula, LocalDate.now());
                    }
                    memoriaClientes.add(pn);
                } else {
                    System.err.println("[DAO] Tipo desconocido al leer cliente binario: " + tipo);
                }
            }
            System.out.println("[DAO] " + memoriaClientes.size() + " clientes cargados desde binario.");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al leer binario: " + e.getMessage());
        }
    }
}
