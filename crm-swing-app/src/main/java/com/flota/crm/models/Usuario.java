package com.flota.crm.models;

public class Usuario {
    private int id;
    private String email;
    private String passwordHash;
    private boolean activo;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;

    public Usuario() {
    }

    public Usuario(int id, String email, String passwordHash, boolean activo) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
