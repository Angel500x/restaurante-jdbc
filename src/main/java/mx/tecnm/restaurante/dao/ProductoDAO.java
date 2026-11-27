package mx.tecnm.restaurante.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.tecnm.restaurante.config.ConexionDB;
import mx.tecnm.restaurante.model.Producto;

public class ProductoDAO {

    // Flujo: Java -> SQL -> PostgreSQL -> ResultSet -> Producto
    public List<Producto> obtenerTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT
                    id_producto,
                    nombre,
                    precio,
                    disponible
                FROM restaurante.producto
                ORDER BY nombre
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getBoolean("disponible")
                );
                productos.add(p);
            }
        }

        return productos;
    }

    public List<Producto> obtenerDisponibles() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT
                    id_producto,
                    nombre,
                    precio,
                    disponible
                FROM restaurante.producto
                WHERE disponible = true
                ORDER BY nombre
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("precio"),
                        rs.getBoolean("disponible")
                ));
            }
        }

        return productos;
    }

    public Producto buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT
                    id_producto,
                    nombre,
                    precio,
                    disponible
                FROM restaurante.producto
                WHERE id_producto = ?
                """;

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Producto(
                            resultSet.getInt("id_producto"),
                            resultSet.getString("nombre"),
                            resultSet.getBigDecimal("precio"),
                            resultSet.getBoolean("disponible")
                    );
                }
            }
        }

        return null;
    }

    public boolean actualizarDisponibilidad(int idProducto, boolean disponible) throws SQLException {
        String sql = """
                UPDATE restaurante.producto
                SET disponible = ?
                WHERE id_producto = ?
                """;

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, disponible);
            statement.setInt(2, idProducto);

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    // Consulta parametrizada con PreparedStatement (prevención de SQL Injection)
    public List<Producto> buscarDisponiblesPorPrecioMaximo(BigDecimal precioMax) throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT
                    id_producto,
                    nombre,
                    precio,
                    disponible
                FROM restaurante.producto
                WHERE disponible = true
                  AND precio <= ?
                ORDER BY precio ASC, nombre ASC
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, precioMax);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("precio"),
                            rs.getBoolean("disponible")
                    ));
                }
            }
        }

        return productos;
    }
}