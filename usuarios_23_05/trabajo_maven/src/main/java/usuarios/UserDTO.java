package usuarios;

public class UserDTO {

    private int    id;
    private String nombre;
    private String email;
    private String telefono;

    // Constructor completo (con ID, usado al leer de la BD)
    public UserDTO(int id, String nombre, String email, String telefono) {
        this.id       = id;
        this.nombre   = nombre;
        this.email    = email;
        this.telefono = telefono;
       
    }

    // Constructor sin ID (usado al crear un usuario nuevo)
    public UserDTO(String nombre, String email, String telefono) {
        this(0, nombre, email, telefono );
    }

    // ── Getters y Setters ──────────────────────────────────────────

    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }

    public String getNombre()           { return nombre; }
    public void setNombre(String n)     { this.nombre = n; }

    public String getEmail()            { return email; }
    public void setEmail(String e)      { this.email = e; }

    public String getTelefono()         { return telefono; }
    public void setTelefono(String t)   { this.telefono = t; }

    

    // ── Representacion legible para consola ───────────────────────

    @Override
    public String toString() {
        return String.format("| %-4d | %-20s | %-28s | %-15s |", 
            id, nombre, email, telefono);
    }

    // Encabezado de tabla para consola2
    public static String encabezado() {
        return String.format(
            "| %-4s | %-25s | %-30s | %-12s |",
            "ID", "NOMBRE", "EMAIL", "TELEFONO"
        );
    }

    public static String separador() {
        return "+------+------------------------------+------"
             + "+---------------------+----------------------+";
    }
}

