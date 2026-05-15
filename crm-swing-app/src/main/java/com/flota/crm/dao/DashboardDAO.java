package com.flota.crm.dao;

import com.flota.crm.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DashboardDAO {

    public double getTotalMantenimientoCostos() {
        double total = 0;
        String sql = "SELECT SUM(costo) as total FROM mantenimientos";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getCantidadAsignacionesActivas() {
        int count = 0;
        String sql = "SELECT COUNT(*) as count FROM asignaciones WHERE estado = 'ACTIVA'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public Map<String, Integer> getEstadoVehiculosDistribucion() {
        Map<String, Integer> distribucion = new HashMap<>();
        String sql = "SELECT estado, COUNT(*) as cantidad FROM vehiculos GROUP BY estado";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribucion.put(rs.getString("estado"), rs.getInt("cantidad"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribucion;
    }
}
