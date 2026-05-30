package com.flota.crm.dao;

import com.flota.crm.config.DatabaseConnection;
import com.flota.crm.models.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (email, password_hash, activo, created_at, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, usuario.getEmail());
            // Hashear la contraseña antes de guardar
            String hashed = BCrypt.hashpw(usuario.getPasswordHash(), BCrypt.gensalt());
            pstmt.setString(2, hashed);
            pstmt.setBoolean(3, usuario.isActivo());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setActivo(rs.getBoolean("activo"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                u.setUpdatedAt(rs.getTimestamp("updated_at"));
                usuarios.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public boolean actualizarContrasena(int id, String nuevaContrasena) {
        String sql = "UPDATE usuarios SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            String hashed = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());
            pstmt.setString(1, hashed);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int id, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setBoolean(1, activo);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean desactivar(int id) {
        return cambiarEstado(id, false);
    }

    public Usuario autenticar(String email, String passwordPlan) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                boolean activo = rs.getBoolean("activo");
                if (!activo) {
                    throw new RuntimeException("INACTIVE"); // Usuario inactivo
                }
                
                String storedHash = rs.getString("password_hash");
                if (BCrypt.checkpw(passwordPlan, storedHash)) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setEmail(rs.getString("email"));
                    u.setPasswordHash(storedHash);
                    u.setActivo(activo);
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    u.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de BD en autenticación: " + e.getMessage());
        } catch (RuntimeException e) {
            if ("INACTIVE".equals(e.getMessage())) {
                throw e; // Propagar excepción específica
            }
        }
        return null; // Credenciales inválidas
    }

    public void verificarYCargarUsuarioPorDefecto() {
        String countSql = "SELECT COUNT(*) FROM usuarios";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
             
            if (rs.next() && rs.getInt(1) == 0) {
                Usuario admin = new Usuario();
                admin.setEmail("bornacelly99@gmail.com");
                admin.setPasswordHash("admin123"); // Será hasheado en insertar()
                admin.setActivo(true);
                
                if (insertar(admin)) {
                    System.out.println("Usuario administrador por defecto creado exitosamente.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar usuarios por defecto: " + e.getMessage());
        }
    }
}
