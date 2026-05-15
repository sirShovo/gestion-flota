package com.flota.services;

import com.flota.models.Conductor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de conductores dentro del sistema.
 * Provee métodos para registrar, buscar y listar conductores en memoria.
 */
public class ConductorService {
    /** Lista interna que funciona como base de datos en memoria para los conductores. */
    private List<Conductor> conductores;
    
    /** Contador para asignar identificadores únicos y secuenciales. */
    private int nextId;

    /**
     * Construye una nueva instancia del servicio de conductores.
     * Inicializa la lista de conductores vacía y el generador de IDs en 1.
     */
    public ConductorService() {
        this.conductores = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Registra un nuevo conductor en el sistema y lo añade a la lista en memoria.
     *
     * @param nombre   Nombre del conductor.
     * @param cedula   Número de cédula (debe ser único).
     * @param telefono Número de contacto.
     * @param licencia Tipo o número de licencia.
     * @return El objeto Conductor recién creado e insertado.
     * @throws IllegalArgumentException Si algún campo está vacío o si la cédula ya está registrada.
     */
    public Conductor registrarConductor(String nombre, String cedula, String telefono, String licencia) {
        if (nombre.trim().isEmpty() || cedula.trim().isEmpty() || telefono.trim().isEmpty() || licencia.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        if (buscarPorCedula(cedula).isPresent()) {
            throw new IllegalArgumentException("Ya existe un conductor con esa cédula.");
        }

        Conductor nuevoConductor = new Conductor(nextId++, nombre, cedula, telefono, licencia);
        conductores.add(nuevoConductor);
        return nuevoConductor;
    }

    /**
     * Busca un conductor específico mediante su número de cédula.
     *
     * @param cedula El número de cédula a buscar.
     * @return Un objeto Optional que contiene el conductor si se encuentra, o está vacío si no.
     */
    public Optional<Conductor> buscarPorCedula(String cedula) {
        return conductores.stream()
                .filter(c -> c.getCedula().equals(cedula))
                .findFirst();
    }

    /**
     * Busca un conductor específico mediante su identificador único (ID).
     *
     * @param id El identificador del conductor.
     * @return Un objeto Optional que contiene el conductor si existe.
     */
    public Optional<Conductor> buscarPorId(int id) {
        return conductores.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    /**
     * Obtiene una copia de la lista de todos los conductores registrados.
     *
     * @return Una nueva lista conteniendo todos los conductores.
     */
    public List<Conductor> obtenerTodos() {
        return new ArrayList<>(conductores);
    }
}
