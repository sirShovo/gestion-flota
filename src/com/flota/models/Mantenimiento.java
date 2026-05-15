package com.flota.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un registro de mantenimiento (reparación o revisión) aplicado a un vehículo.
 */
public class Mantenimiento {
    /** Identificador único del registro de mantenimiento. */
    private int id;
    
    /** Vehículo al que se le aplica este mantenimiento. */
    private Vehiculo vehiculo;
    
    /** Tipo de mantenimiento (preventivo o correctivo). */
    private TipoMantenimiento tipo;
    
    /** Texto detallando el trabajo realizado o problema a arreglar. */
    private String descripcion;
    
    /** Costo monetario estimado o real del mantenimiento. */
    private double costo;
    
    /** Fecha y hora en la que se registró el inicio del mantenimiento. */
    private LocalDateTime fecha;
    
    /** Estado del proceso de mantenimiento (EN_PROGRESO o FINALIZADO). */
    private EstadoMantenimiento estado;

    /**
     * Construye un nuevo registro de mantenimiento.
     * La fecha del registro es asignada al momento actual y el estado inicial es EN_PROGRESO.
     *
     * @param id          Identificador único del mantenimiento.
     * @param vehiculo    Vehículo al que se le realiza el mantenimiento.
     * @param tipo        Clasificación del tipo de mantenimiento.
     * @param descripcion Detalles de las acciones realizadas o por realizar.
     * @param costo       Costo monetario del mantenimiento.
     */
    public Mantenimiento(int id, Vehiculo vehiculo, TipoMantenimiento tipo, String descripcion, double costo) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.costo = costo;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoMantenimiento.EN_PROGRESO;
    }

    public int getId() { return id; }
    
    public Vehiculo getVehiculo() { return vehiculo; }
    
    public TipoMantenimiento getTipo() { return tipo; }
    
    public String getDescripcion() { return descripcion; }
    
    public double getCosto() { return costo; }
    
    public LocalDateTime getFecha() { return fecha; }
    
    public EstadoMantenimiento getEstado() { return estado; }
    public void setEstado(EstadoMantenimiento estado) { this.estado = estado; }

    /**
     * Devuelve una cadena de texto con los detalles del mantenimiento formateados.
     *
     * @return Resumen en texto del mantenimiento.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Mantenimiento[id=%d, vehiculo='%s', tipo=%s, descripcion='%s', costo=%.2f, fecha=%s, estado=%s]",
                id, vehiculo.getPlaca(), tipo, descripcion, costo, fecha.format(formatter), estado);
    }
}
