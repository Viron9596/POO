package persistencia;

import dominio.Factura;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class FacturaDAO extends DAOBase implements IDAO<Factura> {

    private List<Factura> memoriaFacturas = new ArrayList<>();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public FacturaDAO(String rutaArchivo, MetodoPersistencia metodoPersistencia) {
        super(rutaArchivo, metodoPersistencia);
        cargarDesdeDisco();
    }

    @Override
    public void guardar(Factura factura) {
        if (factura == null) return;
        eliminar(factura.getIdFactura());
        memoriaFacturas.add(factura);
        switch (this.metodoPersistencia) {
            case SERIALIZACION -> guardarPorSerializacion();
            case ARCHIVO_BINARIO -> guardarEnBinario();
        }
    }

    @Override
    public boolean eliminar(String id) {
        boolean eliminado = memoriaFacturas.removeIf(f -> f.getIdFactura().equals(id));
        if (eliminado) actualizarArchivoFisico();
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
        if (!archivo.exists()) return;
        try {
            switch (this.metodoPersistencia) {
                case SERIALIZACION -> {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        memoriaFacturas = (List<Factura>) ois.readObject();
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
            dos.writeInt(memoriaFacturas.size());
            for (Factura f : memoriaFacturas) {
                dos.writeUTF(f.getIdFactura() == null ? "" : f.getIdFactura());
                dos.writeUTF(f.getFechaEmision() == null ? "" : f.getFechaEmision().toString());
                dos.writeUTF(f.getNombreCliente() == null ? "" : f.getNombreCliente());
                dos.writeUTF(f.getIdCliente() == null ? "" : f.getIdCliente());
                List<String> dh = f.getDetalleHabitaciones();
                dos.writeInt(dh == null ? 0 : dh.size());
                if (dh != null) for (String s : dh) dos.writeUTF(s == null ? "" : s);
                List<String> ds = f.getDetalleServicios();
                dos.writeInt(ds == null ? 0 : ds.size());
                if (ds != null) for (String s : ds) dos.writeUTF(s == null ? "" : s);
                dos.writeUTF(f.getSubtotal() == null ? "0" : f.getSubtotal().toPlainString());
                dos.writeUTF(f.getDescuento() == null ? "0" : f.getDescuento().toPlainString());
                dos.writeUTF(f.getIva() == null ? "0" : f.getIva().toPlainString());
                dos.writeUTF(f.getTotal() == null ? "0" : f.getTotal().toPlainString());
            }
            dos.flush();
            System.out.println("[DAO] Facturas guardadas en binario: '" + this.rutaArchivo + "'");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al escribir facturas binario: " + e.getMessage());
        }
    }

    private void cargarDesdeBinario() {
        File f = new File(this.rutaArchivo);
        if (!f.exists()) return;
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            int n = dis.readInt();
            memoriaFacturas = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String id = dis.readUTF();
                String fechaStr = dis.readUTF();
                LocalDate fecha = (fechaStr == null || fechaStr.isEmpty()) ? LocalDate.now() : LocalDate.parse(fechaStr, DATE_FMT);
                String nombreCli = dis.readUTF();
                String idCli = dis.readUTF();
                int nDh = dis.readInt();
                ArrayList<String> detHab = new ArrayList<>();
                for (int j = 0; j < nDh; j++) detHab.add(dis.readUTF());
                int nDs = dis.readInt();
                ArrayList<String> detServ = new ArrayList<>();
                for (int j = 0; j < nDs; j++) detServ.add(dis.readUTF());
                String subtotalStr = dis.readUTF();
                String descuentoStr = dis.readUTF();
                String ivaStr = dis.readUTF();
                String totalStr = dis.readUTF();
                BigDecimal subtotal = subtotalStr == null || subtotalStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(subtotalStr);
                BigDecimal descuento = descuentoStr == null || descuentoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(descuentoStr);
                BigDecimal iva = ivaStr == null || ivaStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(ivaStr);
                BigDecimal total = totalStr == null || totalStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(totalStr);
                Factura fac = new Factura(id, fecha, nombreCli, idCli, detHab, detServ, subtotal, descuento, iva, total);
                memoriaFacturas.add(fac);
            }
            System.out.println("[DAO] " + memoriaFacturas.size() + " facturas cargadas desde binario.");
        } catch (IOException e) {
            System.err.println("[ERROR DAO] Error al leer facturas binario: " + e.getMessage());
        }
    }
}
