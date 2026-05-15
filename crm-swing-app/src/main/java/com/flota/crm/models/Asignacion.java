package com.flota.crm.models;

import java.sql.Timestamp;

public class Asignacion {
    private int id;
    private int conductorId;
    private int vehiculoId;
    private Timestamp fechaAsignacion;
    private Timestamp fechaFinalizacion;
    private String estado;
    
    // Virtual fields for display
    private String conductorNombre;
    private String vehiculoPlaca;

    public Asignacion() {}

    public Asignacion(int id, int conductorId, int vehiculoId, Timestamp fechaAsignacion, Timestamp fechaFinalizacion, String estado) {
        this.id = id;
        this.conductorId = conductorId;
        this.vehiculoId = vehiculoId;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaFinalizacion = fechaFinalizacion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getConductorId() { return conductorId; }
    public void setConductorId(int conductorId) { this.conductorId = conductorId; }
    public int getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(int vehiculoId) { this.vehiculoId = vehiculoId; }
    public Timestamp getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(Timestamp fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public Timestamp getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(Timestamp fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getConductorNombre() { return conductorNombre; }
    public void setConductorNombre(String conductorNombre) { this.conductorNombre = conductorNombre; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
}
