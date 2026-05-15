package com.flota;

import com.flota.models.Conductor;
import com.flota.models.EstadoVehiculo;
import com.flota.models.TipoMantenimiento;
import com.flota.models.Vehiculo;
import com.flota.models.Asignacion;
import com.flota.models.Mantenimiento;
import com.flota.services.AsignacionService;
import com.flota.services.ConductorService;
import com.flota.services.MantenimientoService;
import com.flota.services.VehiculoService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Clase principal que actúa como punto de entrada de la aplicación de Consola.
 * Provee la interfaz de usuario en modo texto y manipula los distintos servicios.
 */
public class Main {
    /** Servicio para administrar las operaciones de los conductores. */
    private static ConductorService conductorService = new ConductorService();
    
    /** Servicio para administrar el registro de vehículos. */
    private static VehiculoService vehiculoService = new VehiculoService();
    
    /** Servicio para coordinar el préstamo o asignación de vehículos. */
    private static AsignacionService asignacionService = new AsignacionService();
    
    /** Servicio para controlar las revisiones y reparaciones de los vehículos. */
    private static MantenimientoService mantenimientoService = new MantenimientoService();
    
    /** Escáner para leer la entrada del usuario por consola. */
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Método principal de ejecución.
     * Muestra el menú principal y procesa la entrada del usuario en un bucle continuo.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        boolean salir = false;

