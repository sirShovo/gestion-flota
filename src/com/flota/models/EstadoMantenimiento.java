package com.flota.models;

/**
 * Enumeración que representa los estados por los que puede pasar un mantenimiento.
 */
public enum EstadoMantenimiento {
    /**
     * El vehículo se encuentra actualmente en el taller o bajo revisión.
     */
    EN_PROGRESO,

    /**
     * El trabajo de mantenimiento ha concluido y el vehículo está listo para usarse.
     */
    FINALIZADO
}
