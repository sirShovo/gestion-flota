package com.flota.models;

/**
 * Enumeración que define el estado de disponibilidad general de un vehículo.
 */
public enum EstadoVehiculo {
    /**
     * El vehículo está libre y listo para ser asignado.
     */
    DISPONIBLE,

    /**
     * El vehículo ha sido asignado a un conductor y no puede ser usado por otro.
     */
    EN_USO,

    /**
     * El vehículo está en reparaciones o revisión y no puede ser asignado.
     */
    MANTENIMIENTO
}
