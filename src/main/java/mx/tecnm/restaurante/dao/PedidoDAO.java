package mx.tecnm.restaurante.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mx.tecnm.restaurante.config.ConexionDB;
import mx.tecnm.restaurante.model.DetallePedido;
import mx.tecnm.restaurante.model.Pedido;

public class PedidoDAO {

    public List<Pedido> obtenerPedidos() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = """
                                        SELECT
                    p.id_pedido,
                    c.nombre || ' ' || c.apellido AS cliente,
                    p.fecha_hora AS fecha,
                    p.estado,
                    COUNT(dp.id_producto) AS cantidad_productos,
                    COALESCE(SUM(dp.cantidad * dp.precio_unitario), 0) AS total_pedido
                FROM restaurante.pedido p
                INNER JOIN restaurante.cliente c ON p.id_cliente = c.id_cliente
                LEFT JOIN restaurante.detalle_pedido dp ON p.id_pedido = dp.id_pedido
                GROUP BY p.id_pedido, c.nombre, c.apellido, p.fecha_hora, p.estado
                ORDER BY p.id_pedido DESC;
                                """;

        try (Connection connection = ConexionDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Timestamp ts = resultSet.getTimestamp("fecha");
                LocalDateTime fecha = (ts != null) ? ts.toLocalDateTime() : null;

                Pedido pedido = new Pedido(
                        resultSet.getInt("id_pedido"),
                        resultSet.getString("cliente"),
                        fecha,
                        resultSet.getString("estado"));
                pedidos.add(pedido);
            }
        }

        return pedidos;
    }

    public BigDecimal obtenerTotal(int idPedido) throws SQLException {
        String sql = "SELECT restaurante.calcular_total_pedido(?)";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idPedido);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal total = resultSet.getBigDecimal(1);
                    return (total != null) ? total : BigDecimal.ZERO;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public void cambiarEstado(int idPedido, String nuevoEstado) throws SQLException {
        String sql = "CALL restaurante.cambiar_estado_pedido(?, ?)";

        try (Connection connection = ConexionDB.getConnection();
             CallableStatement statement = connection.prepareCall(sql)) {

            statement.setInt(1, idPedido);
            statement.setString(2, nuevoEstado);

            statement.execute();
        }
    }

    public List<String> obtenerHistorialEstado(int idPedido) throws SQLException {
        List<String> historial = new ArrayList<>();

        String sql = """
                SELECT *
                FROM restaurante.historial_estado_pedido
                WHERE id_pedido = ?
                ORDER BY fecha_cambio
                """;

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idPedido);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String registro = String.format(
                            "Historial [ID: %d | Pedido #%d | %s -> %s | Fecha: %s]",
                            resultSet.getInt("id_historial"),
                            resultSet.getInt("id_pedido"),
                            resultSet.getString("estado_anterior"),
                            resultSet.getString("estado_nuevo"),
                            resultSet.getTimestamp("fecha_cambio")
                    );
                    historial.add(registro);
                }
            }
        }

        return historial;
    }

    public int registrarPedido(int idCliente, List<DetallePedido> detalles) throws SQLException {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe incluir al menos un producto.");
        }

        String sqlPedido = """
                INSERT INTO restaurante.pedido (id_cliente, estado)
                VALUES (?, 'PENDIENTE')
                RETURNING id_pedido
                """;

        String sqlDetalle = """
                INSERT INTO restaurante.detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario)
                VALUES (?, ?, ?, ?)
                """;

        Connection connection = null;
        int idPedidoGenerado = -1;

        try {
            // 1. Abrir conexión
            connection = ConexionDB.getConnection();

            // 2. Desactivar autoCommit para iniciar la transacción explícita
            connection.setAutoCommit(false);

            // 3. Insertar cabecera del pedido y 4. Recuperar id_pedido
            try (PreparedStatement stmtPedido = connection.prepareStatement(sqlPedido)) {
                stmtPedido.setInt(1, idCliente);

                try (ResultSet rs = stmtPedido.executeQuery()) {
                    if (rs.next()) {
                        idPedidoGenerado = rs.getInt("id_pedido");
                    } else {
                        throw new SQLException("No se pudo recuperar el id_pedido generado.");
                    }
                }
            }
            // 5. Insertar detalles compartiendo la misma conexión y el idPedidoGenerado
            try (PreparedStatement stmtDetalle = connection.prepareStatement(sqlDetalle)) {
                for (DetallePedido detalle : detalles) {
                    stmtDetalle.setInt(1, idPedidoGenerado);
                    stmtDetalle.setInt(2, detalle.getIdProducto());
                    stmtDetalle.setInt(3, detalle.getCantidad());
                    stmtDetalle.setBigDecimal(4, detalle.getPrecioUnitario());
                    stmtDetalle.executeUpdate();
                }
            }
            // 6. Confirmar toda la transacción
            connection.commit();
            return idPedidoGenerado;

        } catch (SQLException e) {
            // 7 y 8. Si alguna instrucción falla, deshacer todos los cambios
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Transacción revertida (ROLLBACK) debido a un error.");
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw e;
        } finally {
            // Restaurar comportamiento por defecto y liberar recursos
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}