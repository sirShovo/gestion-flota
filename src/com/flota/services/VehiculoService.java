package com.flota.services;

import com.flota.models.EstadoVehiculo;
import com.flota.models.Vehiculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los vehículos.
 * Permite registrar, buscar y listar vehículos en memoria.
 */
public class VehiculoService {
    /** Lista interna que almacena los vehículos registrados. */
    private List<Vehiculo> vehiculos;
    
    /** Generador de identificadores para los nuevos vehículos. */
    private int nextId;

    /**
     * Construye una nueva instancia del servicio de vehículos.
     * Inicializa la lista interna y establece el primer ID en 1.
     */
    public VehiculoService() {
        this.vehiculos = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Registra un nuevo vehículo comprobando validaciones básicas.
     *
     * @param placa       Placa única del vehículo.
     * @param marca       Marca del vehículo.
     * @param modelo      Modelo del vehículo.
     * @param tipo        Tipo de vehículo (ej. sedán, pickup).
     * @param kilometraje Kilometraje actual (no puede ser negativo).
     * @return El objeto Vehiculo creado y registrado.
     * @throws IllegalArgumentException Si falta algún campo, el km es negativo, o la placa ya existe.
     */
    public Vehiculo registrarVehiculo(String placa, String marca, String modelo, String tipo, double kilometraje) {
        if (placa.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty() || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos (placa, marca, modelo, tipo) son obligatorios.");
        }
        
        if (kilometraje < 0) {
            throw new IllegalArgumentException("El kilometraje no puede ser negativo.");
        }

        if (buscarPorPlaca(placa).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehículo con esa placa.");
        }

        Vehiculo nuevoVehiculo = new Vehiculo(nextId++, placa, marca, modelo, tipo, kilometraje);
        vehiculos.add(nuevoVehiculo);
        return nuevoVehiculo;
    }

    /**
     * Busca un vehículo por su placa, ignorando mayúsculas y minúsculas.
     *
     * @param placa La placa del vehículo a buscar.
     * @return Un Optional que contiene el vehículo si se encuentra.
     */
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        return vehiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa))
                .findFirst();
    }

    /**
     * Busca un vehículo por su identificador único (ID).
     *
     * @param id El identificador del vehículo a buscar.
     * @return Un Optional con el vehículo si existe.
     */
    public Optional<Vehiculo> buscarPorId(int id) {
        return vehiculos.stream()
                .filter(v -> v.getId() == id)
                .findFirst();
    }

    /**
     * Obtiene una copia con todos los vehículos registrados en el sistema.
     *
     * @return Lista de vehículos.
     */
    public List<Vehiculo> obtenerTodos() {
        return new ArrayList<>(vehiculos);
    }

    /**
     * Filtra y obtiene los vehículos que se encuentran en un estado específico.
     *
     * @param estado EstadoVehiculo por el cual filtrar (e.g. DISPONIBLE).
     * @return Lista de vehículos que cumplen con el estado indicado.
     */
    public List<Vehiculo> obtenerPorEstado(EstadoVehiculo estado) {
        return vehiculos.stream()
                .filter(v -> v.getEstado() == estado)
                .collect(Collectors.toList());
    }
}
