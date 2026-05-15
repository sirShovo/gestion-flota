package com.flota.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * La clase Asignacion representa el vínculo temporal entre un Conductor y un Vehiculo.
 * Registra cuándo se entrega un vehículo a un conductor y cuándo es devuelto.
 */
public class Asignacion {
    /** Identificador único de la asignación. */
    private int id;
    
    /** Conductor al que se le asignó el vehículo. */
    private Conductor conductor;
    
    /** Vehículo que fue asignado. */
    private Vehiculo vehiculo;
    
    /** Fecha y hora exacta en la que se inició la asignación. */
    private LocalDateTime fechaAsignacion;
    
    /** Fecha y hora exacta en la que finalizó la asignación (null si sigue activa). */
    private LocalDateTime fechaFinalizacion;
    
    /** Estado actual de la asignación (ACTIVA o FINALIZADA). */
    private EstadoAsignacion estado;

    /**
     * Crea una nueva asignación de un vehículo a un conductor.
     * La fecha de asignación se toma como el momento actual y el estado inicial es ACTIVA.
     *
     * @param id        El identificador único de la asignación.
     * @param conductor El conductor que recibe el vehículo.
     * @param vehiculo  El vehículo que está siendo asignado.
     */
    public Asignacion(int id, Conductor conductor, Vehiculo vehiculo) {
        this.id = id;
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.fechaAsignacion = LocalDateTime.now();
        this.estado = EstadoAsignacion.ACTIVA;
    }

    public int getId() { return id; }
    
    public Conductor getConductor() { return conductor; }
    
    public Vehiculo getVehiculo() { return vehiculo; }
    
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    
    public LocalDateTime getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
    
    public EstadoAsignacion getEstado() { return estado; }
    public void setEstado(EstadoAsignacion estado) { this.estado = estado; }

    /**
     * Devuelve una cadena de texto con los detalles de la asignación formatedos.
     * Muestra las fechas en formato yyyy-MM-dd HH:mm.
     *
     * @return Representación en texto de la asignación.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String finStr = fechaFinalizacion != null ? fechaFinalizacion.format(formatter) : "N/A";
        return String.format("Asignacion[id=%d, conductor='%s', vehiculo='%s', inicio=%s, fin=%s, estado=%s]",
                id, conductor.getNombre(), vehiculo.getPlaca(), fechaAsignacion.format(formatter), finStr, estado);
    }
}
