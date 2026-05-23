package usuarios;


import java.sql.SQLException;
import java.util.List;

/**
 * CAPA DE REPOSITORIO — CONTRATO (Interfaz)
 * ------------------------------------------
 * Define las operaciones CRUD disponibles para la entidad Usuario.
 * La capa de servicio depende SOLO de esta interfaz, nunca de la
 * implementacion concreta. Esto permite cambiar MySQL por otro motor
 * sin tocar la logica de negocio (principio de inversion de dependencias).
 *
 * Operaciones soportadas:
 *   - Registrar uno o varios usuarios
 *   - Leer uno o todos los usuarios
 *   - Actualizar uno o varios usuarios
 *   - Eliminar uno o varios usuarios
 */
public interface IUserRepository {

    // ── CREATE ─────────────────────────────────────────────────────

    /** Guarda un unico usuario en la BD. */
    void guardar(UserDTO user) throws SQLException;

    /** Guarda una lista de usuarios en la BD (batch). */
    void guardarVarios(List<UserDTO> usuarios) throws SQLException;

    // ── READ ───────────────────────────────────────────────────────

    /** Retorna el usuario con el ID dado, o null si no existe. */
    UserDTO buscarPorId(int id) throws SQLException;

    /** Retorna todos los usuarios registrados. */
    List<UserDTO> buscarTodos() throws SQLException;

    /** Retorna los usuarios cuyos IDs esten en la lista. */
    List<UserDTO> buscarPorIds(List<Integer> ids) throws SQLException;

    // ── UPDATE ─────────────────────────────────────────────────────

    /** Actualiza los datos de un unico usuario. */
    void actualizar(UserDTO user) throws SQLException;

    /** Actualiza los datos de varios usuarios. */
    void actualizarVarios(List<UserDTO> usuarios) throws SQLException;

    // ── DELETE ─────────────────────────────────────────────────────

    /** Elimina el usuario con el ID dado. */
    void eliminar(int id) throws SQLException;

    /** Elimina varios usuarios segun la lista de IDs. */
    void eliminarVarios(List<Integer> ids) throws SQLException;
}