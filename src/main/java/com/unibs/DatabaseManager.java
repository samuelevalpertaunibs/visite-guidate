package com.unibs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static Connection connection = null;
    private static String url;
    private static String port;
    private static String name;
    private static String user;
    private static String pass;

    public static Connection getConnection() throws SQLException {
        if (url == null && port == null && name == null && user == null && pass == null) {
            url = System.getenv("DB_URL");
            port = System.getenv("DB_PORT");
            name = System.getenv("DB_NAME");
            user = System.getenv("DB_USER");
            pass = System.getenv("DB_PASS");

            if (url == null || port == null || name == null || user == null || pass == null) {
                throw new IllegalStateException("Variabili d'ambiente per il database mancanti!\n" +
                        "Assicurati di aver impostato DB_URL, DB_PORT, DB_NAME, DB_USER e DB_PASS.");
            }
        }

        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/visite_guidate";

            try {
                connection = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                throw new SQLException("Impossibile comunicare con il database");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
