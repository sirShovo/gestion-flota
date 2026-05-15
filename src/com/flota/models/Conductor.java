package com.flota.models;

/**
 * La clase Conductor representa a la persona encargada de manejar un vehículo de la flota.
 */
public class Conductor {
    /** Identificador único del conductor. */
    private int id;
    
    /** Nombre completo del conductor. */
    private String nombre;
    
    /** Cédula de identidad o documento del conductor. */
    private String cedula;
    
    /** Número de teléfono de contacto. */
    private String telefono;
    
    /** Número o tipo de licencia de conducir. */
    private String licencia;
    
    /** Indica si el conductor está activo dentro del sistema (true) o inactivo (false). */
    private boolean activo;

    /**
     * Construye un nuevo Conductor con la información proporcionada.
     * Por defecto, un nuevo conductor siempre se inicializa como activo.
     *
     * @param id       El identificador único del nuevo conductor.
     * @param nombre   El nombre completo del conductor.
     * @param cedula   El número de cédula del conductor.
     * @param telefono El teléfono de contacto del conductor.
     * @param licencia La licencia de conducir del conductor.
     */
    public Conductor(int id, String nombre, String cedula, String telefono, String licencia) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.telefono = telefono;
        this.licencia = licencia;
        this.activo = true; // Por defecto activo
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    /**
     * Devuelve una representación en formato de texto con los datos relevantes del conductor.
     *
     * @return String con el detalle de los atributos del conductor.
     */
    @Override
    public String toString() {
        return String.format("Conductor[id=%d, nombre='%s', cedula='%s', telefono='%s', licencia='%s', estado=%s]",
                id, nombre, cedula, telefono, licencia, activo ? "ACTIVO" : "INACTIVO");
    }
}
