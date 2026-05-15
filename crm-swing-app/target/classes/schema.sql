-- Script de creación de la base de datos para el CRM de Gestión de Flota

-- NOTA: Ejecutar esto en una base de datos PostgreSQL vacía (ej: "flota_crm_db")

-- Tabla de Conductores
CREATE TABLE IF NOT EXISTS conductores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cedula VARCHAR(50) UNIQUE NOT NULL,
    telefono VARCHAR(50),
    licencia VARCHAR(50),
    activo BOOLEAN DEFAULT true
);

-- Tabla de Vehículos
CREATE TABLE IF NOT EXISTS vehiculos (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(20) UNIQUE NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    kilometraje NUMERIC(10, 2) DEFAULT 0.0,
    estado VARCHAR(20) DEFAULT 'DISPONIBLE' -- DISPONIBLE, EN_USO, MANTENIMIENTO
);

-- Tabla de Asignaciones
CREATE TABLE IF NOT EXISTS asignaciones (
    id SERIAL PRIMARY KEY,
    conductor_id INT NOT NULL,
    vehiculo_id INT NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_finalizacion TIMESTAMP NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVA', -- ACTIVA, FINALIZADA
    CONSTRAINT fk_conductor FOREIGN KEY (conductor_id) REFERENCES conductores(id),
    CONSTRAINT fk_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id)
);

-- Tabla de Mantenimientos
CREATE TABLE IF NOT EXISTS mantenimientos (
    id SERIAL PRIMARY KEY,
    vehiculo_id INT NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- PREVENTIVO, CORRECTIVO
    descripcion TEXT,
    costo NUMERIC(10, 2) DEFAULT 0.0,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'EN_PROGRESO', -- EN_PROGRESO, FINALIZADO
    CONSTRAINT fk_mantenimiento_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id)
);

-- Inserción de datos iniciales para pruebas (Opcional)
INSERT INTO conductores (nombre, cedula, telefono, licencia) VALUES
('Juan Pérez', '1234567890', '555-0101', 'TIPO-B'),
('María García', '0987654321', '555-0102', 'TIPO-C') ON CONFLICT DO NOTHING;

INSERT INTO vehiculos (placa, marca, modelo, tipo, kilometraje, estado) VALUES
('ABC-123', 'Toyota', 'Corolla', 'Sedán', 15000, 'DISPONIBLE'),
('XYZ-987', 'Ford', 'Ranger', 'Camioneta', 25000, 'DISPONIBLE') ON CONFLICT DO NOTHING;
