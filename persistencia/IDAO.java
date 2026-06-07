package persistencia;

import java.util.List;

/**
 * Interfaz genérica para la manipulación y persistencia de datos.
 * @param <T> Tipo de entidad con la que operará el DAO.
 */
public interface IDAO<T> {
    
    // Recibe un objeto tipo T y no retorna nada [cite: 358]
    void guardar(T entidad);
    
    // Recibe el identificador String y confirma el éxito de la eliminación [cite: 359]
    boolean eliminar(String id);
    
    // Busca un registro por su clave primaria e id, retornando la entidad T [cite: 360]
    T buscarPorId(String id);
    
    // Recupera una lista completa con todas las entidades almacenadas [cite: 360]
    List<T> listarTodo();
}