package usuarios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CAPA DE REPOSITORIO — IMPLEMENTACION CONCRETA (MySQL / JDBC)
 * -------------------------------------------------------------
 * Implementa IUserRepository usando JDBC puro, igual que el ejemplo
 * del patron Repository del docente.
 *
 * Esta clase es la UNICA que conoce:
 *   - Nombres de tabla y columnas
 *   - Sintaxis SQL especifica de MySQL
 *   - Manejo de conexiones y ResultSet
 *
 * Si se cambia a PostgreSQL, Oracle, o un archivo CSV, solo se
 * reescribe esta clase; el resto del sistema queda intacto.
 */
public class UserRepository implements IUserRepository {

    // ── Sentencias SQL ─────────────────────────────────────────────

    private static final String SQL_INSERT =
        "INSERT INTO usuario (nombre, email, telefono) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
        "SELECT * FROM usuario WHERE id = ?";

    private static final String SQL_SELECT_ALL =
        "SELECT * FROM usuario ORDER BY id";

    private static final String SQL_UPDATE =
        "UPDATE usuario SET nombre = ?, email = ?, telefono = ? WHERE id = ?";

    private static final String SQL_DELETE =
        "DELETE FROM usuario WHERE id = ?";

    // ── CREATE ─────────────────────────────────────────────────────

    @Override
    public void guardar(UserDTO user) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            mapearParametros(ps, user);
            ps.executeUpdate();
        }
    }

    @Override
    public void guardarVarios(List<UserDTO> usuarios) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            conn.setAutoCommit(false);                          // Transaccion batch
            try {
                for (UserDTO user : usuarios) {
                    mapearParametros(ps, user);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();                                // Revertir si falla alguno
                throw e;
            }
        }
    }

    // ── READ ───────────────────────────────────────────────────────

    @Override
    public UserDTO buscarPorId(int id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultado(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<UserDTO> buscarTodos() throws SQLException {
        List<UserDTO> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        }
        return lista;
    }

    @Override
    public List<UserDTO> buscarPorIds(List<Integer> ids) throws SQLException {
        List<UserDTO> lista = new ArrayList<>();
        for (int id : ids) {
            UserDTO u = buscarPorId(id);
            if (u != null) {
                lista.add(u);
            }
        }
        return lista;
    }

    // ── UPDATE ─────────────────────────────────────────────────────

    @Override
    public void actualizar(UserDTO user) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getTelefono());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizarVarios(List<UserDTO> usuarios) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            conn.setAutoCommit(false);
            try {
                for (UserDTO user : usuarios) {
                    ps.setString(1, user.getNombre());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getTelefono());
                    ps.setInt(4, user.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────

    @Override
    public void eliminar(int id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminarVarios(List<Integer> ids) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            conn.setAutoCommit(false);
            try {
                for (int id : ids) {
                    ps.setInt(1, id);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ── Helpers privados ───────────────────────────────────────────

    /** Mapea los campos de un UserDTO a los parametros de un PreparedStatement INSERT. */
    private void mapearParametros(PreparedStatement ps, UserDTO user) throws SQLException {
        ps.setString(1, user.getNombre());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getTelefono());
    }

    /** Construye un UserDTO a partir de la fila actual de un ResultSet. */
    private UserDTO mapearResultado(ResultSet rs) throws SQLException {
        return new UserDTO(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("email"),
            rs.getString("telefono")
        );
    }
}

