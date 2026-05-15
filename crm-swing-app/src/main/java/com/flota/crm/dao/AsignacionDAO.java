package com.flota.crm.dao;

import com.flota.crm.config.DatabaseConnection;
import com.flota.crm.models.Asignacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {

    public List<Asignacion> obtenerTodas() {
        List<Asignacion> lista = new ArrayList<>();
        // Query con JOIN para obtener los nombres amigables en vez de solo IDs
        String sql = "SELECT a.*, c.nombre as conductor_nombre, v.placa as vehiculo_placa " +
                     "FROM asignaciones a " +
                     "JOIN conductores c ON a.conductor_id = c.id " +
                     "JOIN vehiculos v ON a.vehiculo_id = v.id " +
                     "ORDER BY a.id DESC";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                Asignacion a = new Asignacion(
                    rs.getInt("id"),
                    rs.getInt("conductor_id"),
                    rs.getInt("vehiculo_id"),
                    rs.getTimestamp("fecha_asignacion"),
                    rs.getTimestamp("fecha_finalizacion"),
                    rs.getString("estado")
                );
                a.setConductorNombre(rs.getString("conductor_nombre"));
                a.setVehiculoPlaca(rs.getString("vehiculo_placa"));
                lista.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Asignacion asignacion) {
        String sqlInsert = "INSERT INTO asignaciones (conductor_id, vehiculo_id, estado) VALUES (?, ?, 'ACTIVA')";
        String sqlUpdateVehiculo = "UPDATE vehiculos SET estado = 'EN_USO' WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Transacción para asegurar consistencia
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
                stmt.setInt(1, asignacion.getConductorId());
                stmt.setInt(2, asignacion.getVehiculoId());
                stmt.executeUpdate();
            }
            
            try (PreparedStatement stmt2 = conn.prepareStatement(sqlUpdateVehiculo)) {
                stmt2.setInt(1, asignacion.getVehiculoId());
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
    
    public boolean finalizar(int idAsignacion, int idVehiculo) {
        String sqlUpdateAsignacion = "UPDATE asignaciones SET estado = 'FINALIZADA', fecha_finalizacion = CURRENT_TIMESTAMP WHERE id = ?";
        String sqlUpdateVehiculo = "UPDATE vehiculos SET estado = 'DISPONIBLE' WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateAsignacion)) {
                stmt.setInt(1, idAsignacion);
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
}
