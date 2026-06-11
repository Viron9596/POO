package persistencia;

import dominio.ServicioHotel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class ServicioDAO extends DAOBase implements IDAO<ServicioHotel> {

    private List<ServicioHotel> memoriaServicios = new ArrayList<>();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public ServicioDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(ServicioHotel servicio) {
        if (servicio == null) return;
        // usar toPlainString para representar el BigDecimal de forma estable
        eliminar(servicio.getIdServicio().toPlainString());
        memoriaServicios.add(servicio);
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    @Override
    public boolean eliminar(String id) {
        if (id == null) return false;
        try {
            BigDecimal idBd = new BigDecimal(id);
            boolean eliminado = memoriaServicios.removeIf(s -> s.getIdServicio() != null && s.getIdServicio().compareTo(idBd) == 0);
            if (eliminado) actualizarArchivoFisico();
            return eliminado;
        } catch (NumberFormatException e) {
            // Si el id no es un número válido, no hacemos nada
            return false;
        }
    }

    @Override
    public ServicioHotel buscarPorId(String id) {
        if (id == null) return null;
        try {
            BigDecimal idBd = new BigDecimal(id);
            for (ServicioHotel s : memoriaServicios) {
                if (s.getIdServicio() != null && s.getIdServicio().compareTo(idBd) == 0) return s;
            }
        } catch (NumberFormatException e) {
            // id no numérico
        }
        return null;
    }

    @Override
    public List<ServicioHotel> listarTodo() { return new ArrayList<>(memoriaServicios); }

    private void guardarPorSerializacion() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.rutaArchivo))) {
            oos.writeObject(memoriaServicios);
            System.out.println("[DAO] Servicios guardados en disco (serialización): '" + this.rutaArchivo + "'");
        } catch (IOException e) { System.err.println("[ERROR] " + e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeDisco() {
        File archivo = new File(this.rutaArchivo);
        if (!archivo.exists()) return;
        try {
            switch (this.metodoPersistencia) {
                case SERIALIZACION -> {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        memoriaServicios = (List<ServicioHotel>) ois.readObject();
                    }
                }
                case ARCHIVO_BINARIO -> cargarDesdeBinario();
            }
        } catch (IOException | ClassNotFoundException e) { System.err.println("[ERROR] " + e.getMessage()); }
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
            dos.writeInt(memoriaServicios.size());
            for (ServicioHotel s : memoriaServicios) {
                dos.writeUTF(s.getIdServicio() == null ? "" : s.getIdServicio().toPlainString());
                dos.writeUTF(s.getNombre() == null ? "" : s.getNombre());
                dos.writeUTF(s.getDescripcion() == null ? "" : s.getDescripcion());
                dos.writeUTF(s.getCosto() == null ? "0" : s.getCosto().toPlainString());
                dos.writeBoolean(s.isActivo());
            }
            dos.flush();
            System.out.println("[DAO] Servicios guardados en binario: '" + this.rutaArchivo + "'");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al escribir servicios binario: " + e.getMessage());
        }
    }

    private void cargarDesdeBinario() {
        File f = new File(this.rutaArchivo);
        if (!f.exists()) return;
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            int n = dis.readInt();
            memoriaServicios = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String idStr = dis.readUTF();
                String nombre = dis.readUTF();
                String descripcion = dis.readUTF();
                String costoStr = dis.readUTF();
                boolean activo = dis.readBoolean();
                BigDecimal idBd = idStr == null || idStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(idStr);
                BigDecimal costo = costoStr == null || costoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(costoStr);
                ServicioHotel s = new ServicioHotel(idBd, nombre, descripcion, costo, activo);
                memoriaServicios.add(s);
            }
            System.out.println("[DAO] " + memoriaServicios.size() + " servicios cargados desde binario.");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al leer servicios binario: " + e.getMessage());
        }
    }
}
