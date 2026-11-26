package mx.tecnm.restaurante.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private ConexionDB() {
        // Evita instanciar la clase utilitaria
    }

    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new SQLException(
                "Faltan variables de entorno de conexión. Define DB_URL, DB_USER y DB_PASSWORD."
            );
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}