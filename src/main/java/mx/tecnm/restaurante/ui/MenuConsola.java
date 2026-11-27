package mx.tecnm.restaurante.ui;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import mx.tecnm.restaurante.dao.ClienteDAO;
import mx.tecnm.restaurante.dao.ProductoDAO;
import mx.tecnm.restaurante.model.Cliente;
import mx.tecnm.restaurante.model.DetallePedido;
import mx.tecnm.restaurante.model.Pedido;
import mx.tecnm.restaurante.model.Producto;
import mx.tecnm.restaurante.service.PedidoService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuConsola {

    private final Scanner scanner = new Scanner(System.in);
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    // 1. Aquí reemplazamos PedidoDAO por PedidoService
    private final PedidoService pedidoService = new PedidoService();
    private static final Logger LOGGER = Logger.getLogger(MenuConsola.class.getName());

    public void iniciar() {
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Selecciona una opción: ");

            try {
                switch (opcion) {
                    case 1 -> listarProductos();
                    case 2 -> listarProductosDisponibles();
                    case 3 -> buscarProductoPorId();
                    case 4 -> registrarCliente();
                    case 5 -> consultarPedidos();
                    case 6 -> crearPedido();
                    case 7 -> consultarTotalPedido();
                    case 8 -> cambiarEstadoPedido();
                    case 9 -> consultarHistorial();
                    case 0 -> System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    default -> System.out.println("Opción no válida. Intenta de nuevo.");
                }
            } catch (SQLException e) {
                // 1. Registro técnico interno (no se expone como respuesta amigable)
                LOGGER.log(Level.SEVERE, "Fallo SQL [" + e.getSQLState() + "]: " + e.getMessage(), e);

                // 2. Mensaje seguro y comprensible para el usuario final
                if ("23505".equals(e.getSQLState())) {
                    System.out.println("El dato ingresado ya existe en el sistema. Verifica e intenta de nuevo.");
                } else if ("23514".equals(e.getSQLState())) {
                    System.out.println("Uno de los valores ingresados no es válido (revisa cantidades o montos).");
                } else if ("P0001".equals(e.getSQLState())) {
                    System.out.println("Operación no permitida por las reglas del restaurante.");
                } else {
                    System.out.println("Ocurrió un problema al procesar tu solicitud. Por favor intenta más tarde.");
                }
            }
        }

        scanner.close();
    }

    private void mostrarMenu() {
        System.out.println("\n=================================");
        System.out.println("SISTEMA RESTAURANTE");
        System.out.println("=================================");
        System.out.println("1. Listar productos");
        System.out.println("2. Listar productos disponibles");
        System.out.println("3. Buscar producto por ID");
        System.out.println("4. Registrar cliente");
        System.out.println("5. Consultar pedidos");
        System.out.println("6. Crear pedido");
        System.out.println("7. Consultar total de pedido");
        System.out.println("8. Cambiar estado de pedido");
        System.out.println("9. Consultar historial");
        System.out.println("0. Salir");
        System.out.println("=================================");
    }

    private void listarProductos() throws SQLException {
        List<Producto> productos = productoDAO.obtenerTodos();
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        productos.forEach(System.out::println);
    }

    private void listarProductosDisponibles() throws SQLException {
        List<Producto> disponibles = productoDAO.obtenerDisponibles();
        System.out.println("\n--- PRODUCTOS DISPONIBLES ---");
        disponibles.forEach(System.out::println);
    }

    private void buscarProductoPorId() throws SQLException {
        int id = leerEntero("Ingresa el ID del producto: ");
        Producto producto = productoDAO.buscarPorId(id);
        if (producto != null) {
            System.out.println("Encontrado -> " + producto);
        } else {
            System.out.println("No existe un producto con ID " + id);
        }
    }

    private void registrarCliente() throws SQLException {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine().trim();
        System.out.print("Correo electrónico: ");
        String email = scanner.nextLine().trim();

        Cliente nuevo = new Cliente(nombre, apellido, telefono, email);
        // Deja aquí el nombre exacto de tu método en ClienteDAO (ej. insertar, crear,
        // etc.)
        clienteDAO.insertar(nuevo);
        System.out.println("Cliente registrado con éxito.");
    }

    // 2. Actualizado a pedidoService.listarPedidos()
    private void consultarPedidos() throws SQLException {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        System.out.println("\n--- PEDIDOS REGISTRADOS ---");
        pedidos.forEach(System.out::println);
    }

    // 3. Actualizado a pedidoService.crearPedidoCompleto(...)
    private void crearPedido() throws SQLException {
        int idCliente = leerEntero("ID del cliente: ");
        List<DetallePedido> detalles = new ArrayList<>();

        boolean agregarMas = true;
        while (agregarMas) {
            int idProducto = leerEntero("ID del producto: ");
            Producto prod = productoDAO.buscarPorId(idProducto);

            if (prod == null) {
                System.out.println("Producto no encontrado. Intenta con otro ID.");
                continue;
            }

            int cantidad = leerEntero("Cantidad para '" + prod.getNombre() + "' ($" + prod.getPrecio() + "): ");
            detalles.add(new DetallePedido(idProducto, cantidad, prod.getPrecio()));

            System.out.print("¿Agregar otro producto? (s/n): ");
            String resp = scanner.nextLine().trim();
            agregarMas = resp.equalsIgnoreCase("s");
        }

        int idPedido = pedidoService.crearPedidoCompleto(idCliente, detalles);
        System.out.println("Pedido #" + idPedido + " creado correctamente con " + detalles.size() + " producto(s).");
    }

    // 4. Actualizado a pedidoService.calcularTotalPedido(...)
    private void consultarTotalPedido() throws SQLException {
        int idPedido = leerEntero("Ingresa el ID del pedido: ");
        BigDecimal total = pedidoService.calcularTotalPedido(idPedido);
        System.out.println("Total del Pedido #" + idPedido + ": $" + total);
    }

    // 5. Actualizado a pedidoService.actualizarEstadoPedido(...)
    private void cambiarEstadoPedido() throws SQLException {
        int idPedido = leerEntero("Ingresa el ID del pedido: ");
        System.out.print("Nuevo estado (PENDIENTE / EN_PREPARACION / ENTREGADO / CANCELADO): ");
        String nuevoEstado = scanner.nextLine().trim().toUpperCase();

        pedidoService.actualizarEstadoPedido(idPedido, nuevoEstado);
        System.out.println("Estado del Pedido #" + idPedido + " actualizado a '" + nuevoEstado + "'.");
    }

    // 6. Actualizado a pedidoService.obtenerHistorial(...)
    private void consultarHistorial() throws SQLException {
        int idPedido = leerEntero("Ingresa el ID del pedido: ");
        List<String> historial = pedidoService.obtenerHistorial(idPedido);

        if (historial.isEmpty()) {
            System.out.println("No hay registros de cambios de estado para el Pedido #" + idPedido);
        } else {
            System.out.println("\n--- HISTORIAL DEL PEDIDO #" + idPedido + " ---");
            historial.forEach(System.out::println);
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número entero válido.");
            }
        }
    }
}