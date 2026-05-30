package com.flota.crm.dao;

import com.flota.crm.config.DatabaseConnection;
import com.flota.crm.models.Conductor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConductorDAO {

    public List<Conductor> obtenerTodos() {
        List<Conductor> lista = new ArrayList<>();
        String sql = "SELECT * FROM conductores WHERE activo = true ORDER BY id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                Conductor c = new Conductor(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("cedula"),
                    rs.getString("telefono"),
                    rs.getString("licencia"),
                    rs.getBoolean("activo")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Conductor conductor) {
        String sql = "INSERT INTO conductores (nombre, cedula, telefono, licencia, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, conductor.getNombre());
            stmt.setString(2, conductor.getCedula());
            stmt.setString(3, conductor.getTelefono());
            stmt.setString(4, conductor.getLicencia());
            stmt.setBoolean(5, true);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Conductor conductor) {
        String sql = "UPDATE conductores SET nombre = ?, cedula = ?, telefono = ?, licencia = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, conductor.getNombre());
            stmt.setString(2, conductor.getCedula());
            stmt.setString(3, conductor.getTelefono());
            stmt.setString(4, conductor.getLicencia());
            stmt.setInt(5, conductor.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE conductores SET activo = false WHERE id = ?";
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
