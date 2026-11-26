package mx.tecnm.restaurante;

import java.sql.Connection;
import java.sql.SQLException;
import mx.tecnm.restaurante.config.ConexionDB;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = ConexionDB.getConnection()) {
            System.out.println("Conexión realizada correctamente");
            System.out.println("Base de datos: " + connection.getCatalog());
            System.out.println("Usuario conectado: " + connection.getMetaData().getUserName());
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}