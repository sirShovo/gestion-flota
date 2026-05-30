package com.flota.crm.dao;

import com.flota.crm.config.DatabaseConnection;
import com.flota.crm.models.Mantenimiento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAO {

    public List<Mantenimiento> obtenerTodos() {
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT m.*, v.placa as vehiculo_placa " +
                     "FROM mantenimientos m " +
                     "JOIN vehiculos v ON m.vehiculo_id = v.id " +
                     "ORDER BY m.id DESC";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                Mantenimiento m = new Mantenimiento(
                    rs.getInt("id"),
                    rs.getInt("vehiculo_id"),
                    rs.getString("tipo"),
                    rs.getString("descripcion"),
                    rs.getDouble("costo"),
                    rs.getTimestamp("fecha"),
                    rs.getString("estado")
                );
                m.setVehiculoPlaca(rs.getString("vehiculo_placa"));
                lista.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Mantenimiento mantenimiento) {
        String sqlInsert = "INSERT INTO mantenimientos (vehiculo_id, tipo, descripcion, costo, estado) VALUES (?, ?, ?, ?, 'EN_PROGRESO')";
        String sqlUpdateVehiculo = "UPDATE vehiculos SET estado = 'MANTENIMIENTO' WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                stmt.setInt(1, mantenimiento.getVehiculoId());
                stmt.setString(2, mantenimiento.getTipo());
                stmt.setString(3, mantenimiento.getDescripcion());
                stmt.setDouble(4, mantenimiento.getCosto());
                stmt.executeUpdate();
            }
            
            try (PreparedStatement stmt2 = conn.prepareStatement(sqlUpdateVehiculo)) {
                stmt2.setInt(1, mantenimiento.getVehiculoId());
                stmt2.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
    
    public boolean finalizar(int idMantenimiento, int idVehiculo) {
        String sqlUpdateMantenimiento = "UPDATE mantenimientos SET estado = 'FINALIZADO' WHERE id = ?";
        String sqlUpdateVehiculo = "UPDATE vehiculos SET estado = 'DISPONIBLE' WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateMantenimiento)) {
                stmt.setInt(1, idMantenimiento);
                stmt.executeUpdate();
            }
            
            try (PreparedStatement stmt2 = conn.prepareStatement(sqlUpdateVehiculo)) {
                stmt2.setInt(1, idVehiculo);
                stmt2.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public boolean actualizar(Mantenimiento mantenimiento) {
        String sql = "UPDATE mantenimientos SET tipo = ?, descripcion = ?, costo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, mantenimiento.getTipo());
            stmt.setString(2, mantenimiento.getDescripcion());
            stmt.setDouble(3, mantenimiento.getCosto());
            stmt.setInt(4, mantenimiento.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE mantenimientos SET estado = 'CANCELADO' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
