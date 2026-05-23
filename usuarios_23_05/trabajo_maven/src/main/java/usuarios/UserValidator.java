package usuarios;

public class UserValidator {

    public static boolean validarNombre(String nombre) {
        return nombre != null && !nombre.trim().isEmpty();
    }

    public static boolean validarEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean validarTelefono(String telefono) {
        // Opcional: puede ser nulo o vacio
        if (telefono == null || telefono.trim().isEmpty()) return true;
        return telefono.matches("[0-9+\\-\\s()]{6,20}");
    }

    public static boolean validarUsuario(String nombre, String email, String telefono) {
        return validarNombre(nombre) && validarEmail(email) && validarTelefono(telefono);
    }
}

