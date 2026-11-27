package mx.tecnm.restaurante;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import mx.tecnm.restaurante.config.ConexionDB;
import mx.tecnm.restaurante.dao.ClienteDAO;
import mx.tecnm.restaurante.dao.PedidoDAO;
import mx.tecnm.restaurante.dao.ProductoDAO;
import mx.tecnm.restaurante.model.Cliente;
import mx.tecnm.restaurante.model.DetallePedido;
import mx.tecnm.restaurante.model.Pedido;
import mx.tecnm.restaurante.model.Producto;
import mx.tecnm.restaurante.ui.MenuConsola;

public class Main {

    public static void main(String[] args) {
        MenuConsola menu = new MenuConsola();
        menu.iniciar();
         
         /* 
        try (Connection connection = ConexionDB.getConnection()) {
            System.out.println("Conexión realizada correctamente");
            System.out.println("Base de datos: " + connection.getCatalog());
            System.out.println("Usuario conectado: " + connection.getMetaData().getUserName());
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
            

        ProductoDAO productoDAO = new ProductoDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        PedidoDAO pedidoDAO = new PedidoDAO();

        */
        
        /* 
        try {
            List<Producto> productos = productoDAO.obtenerTodos();
            System.out.println("=== CATÁLOGO DE PRODUCTOS (" + productos.size() + " registros) ===");
            for (Producto p : productos) {
                System.out.println(p);
            }
        } catch (SQLException e) {
            System.out.println("Error en ProductoDAO: " + e.getMessage());
        }
          */  

        /* 
        try {
            List<Producto> disponibles = productoDAO.obtenerDisponibles();
            System.out.println("=== PRODUCTOS DISPONIBLES (" + disponibles.size() + " registros) ===");
            for (Producto p : disponibles) {
                System.out.println(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener productos disponibles: " + e.getMessage());
        }
            */

        /* 
        try {
            int idBuscado = 999;
            Producto producto = productoDAO.buscarPorId(idBuscado);

            if (producto != null) {
                System.out.println("Producto encontrado: " + producto);
            } else {
                System.out.println("No se encontró ningún producto con ID: " + idBuscado);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar producto por ID: " + e.getMessage());
        }
            */

        /* 
        try {
            Cliente nuevoCliente = new Cliente(
                    "Carlos",
                    "Mendoza",
                    "9511234567",
                    "carlos.mendoza@correo.com"
            );

            Cliente clienteRegistrado = clienteDAO.insertar(nuevoCliente);
            System.out.println("Cliente registrado con éxito:");
            System.out.println(clienteRegistrado);

        } catch (SQLException e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
        }
            */

        /* 
        String correoRepetido = "duplicado.prueba@correo.com";

        Cliente cliente1 = new Cliente("Ana", "Lopez", "9511112233", correoRepetido);
        Cliente cliente2 = new Cliente("Luis", "Perez", "9514445566", correoRepetido);

        registrarConValidacion(clienteDAO, cliente1);
        registrarConValidacion(clienteDAO, cliente2);
    }

    private static void registrarConValidacion(ClienteDAO dao, Cliente cliente) {
        try {
            Cliente guardado = dao.insertar(cliente);
            System.out.println("Registro exitoso -> " + guardado);
        } catch (SQLException e) {
            // Código estándar SQLState "23505" = violación de restricción UNIQUE en PostgreSQL
            if ("23505".equals(e.getSQLState())) {
                System.out.println("Aviso: El correo '" + cliente.getCorreo()
                        + "' ya está registrado. Por favor utiliza otro correo electrónico.");
            } else {
                System.out.println("Error inesperado en la base de datos: " + e.getMessage());
            }
        }
            */

        /*
        int idProducto = 1;

        try {
            System.out.println("Estado inicial: " + productoDAO.buscarPorId(idProducto));

            boolean actualizado = productoDAO.actualizarDisponibilidad(idProducto, false);
            System.out.println("¿Registro actualizado?: " + actualizado);
            System.out.println("Después del UPDATE: " + productoDAO.buscarPorId(idProducto));

            // Restauramos su disponibilidad a true para dejar el catálogo intacto
            productoDAO.actualizarDisponibilidad(idProducto, true);
            System.out.println("Estado restaurado: " + productoDAO.buscarPorId(idProducto));

        } catch (SQLException e) {
            System.out.println("Error al actualizar disponibilidad: " + e.getMessage());
        }
            */

         /* 
        try {
            List<Pedido> pedidos = pedidoDAO.obtenerPedidos();
            System.out.println("=== LISTADO DE PEDIDOS (" + pedidos.size() + " registros) ===");
            for (Pedido p : pedidos) {
                System.out.println(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar pedidos: " + e.getMessage());
        }
         */   

        /* 
        int idPedido = 1;

        try {
            BigDecimal total = pedidoDAO.obtenerTotal(idPedido);
            System.out.println("Total del Pedido #" + idPedido + ": $" + total);
        } catch (SQLException e) {
            System.out.println("Error al calcular el total del pedido: " + e.getMessage());
        }
            */

         /* 
        int idPedido = 5;
        String nuevoEstado = "LISTO"; // Ajusta a un estado válido según tu restricción CHECK en BD

        try {
            pedidoDAO.cambiarEstado(idPedido, nuevoEstado);
            System.out.println("Procedimiento ejecutado: Pedido #" + idPedido + " actualizado a '" + nuevoEstado + "'.");

            List<Pedido> pedidos = pedidoDAO.obtenerPedidos();
            for (Pedido p : pedidos) {
                if (p.getIdPedido() == idPedido) {
                    System.out.println("Estado verificado en BD -> " + p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado del pedido: " + e.getMessage());
        }
            */
            
        /* 
        // Cambia este ID por el pedido que estaba en PENDIENTE o EN_PREPARACION
        int idPedido = 5;
        String nuevoEstado = "ENTREGADO";

        try {
            System.out.println("1. Llamando al procedimiento cambiar_estado_pedido desde Java...");
            pedidoDAO.cambiarEstado(idPedido, nuevoEstado);
            System.out.println("2. Estado actualizado correctamente.\n");

            System.out.println("3. Consultando historial_estado_pedido (generado por el Trigger):");
            List<String> historial = pedidoDAO.obtenerHistorialEstado(idPedido);
            for (String entrada : historial) {
                System.out.println("   " + entrada);
            }

        } catch (SQLException e) {
            System.out.println("Error en el flujo: " + e.getMessage());
        }
            */

        /* 
        // ID de un pedido que ya tenga estado 'ENTREGADO' en la BD
        int idPedidoEntregado = 5;
        String estadoInvalido = "PENDIENTE";

        System.out.println("Intentando cambiar Pedido #" + idPedidoEntregado
                + " de ENTREGADO a " + estadoInvalido + "...");

        try {
            pedidoDAO.cambiarEstado(idPedidoEntregado, estadoInvalido);
            System.out.println("Estado actualizado correctamente.");
        } catch (SQLException e) {
            // PostgreSQL usa el SQLState "P0001" por defecto para RAISE EXCEPTION definido por el usuario
            if ("P0001".equals(e.getSQLState())) {
                System.out.println("Operación rechazada por regla de negocio en PostgreSQL:");
                System.out.println("-> " + e.getMessage());
            } else {
                System.out.println("Error de base de datos [" + e.getSQLState() + "]: " + e.getMessage());
            }
        }
            */

        /* 
        int idCliente = 1; // Asegúrate de que exista en restaurante.cliente
        List<DetallePedido> detalles = List.of(
                new DetallePedido(1, 2, new BigDecimal("85.00")), // 2 unidades del producto 1
                new DetallePedido(2, 1, new BigDecimal("45.00"))  // 1 unidad del producto 2
        );

        try {
            int nuevoIdPedido = pedidoDAO.registrarPedido(idCliente, detalles);
            System.out.println("Pedido registrado con éxito. ID generado: #" + nuevoIdPedido);

            BigDecimal total = pedidoDAO.obtenerTotal(nuevoIdPedido);
            System.out.println("Total calculado por PostgreSQL: $" + total);

        } catch (SQLException e) {
            System.out.println("Error al registrar el pedido: " + e.getMessage());
        }
            */

        /* 
        int idCliente = 1;
        List<DetallePedido> detallesInvalidos = List.of(
                new DetallePedido(1, 2, new BigDecimal("85.00")), // Válido: se ejecuta primero
                new DetallePedido(2, 0, new BigDecimal("45.00"))  // Inválido: viola CHECK (cantidad > 0)
        );

        System.out.println("=== PRUEBA DE TRANSACCIÓN CON ERROR (ROLLBACK) ===");
        try {
            int idGenerado = pedidoDAO.registrarPedido(idCliente, detallesInvalidos);
            System.out.println("Pedido registrado: #" + idGenerado);
        } catch (SQLException e) {
            System.out.println("Fallo detectado en el segundo detalle -> SQLState: " + e.getSQLState());
            System.out.println("Motivo PostgreSQL: " + e.getMessage());
        }

        System.out.println("\n=== VERIFICANDO QUE NO SE GUARDÓ EL PEDIDO HUÉRFANO ===");
        try {
            List<Pedido> pedidosActuales = pedidoDAO.obtenerPedidos();
            System.out.println("Total de pedidos en BD tras el rollback: " + pedidosActuales.size());
            for (Pedido p : pedidosActuales) {
                System.out.println(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar pedidos: " + e.getMessage());
        }
            */
    }
}