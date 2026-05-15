package com.flota.services;

import com.flota.models.EstadoMantenimiento;
import com.flota.models.EstadoVehiculo;
import com.flota.models.Mantenimiento;
import com.flota.models.TipoMantenimiento;
import com.flota.models.Vehiculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar todos los procesos de mantenimiento de la flota.
 * Permite mandar vehículos a mantenimiento y registrarlos de vuelta una vez terminados.
 */
public class MantenimientoService {
    /** Lista interna que guarda el registro histórico y activo de mantenimientos. */
    private List<Mantenimiento> mantenimientos;
    
    /** Identificador auto-incremental para los mantenimientos. */
    private int nextId;

    /**
     * Construye una nueva instancia para el servicio de mantenimiento.
     */
    public MantenimientoService() {
        this.mantenimientos = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Crea un nuevo registro indicando que un vehículo entra a mantenimiento.
     * Cambia el estado del vehículo a "MANTENIMIENTO".
     *
     * @param vehiculo    Vehículo a someter al mantenimiento.
     * @param tipo        Preventivo o correctivo.
     * @param descripcion Detalle del problema o servicio requerido.
     * @param costo       Costo proyectado o final.
     * @return El Mantenimiento registrado y creado.
     * @throws IllegalArgumentException Si falta información obligatoria o si el vehículo está en uso.
     */
    public Mantenimiento registrarMantenimiento(Vehiculo vehiculo, TipoMantenimiento tipo, String descripcion, double costo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio.");
        }

        if (vehiculo.getEstado() == EstadoVehiculo.EN_USO) {
            throw new IllegalArgumentException("No se puede registrar mantenimiento si el vehículo está en uso.");
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }

        if (costo < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo.");
        }

        // Cambiar estado del vehículo
        vehiculo.setEstado(EstadoVehiculo.MANTENIMIENTO);

        Mantenimiento nuevoMantenimiento = new Mantenimiento(nextId++, vehiculo, tipo, descripcion, costo);
        mantenimientos.add(nuevoMantenimiento);
        return nuevoMantenimiento;
    }

    /**
     * Finaliza un trabajo de mantenimiento, permitiendo que el vehículo regrese
     * al estado "DISPONIBLE" para poder volver a ser asignado.
     *
     * @param idMantenimiento El identificador del registro de mantenimiento.
     * @throws IllegalArgumentException Si el id no existe o si ya estaba finalizado.
     */
    public void finalizarMantenimiento(int idMantenimiento) {
        Optional<Mantenimiento> mantenimientoOpt = buscarPorId(idMantenimiento);
        
        if (mantenimientoOpt.isEmpty()) {
            throw new IllegalArgumentException("Mantenimiento no encontrado.");
        }

        Mantenimiento mant = mantenimientoOpt.get();
        if (mant.getEstado() == EstadoMantenimiento.FINALIZADO) {
            throw new IllegalArgumentException("Este mantenimiento ya ha sido finalizado.");
        }

        mant.setEstado(EstadoMantenimiento.FINALIZADO);
        mant.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
    }

    /**
     * Busca un registro de mantenimiento en particular utilizando su ID.
     *
     * @param id El identificador único del mantenimiento.
     * @return Optional con el mantenimiento, si se encontró.
     */
    public Optional<Mantenimiento> buscarPorId(int id) {
        return mantenimientos.stream()
                .filter(m -> m.getId() == id)
                .findFirst();
    }

    /**
     * Obtiene todo el registro histórico de los mantenimientos procesados en el sistema.
     *
     * @return Lista de mantenimientos documentados.
     */
    public List<Mantenimiento> obtenerHistorial() {
        return new ArrayList<>(mantenimientos);
    }
}
