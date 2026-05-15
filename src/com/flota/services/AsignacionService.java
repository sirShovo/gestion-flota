package com.flota.services;

import com.flota.models.Asignacion;
import com.flota.models.Conductor;
import com.flota.models.EstadoAsignacion;
import com.flota.models.EstadoVehiculo;
import com.flota.models.Vehiculo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar las asignaciones de vehículos a conductores.
 * Validará reglas de negocio como que un conductor no tenga más de una asignación activa,
 * o que un vehículo no se asigne si está en mantenimiento o uso.
 */
public class AsignacionService {
    /** Lista interna para almacenar los registros de todas las asignaciones hechas. */
    private List<Asignacion> asignaciones;
    
    /** Generador de IDs secuenciales para las nuevas asignaciones. */
    private int nextId;

    /**
     * Construye una nueva instancia del servicio de asignaciones.
     */
    public AsignacionService() {
        this.asignaciones = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Genera una nueva asignación de un vehículo para un conductor específico.
     * También actualiza el estado del vehículo a "EN_USO".
     *
     * @param conductor El conductor al cual se le asigará el vehículo.
     * @param vehiculo  El vehículo que será asignado.
     * @return La nueva asignación creada.
     * @throws IllegalArgumentException Si el conductor no existe, el vehículo está ocupado o
     *                                  si el conductor ya tiene un vehículo asignado.
     */
    public Asignacion asignarVehiculo(Conductor conductor, Vehiculo vehiculo) {
        if (conductor == null || vehiculo == null) {
            throw new IllegalArgumentException("Conductor y Vehículo son obligatorios.");
        }

        if (!conductor.isActivo()) {
            throw new IllegalArgumentException("El conductor está inactivo.");
        }

        if (vehiculo.getEstado() == EstadoVehiculo.EN_USO) {
            throw new IllegalArgumentException("El vehículo ya está en uso.");
        }

        if (vehiculo.getEstado() == EstadoVehiculo.MANTENIMIENTO) {
            throw new IllegalArgumentException("El vehículo se encuentra en mantenimiento.");
        }

        boolean tieneAsignacionActiva = asignaciones.stream()
                .anyMatch(a -> a.getConductor().getId() == conductor.getId() && a.getEstado() == EstadoAsignacion.ACTIVA);

        if (tieneAsignacionActiva) {
            throw new IllegalArgumentException("El conductor ya tiene una asignación activa.");
        }

        // Cambiar estado del vehículo
        vehiculo.setEstado(EstadoVehiculo.EN_USO);

        Asignacion nuevaAsignacion = new Asignacion(nextId++, conductor, vehiculo);
        asignaciones.add(nuevaAsignacion);
        return nuevaAsignacion;
    }

    /**
     * Finaliza una asignación en curso, liberando el vehículo para que quede "DISPONIBLE"
     * y registrando la fecha de finalización de esta asignación.
     *
     * @param idAsignacion ID de la asignación a finalizar.
     * @throws IllegalArgumentException Si la asignación no se encuentra o ya estaba finalizada.
     */
    public void finalizarAsignacion(int idAsignacion) {
        Optional<Asignacion> asignacionOpt = buscarPorId(idAsignacion);
        if (asignacionOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró la asignación.");
        }

        Asignacion asignacion = asignacionOpt.get();
        if (asignacion.getEstado() == EstadoAsignacion.FINALIZADA) {
            throw new IllegalArgumentException("La asignación ya está finalizada.");
        }

        // Registrar fin
        asignacion.setEstado(EstadoAsignacion.FINALIZADA);
        asignacion.setFechaFinalizacion(LocalDateTime.now());
        
        // Liberar vehículo
        asignacion.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
    }

    /**
     * Busca una asignación por su identificador único.
     *
     * @param id ID de la asignación.
     * @return Optional con la asignación, vacío si no existe.
     */
    public Optional<Asignacion> buscarPorId(int id) {
        return asignaciones.stream()
                .filter(a -> a.getId() == id)
                .findFirst();
    }

    /**
     * Obtiene una lista solo con las asignaciones que se encuentran activas en el momento.
     *
     * @return Lista de asignaciones con estado ACTIVA.
     */
    public List<Asignacion> obtenerActivas() {
        return asignaciones.stream()
                .filter(a -> a.getEstado() == EstadoAsignacion.ACTIVA)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una lista con todas las asignaciones realizadas históricamente.
     *
     * @return Lista de todas las asignaciones (historial).
     */
    public List<Asignacion> obtenerTodas() {
        return new ArrayList<>(asignaciones);
    }
}
