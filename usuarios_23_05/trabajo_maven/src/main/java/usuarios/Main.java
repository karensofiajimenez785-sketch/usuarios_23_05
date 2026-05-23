package usuarios;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * CAPA DE PRESENTACION — INTERFAZ DE CONSOLA
 * -------------------------------------------
 * Unico punto de entrada de la aplicacion.
 * Muestra menus, lee entradas del usuario y llama al UserService.
 * No contiene logica de negocio ni SQL.
 */
public class Main {

    private static final UserService service = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    // ══════════════════════════════════════════════════════════════
    //  ENTRADA PRINCIPAL
    // ══════════════════════════════════════════════════════════════
    public static void main(String[] args) {


        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opcion: ");
            System.out.println();

            switch (opcion) {
                case 1: menuRegistrar();    break;
                case 2: menuLeer();         break;
                case 3: menuActualizar();   break;
                case 4: menuEliminar();     break;
                case 5: salir = true;       break;
                default: System.out.println("  [!] Opcion no valida. Intente de nuevo.");
            }
        }

        System.out.println("\n  Hasta luego. Sesion cerrada.\n");
        scanner.close();
    }

    // ══════════════════════════════════════════════════════════════
    //  MENUS
    // ══════════════════════════════════════════════════════════════

    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    SISTEMA DE GESTION DE USUARIOS    ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Registrar usuario(s)             ║");
        System.out.println("║  2. Consultar usuario(s)             ║");
        System.out.println("║  3. Actualizar usuario(s)            ║");
        System.out.println("║  4. Eliminar usuario(s)              ║");
        System.out.println("║  5. Salir                            ║");
        System.out.println("╚══════════════════════════════════════╝");
    }  

    // ─────────────────────────────────────────────────────────────
    //  1. REGISTRAR
    // ─────────────────────────────────────────────────────────────
    private static void menuRegistrar() {
        System.out.println("── REGISTRAR USUARIOS ──");
        System.out.println("  1. Registrar un usuario");
        System.out.println("  2. Registrar varios usuarios");
        int op = leerEntero("Opcion: ");

        if (op == 1) {
            UserDTO u = leerDatosUsuario("Nuevo usuario");
            if (u == null) return;
            try {
                service.registrarUsuario(u.getNombre(), u.getEmail(),
                                         u.getTelefono());
                System.out.println("  [OK] Usuario registrado exitosamente.");
            } catch (InvalidUserDataException e) {
                System.err.println("  [ERROR Validacion] " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("  [ERROR BD] " + e.getMessage());
            }

        } else if (op == 2) {
            int cantidad = leerEntero("Cuantos usuarios desea registrar? ");
            List<UserDTO> lista = new ArrayList<>();
            for (int i = 1; i <= cantidad; i++) {
                UserDTO u = leerDatosUsuario("Usuario " + i + " de " + cantidad);
                if (u != null) lista.add(u);
            }
            if (lista.isEmpty()) { System.out.println("  No se ingresaron usuarios."); return; }
            try {
                service.registrarVarios(lista);
                System.out.println("  [OK] " + lista.size() + " usuario(s) registrados exitosamente.");
            } catch (InvalidUserDataException e) {
                System.err.println("  [ERROR Validacion] " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("  [ERROR BD] " + e.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  2. LEER / CONSULTAR
    // ─────────────────────────────────────────────────────────────
    private static void menuLeer() {
        System.out.println("── CONSULTAR USUARIOS ──");
        System.out.println("  1. Ver todos los usuarios");
        System.out.println("  2. Buscar por ID");
        System.out.println("  3. Buscar varios por IDs");
        int op = leerEntero("Opcion: ");

        try {
            if (op == 1) {
                List<UserDTO> todos = service.obtenerTodos();
                imprimirTabla(todos);

            } else if (op == 2) {
                int id = leerEntero("ID del usuario: ");
                UserDTO u = service.obtenerPorId(id);
                if (u != null) {
                    imprimirTabla(List.of(u));
                } else {
                    System.out.println("  [!] No se encontro usuario con ID " + id);
                }

            } else if (op == 3) {
                List<Integer> ids = leerListaIds();
                List<UserDTO> lista = service.obtenerPorIds(ids);
                imprimirTabla(lista);
            }
        } catch (SQLException e) {
            System.err.println("  [ERROR BD] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  3. ACTUALIZAR
    // ─────────────────────────────────────────────────────────────
    private static void menuActualizar() {
        System.out.println("── ACTUALIZAR USUARIOS ──");
        System.out.println("  1. Actualizar un usuario");
        System.out.println("  2. Actualizar varios usuarios");
        int op = leerEntero("Opcion: ");

        try {
            if (op == 1) {
                int id = leerEntero("ID del usuario a actualizar: ");
                UserDTO existente = service.obtenerPorId(id);
                if (existente == null) {
                    System.out.println("  [!] No existe usuario con ID " + id);
                    return;
                }
                System.out.println("  Datos actuales:");
                imprimirTabla(List.of(existente));
                System.out.println("  Ingrese los nuevos datos:");
                UserDTO nuevos = leerDatosUsuario("Actualizar");
                if (nuevos == null) return;
                nuevos.setId(id);
                service.actualizarUsuario(nuevos);
                System.out.println("  [OK] Usuario ID " + id + " actualizado.");

            } else if (op == 2) {
                List<Integer> ids = leerListaIds();
                List<UserDTO> actualizados = new ArrayList<>();
                for (int id : ids) {
                    UserDTO existente = service.obtenerPorId(id);
                    if (existente == null) {
                        System.out.println("  [!] No existe usuario con ID " + id + ". Se omite.");
                        continue;
                    }
                    System.out.println("  Datos actuales de ID " + id + ":");
                    imprimirTabla(List.of(existente));
                    UserDTO nuevos = leerDatosUsuario("Actualizar ID " + id);
                    if (nuevos != null) {
                        nuevos.setId(id);
                        actualizados.add(nuevos);
                    }
                }
                if (!actualizados.isEmpty()) {
                    service.actualizarVarios(actualizados);
                    System.out.println("  [OK] " + actualizados.size() + " usuario(s) actualizados.");
                }
            }
        } catch (InvalidUserDataException e) {
            System.err.println("  [ERROR Validacion] " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("  [ERROR BD] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  4. ELIMINAR
    // ─────────────────────────────────────────────────────────────
    private static void menuEliminar() {
        System.out.println("── ELIMINAR USUARIOS ──");
        System.out.println("  1. Eliminar un usuario");
        System.out.println("  2. Eliminar varios usuarios");
        int op = leerEntero("Opcion: ");

        try {
            if (op == 1) {
                int id = leerEntero("ID del usuario a eliminar: ");
                UserDTO u = service.obtenerPorId(id);
                if (u == null) {
                    System.out.println("  [!] No existe usuario con ID " + id);
                    return;
                }
                imprimirTabla(List.of(u));
                System.out.print("  Confirma la eliminacion? (s/n): ");
                String conf = scanner.nextLine().trim().toLowerCase();
                if (conf.equals("s")) {
                    service.eliminarUsuario(id);
                    System.out.println("  [OK] Usuario ID " + id + " eliminado.");
                } else {
                    System.out.println("  Operacion cancelada.");
                }

            } else if (op == 2) {
                List<Integer> ids = leerListaIds();
                List<UserDTO> encontrados = service.obtenerPorIds(ids);
                if (encontrados.isEmpty()) {
                    System.out.println("  [!] Ninguno de los IDs fue encontrado."); return;
                }
                System.out.println("  Usuarios a eliminar:");
                imprimirTabla(encontrados);
                System.out.print("  Confirma la eliminacion de " + encontrados.size() + " usuario(s)? (s/n): ");
                String conf = scanner.nextLine().trim().toLowerCase();
                if (conf.equals("s")) {
                    List<Integer> idsEncontrados = encontrados.stream()
                            .map(UserDTO::getId).collect(Collectors.toList());
                    service.eliminarVarios(idsEncontrados);
                    System.out.println("  [OK] " + idsEncontrados.size() + " usuario(s) eliminados.");
                } else {
                    System.out.println("  Operacion cancelada.");
                }
            }
        } catch (SQLException e) {
            System.err.println("  [ERROR BD] " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  UTILIDADES DE CONSOLA
    // ══════════════════════════════════════════════════════════════

    /** Lee los datos de un usuario desde la consola. Retorna null si el nombre esta vacio. */
    private static UserDTO leerDatosUsuario(String titulo) {
        System.out.println("  [" + titulo + "]");
        System.out.print("  Nombre   : "); String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) { System.out.println("  [!] Nombre obligatorio. Operacion cancelada."); return null; }
        System.out.print("  Email    : "); String email    = scanner.nextLine().trim();
        System.out.print("  Telefono : "); String telefono = scanner.nextLine().trim();
        return new UserDTO(nombre, email, telefono);
    }

    /** Lee un entero de la consola; reintenta si el valor no es valido. */
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Debe ingresar un numero entero.");
            }
        }
    }

    /** Lee una lista de IDs separados por coma: "1,2,5" → [1, 2, 5] */
    private static List<Integer> leerListaIds() {
        while (true) {
            System.out.print("  Ingrese los IDs separados por coma (ej: 1,3,5): ");
            String entrada = scanner.nextLine().trim();
            try {
                return Arrays.stream(entrada.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Formato invalido. Use numeros separados por coma.");
            }
        }
    }

    /** Imprime una lista de usuarios como tabla en consola. */
    private static void imprimirTabla(List<UserDTO> lista) {
        System.out.println();
        System.out.println(UserDTO.separador());
        System.out.println(UserDTO.encabezado());
        System.out.println(UserDTO.separador());
        if (lista.isEmpty()) {
            System.out.println("  (Sin resultados)");
        } else {
            for (UserDTO u : lista) {
                System.out.println(u);
            }
        }
        System.out.println(UserDTO.separador());
        System.out.println("  Total: " + lista.size() + " usuario(s).");
        System.out.println();
    }
}
