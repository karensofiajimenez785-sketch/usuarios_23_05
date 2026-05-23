package usuarios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * CAPA DE INFRAESTRUCTURA / CONFIGURACION
 * -----------------------------------------
 * Responsable de gestionar la conexion con la base de datos MySQL.
 * Tambien inicializa la tabla de usuarios si no existe.
 */
public class DatabaseConfig {

	private static final  String URL = "jdbc:mysql://127.0.0.1:3306/gestion_usuarios_db" + 
	"?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&autoReconnect=true";
	private static final String USER = "karencita";
    private static final String PASSWORD = "adso256";

    // ---------------------------------------------------------------
    // Retorna una conexion activa a la base de datos
    // ---------------------------------------------------------------
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ---------------------------------------------------------------
    // Crea la tabla USUARIOS si no existe (se llama al iniciar la app)
    // ---------------------------------------------------------------
    public static void inicializarBaseDeDatos() {
        String sql = "CREATE TABLE IF NOT EXISTS usuario ("
                + "id       INT AUTO_INCREMENT PRIMARY KEY, "
                + "nombre   VARCHAR(100) NOT NULL, "
                + "email    VARCHAR(150) NOT NULL UNIQUE, "
                + "telefono VARCHAR(20), "
                + ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("[DB] Tabla 'usuarios' verificada/creada correctamente.");
        } catch (SQLException e) {
            System.err.println("[DB ERROR] No se pudo inicializar la base de datos: " + e.getMessage());
        }
    }
}

