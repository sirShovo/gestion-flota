package com.flota.crm.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Modify these constants based on the user's PostgreSQL setup
    private static final String URL = "jdbc:postgresql://localhost:5432/flota_crm_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Demonologie2025++"; // Defaulting to admin or postgres

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
