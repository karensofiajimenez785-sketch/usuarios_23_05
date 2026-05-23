package usuarios;

public class InvalidUserDataException extends Exception {
    public InvalidUserDataException(String mensaje) {
        super(mensaje);
    }
}

