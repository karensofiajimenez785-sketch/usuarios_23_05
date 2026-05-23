package usuarios;

import java.sql.SQLException;
import java.util.List;

/**
 * CAPA DE SERVICIO / LOGICA DE NEGOCIO
 * --------------------------------------
 * Orquesta las operaciones sobre usuarios:
 *   1. Valida los datos de entrada (UserValidator)
 *   2. Delega el acceso a datos al repositorio (IUserRepository)
 *   3. Lanza excepciones de dominio si los datos son incorrectos
 *
 * Esta capa NO sabe si los datos vienen de consola, de una API REST
 * o de un formulario web. Tampoco sabe si la BD es MySQL u otra.
 * Esa independencia es el corazon del patron Repository.
 */
public class UserService {

    // Dependencia inyectada a traves de la INTERFAZ (no la clase concreta)
    private IUserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();   // Unico punto de acoplamiento concreto
    }

    // Constructor alternativo para inyeccion de dependencias / pruebas
    public UserService(IUserRepository repositorio) {
        this.userRepository = repositorio;
    }

    // ── CREATE ─────────────────────────────────────────────────────

    public void registrarUsuario(String nombre, String email, String telefono)
            throws SQLException, InvalidUserDataException {
        if (!UserValidator.validarUsuario(nombre, email, telefono)) {
            throw new InvalidUserDataException(
                "Datos invalidos: verifique nombre, email y telefono.");
        }
        UserDTO user = new UserDTO(nombre, email, telefono);
        userRepository.guardar(user);
    }

    public void registrarVarios(List<UserDTO> usuarios)
            throws SQLException, InvalidUserDataException {
        for (UserDTO u : usuarios) {
            if (!UserValidator.validarUsuario(u.getNombre(), u.getEmail(), u.getTelefono())) {
                throw new InvalidUserDataException(
                    "Datos invalidos para: " + u.getNombre() + " / " + u.getEmail());
            }
        }
        userRepository.guardarVarios(usuarios);
    }

    // ── READ ───────────────────────────────────────────────────────

    public UserDTO obtenerPorId(int id) throws SQLException {
        return userRepository.buscarPorId(id);
    }

    public List<UserDTO> obtenerTodos() throws SQLException {
        return userRepository.buscarTodos();
    }

    public List<UserDTO> obtenerPorIds(List<Integer> ids) throws SQLException {
        return userRepository.buscarPorIds(ids);
    }

    // ── UPDATE ─────────────────────────────────────────────────────

    public void actualizarUsuario(UserDTO user)
            throws SQLException, InvalidUserDataException {
        if (!UserValidator.validarUsuario(user.getNombre(), user.getEmail(), user.getTelefono())) {
            throw new InvalidUserDataException("Datos invalidos al actualizar usuario ID: " + user.getId());
        }
        userRepository.actualizar(user);
    }

    public void actualizarVarios(List<UserDTO> usuarios)
            throws SQLException, InvalidUserDataException {
        for (UserDTO u : usuarios) {
            if (!UserValidator.validarUsuario(u.getNombre(), u.getEmail(), u.getTelefono())) {
                throw new InvalidUserDataException("Datos invalidos al actualizar ID: " + u.getId());
            }
        }
        userRepository.actualizarVarios(usuarios);
    }

    // ── DELETE ─────────────────────────────────────────────────────

    public void eliminarUsuario(int id) throws SQLException {
        userRepository.eliminar(id);
    }

    public void eliminarVarios(List<Integer> ids) throws SQLException {
        userRepository.eliminarVarios(ids);
    }
}
