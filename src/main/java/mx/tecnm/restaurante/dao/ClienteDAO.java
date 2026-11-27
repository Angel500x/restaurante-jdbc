package mx.tecnm.restaurante.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mx.tecnm.restaurante.config.ConexionDB;
import mx.tecnm.restaurante.model.Cliente;

public class ClienteDAO {

    public Cliente insertar(Cliente cliente) throws SQLException {
        String sql = """
                INSERT INTO restaurante.cliente
                    (nombre, apellido, telefono, correo)
                VALUES (?, ?, ?, ?)
                RETURNING id_cliente, fecha_registro
                """;

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getApellido());
            statement.setString(3, cliente.getTelefono());
            statement.setString(4, cliente.getCorreo());

            // Usamos executeQuery() porque RETURNING devuelve una fila de resultados
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cliente.setIdCliente(resultSet.getInt("id_cliente"));
                    Date fechaSql = resultSet.getDate("fecha_registro");
                    if (fechaSql != null) {
                        cliente.setFechaRegistro(fechaSql.toLocalDate());
                    }
                }
            }
        }

        return cliente;
    }
}