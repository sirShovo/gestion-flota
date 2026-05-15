package com.flota.crm.models;

import java.sql.Timestamp;

public class Mantenimiento {
    private int id;
    private int vehiculoId;
    private String tipo;
    private String descripcion;
    private double costo;
    private Timestamp fecha;
    private String estado;
    
    // Virtual field for display
    private String vehiculoPlaca;

    public Mantenimiento() {}

    public Mantenimiento(int id, int vehiculoId, String tipo, String descripcion, double costo, Timestamp fecha, String estado) {
        this.id = id;
        this.vehiculoId = vehiculoId;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.costo = costo;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(int vehiculoId) { this.vehiculoId = vehiculoId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
}
