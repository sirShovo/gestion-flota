package com.flota.models;

/**
 * Enumeración que representa los posibles estados de una asignación de vehículo.
 */
public enum EstadoAsignacion {
    /**
     * La asignación se encuentra actualmente en curso.
     */
    ACTIVA,

    /**
     * La asignación ha sido terminada y el vehículo ha sido devuelto.
     */
    FINALIZADA
}
