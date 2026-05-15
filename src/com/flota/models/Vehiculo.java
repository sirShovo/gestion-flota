package com.flota.models;

/**
 * La clase Vehiculo representa un automotor dentro del sistema de gestión de la flota.
 */
public class Vehiculo {
    /** Identificador único del vehículo. */
    private int id;
    
    /** Placa o matrícula del vehículo. */
    private String placa;
    
    /** Marca de fabricación del vehículo (ej: Toyota, Nissan). */
    private String marca;
    
    /** Modelo del vehículo. */
    private String modelo;
    
    /** Tipo o clasificación del vehículo (ej: Camión, Sedán). */
    private String tipo;
    
    /** Kilometraje actual registrado del vehículo. */
    private double kilometraje;
    
    /** Estado del vehículo indicando su disponibilidad general. */
    private EstadoVehiculo estado;

    /**
     * Crea una nueva instancia de un vehículo.
     * Todo vehículo recién creado inicia con el estado DISPONIBLE por defecto.
     *
     * @param id          Identificador único para el nuevo vehículo.
     * @param placa       Placa única del vehículo.
     * @param marca       Marca del vehículo.
     * @param modelo      Modelo del vehículo.
     * @param tipo        Tipo de vehículo.
     * @param kilometraje Kilometraje inicial con el que se registra.
     */
    public Vehiculo(int id, String placa, String marca, String modelo, String tipo, double kilometraje) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
        this.kilometraje = kilometraje;
        this.estado = EstadoVehiculo.DISPONIBLE; // Por defecto disponible
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public double getKilometraje() { return kilometraje; }
    public void setKilometraje(double kilometraje) { this.kilometraje = kilometraje; }
    
    public EstadoVehiculo getEstado() { return estado; }
    public void setEstado(EstadoVehiculo estado) { this.estado = estado; }

    /**
     * Genera un resumen en formato de texto con los detalles del vehículo.
     *
     * @return Una cadena de texto con los atributos del vehículo.
     */
    @Override
    public String toString() {
        return String.format("Vehiculo[id=%d, placa='%s', marca='%s', modelo='%s', tipo='%s', km=%.2f, estado=%s]",
                id, placa, marca, modelo, tipo, kilometraje, estado);
    }
}