        System.out.println("==================================================");
        System.out.println("  SISTEMA DE GESTIÓN DE FLOTA VEHICULAR ");
        System.out.println("==================================================");

        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero("Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1: registrarConductor(); break;
                    case 2: registrarVehiculo(); break;
                    case 3: asignarVehiculo(); break;
                    case 4: finalizarAsignacion(); break;
                    case 5: registrarMantenimiento(); break;
                    case 6: finalizarMantenimiento(); break;
                    case 7: consultarInformacion(); break;
                    case 8: 
                        salir = true; 
                        System.out.println("Saliendo del sistema..."); 
                        break;
                    default:
                        System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    /**
     * Muestra las opciones disponibles del menú principal en la consola.
     */
    private static void mostrarMenu() {
        System.out.println("1. Registrar conductor");
        System.out.println("2. Registrar vehículo");
        System.out.println("3. Asignar vehículo a conductor");
        System.out.println("4. Finalizar asignación");
        System.out.println("5. Registrar mantenimiento");
        System.out.println("6. Finalizar mantenimiento");
        System.out.println("7. Consultar información");
        System.out.println("8. Salir");
    }

    /**
     * Inicia un flujo interactivo para registrar un nuevo conductor pidiendo datos por consola.
     */
    private static void registrarConductor() {
        System.out.println("\n--- REGISTRO DE CONDUCTOR ---");
        String nombre = leerCadena("Nombre: ");
        String cedula = leerCadena("Cédula: ");
        String telefono = leerCadena("Teléfono: ");
        String licencia = leerCadena("Licencia: ");

        Conductor c = conductorService.registrarConductor(nombre, cedula, telefono, licencia);
        System.out.println("Conductor registrado con éxito. ID: " + c.getId());
    }

    /**
     * Inicia un flujo interactivo para registrar un nuevo vehículo pidiendo datos por consola.
     */
    private static void registrarVehiculo() {
        System.out.println("\n--- REGISTRO DE VEHÍCULO ---");
        String placa = leerCadena("Placa: ");
        String marca = leerCadena("Marca: ");
        String modelo = leerCadena("Modelo: ");
        String tipo = leerCadena("Tipo (Ej. Sedán, Camión, SUV): ");
        double km = leerDouble("Kilometraje: ");

        Vehiculo v = vehiculoService.registrarVehiculo(placa, marca, modelo, tipo, km);
        System.out.println("Vehículo registrado con éxito. ID: " + v.getId());
    }

    /**
     * Permite al usuario seleccionar un conductor y un vehículo para crear una nueva asignación.
     */
    private static void asignarVehiculo() {
        System.out.println("\n--- ASIGNACIÓN DE VEHÍCULO ---");
        
        System.out.println("Conductores disponibles:");
        conductorService.obtenerTodos().forEach(System.out::println);
        int idConductor = leerEntero("Ingrese ID del Conductor: ");
        Optional<Conductor> cOpt = conductorService.buscarPorId(idConductor);

        if (cOpt.isEmpty()) {
            System.out.println("Error: Conductor no encontrado.");
            return;
        }

        System.out.println("\nVehículos disponibles:");
        vehiculoService.obtenerPorEstado(EstadoVehiculo.DISPONIBLE).forEach(System.out::println);
        int idVehiculo = leerEntero("Ingrese ID del Vehículo: ");
        Optional<Vehiculo> vOpt = vehiculoService.buscarPorId(idVehiculo);

        if (vOpt.isEmpty()) {
            System.out.println("Error: Vehículo no encontrado.");
            return;
        }

        Asignacion a = asignacionService.asignarVehiculo(cOpt.get(), vOpt.get());
        System.out.println("Asignación creada con éxito. ID: " + a.getId());
    }

    /**
     * Solicita el ID de una asignación activa para marcarla como finalizada.
     */
    private static void finalizarAsignacion() {
        System.out.println("\n--- FINALIZAR ASIGNACIÓN ---");
        List<Asignacion> activas = asignacionService.obtenerActivas();
        if (activas.isEmpty()) {
            System.out.println("No hay asignaciones activas.");
            return;
        }

        activas.forEach(System.out::println);
        int idAsignacion = leerEntero("Ingrese ID de la Asignación a finalizar: ");
        
        asignacionService.finalizarAsignacion(idAsignacion);
        System.out.println("Asignación finalizada con éxito.");
    }

    /**
     * Selecciona un vehículo y registra el inicio de un proceso de mantenimiento sobre él.
     */
    private static void registrarMantenimiento() {
        System.out.println("\n--- REGISTRAR MANTENIMIENTO ---");
        vehiculoService.obtenerTodos().forEach(System.out::println);
        int idVehiculo = leerEntero("Ingrese ID del Vehículo: ");
        
        Optional<Vehiculo> vOpt = vehiculoService.buscarPorId(idVehiculo);
        if (vOpt.isEmpty()) {
            System.out.println("Error: Vehículo no encontrado.");
            return;
        }

        System.out.println("Tipo de Mantenimiento:\n1. Preventivo\n2. Correctivo");
        int tipoOpc = leerEntero("Seleccione tipo: ");
        TipoMantenimiento tipo = (tipoOpc == 1) ? TipoMantenimiento.PREVENTIVO : TipoMantenimiento.CORRECTIVO;

        String descripcion = leerCadena("Descripción del trabajo: ");
        double costo = leerDouble("Costo estimado ($): ");

        Mantenimiento m = mantenimientoService.registrarMantenimiento(vOpt.get(), tipo, descripcion, costo);
        System.out.println("Mantenimiento registrado. ID: " + m.getId());
    }

    /**
     * Solicita el ID de un mantenimiento en progreso para registrar que ya concluyó.
     */
    private static void finalizarMantenimiento() {
        System.out.println("\n--- FINALIZAR MANTENIMIENTO ---");
        List<Mantenimiento> historial = mantenimientoService.obtenerHistorial();
        historial.forEach(System.out::println);

        int idMant = leerEntero("Ingrese ID del Mantenimiento a finalizar: ");
        mantenimientoService.finalizarMantenimiento(idMant);
        System.out.println("Mantenimiento finalizado con éxito.");
    }

    /**
     * Menú secundario que permite visualizar listas de datos específicos del sistema.
     */
    private static void consultarInformacion() {
        System.out.println("\n--- CONSULTAS ---");
        System.out.println("1. Listar conductores");
        System.out.println("2. Listar vehículos por estado");
        System.out.println("3. Listar asignaciones activas");
        System.out.println("4. Listar historial de mantenimientos");
        
        int opc = leerEntero("Seleccione consulta: ");
        
        System.out.println("\n--- RESULTADOS ---");
        switch (opc) {
            case 1:
                conductorService.obtenerTodos().forEach(System.out::println);
                break;
            case 2:
                System.out.println("Estados: 1. DISPONIBLE  2. EN USO  3. MANTENIMIENTO");
                int estOpc = leerEntero("Seleccione estado: ");
                EstadoVehiculo evt = EstadoVehiculo.DISPONIBLE;
                if (estOpc == 2) evt = EstadoVehiculo.EN_USO;
                if (estOpc == 3) evt = EstadoVehiculo.MANTENIMIENTO;
                vehiculoService.obtenerPorEstado(evt).forEach(System.out::println);
                break;
            case 3:
                asignacionService.obtenerActivas().forEach(System.out::println);
                break;
            case 4:
                mantenimientoService.obtenerHistorial().forEach(System.out::println);
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    // --- UTILIDADES ---
    
    /**
     * Imprime un mensaje en pantalla y lee una cadena de texto desde la consola.
     *
     * @param mensaje Mensaje o prompt a mostrar al usuario.
     * @return El texto ingresado.
     */
    private static String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    /**
     * Imprime un mensaje en pantalla y de forma robusta asegura la lectura de un número entero.
     *
     * @param mensaje Prompt a mostrar.
     * @return Un número entero válido ingresado por el usuario.
     */
    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!scanner.hasNextInt()) {
            System.out.print("Por favor, ingrese un número válido: ");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // consumir el newline
        return valor;
    }

    /**
     * Imprime un mensaje en pantalla y asegura la lectura de un número de punto flotante.
     *
     * @param mensaje Prompt a mostrar.
     * @return Un número decimal con coma flotante válido.
     */
    private static double leerDouble(String mensaje) {
        System.out.print(mensaje);
        while (!scanner.hasNextDouble()) {
            System.out.print("Por favor, ingrese un monto válido: ");
            scanner.next();
        }
        double valor = scanner.nextDouble();
        scanner.nextLine(); // consumir el newline
        return valor;
    }
}
