package com.flota.crm;

import com.flota.crm.dao.*;
import com.flota.crm.models.*;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.util.Map;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Inicializar DAOs
        ConductorDAO conductorDAO = new ConductorDAO();
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        AsignacionDAO asignacionDAO = new AsignacionDAO();
        MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        DashboardDAO dashboardDAO = new DashboardDAO();

        // Crear usuario por defecto si no existe
        usuarioDAO.verificarYCargarUsuarioPorDefecto();

        // Iniciar Javalin Server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(8080);

        System.out.println("Servidor iniciado en http://localhost:8080");

        // ---- ENDPOINTS REST API ----

        // Login
        app.post("/api/login", ctx -> {
            Usuario req = ctx.bodyAsClass(Usuario.class);
            Usuario user = usuarioDAO.autenticar(req.getEmail(), req.getPasswordHash());
            if (user != null) {
                ctx.json(user);
            } else {
                ctx.status(401).result("Credenciales inválidas o usuario inactivo");
            }
        });

        // Dashboard
        app.get("/api/dashboard/stats", ctx -> {
            ctx.json(Map.of(
                "totalMantenimiento", dashboardDAO.getTotalMantenimientoCostos(),
                "asignacionesActivas", dashboardDAO.getCantidadAsignacionesActivas(),
                "distribucion", dashboardDAO.getEstadoVehiculosDistribucion()
            ));
        });

        // Conductores
        app.get("/api/conductores", ctx -> ctx.json(conductorDAO.obtenerTodos()));
        app.post("/api/conductores", ctx -> {
            Conductor c = ctx.bodyAsClass(Conductor.class);
            if(conductorDAO.insertar(c)) ctx.status(201); else ctx.status(500);
        });
        app.put("/api/conductores", ctx -> {
            Conductor c = ctx.bodyAsClass(Conductor.class);
            if(conductorDAO.actualizar(c)) ctx.status(200); else ctx.status(500);
        });
        app.delete("/api/conductores/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if(conductorDAO.eliminar(id)) ctx.status(200); else ctx.status(500);
        });

        // Vehiculos
        app.get("/api/vehiculos", ctx -> ctx.json(vehiculoDAO.obtenerTodos()));
        app.post("/api/vehiculos", ctx -> {
            Vehiculo v = ctx.bodyAsClass(Vehiculo.class);
            if(vehiculoDAO.insertar(v)) ctx.status(201); else ctx.status(500);
        });
        app.put("/api/vehiculos", ctx -> {
            Vehiculo v = ctx.bodyAsClass(Vehiculo.class);
            if(vehiculoDAO.actualizar(v)) ctx.status(200); else ctx.status(500);
        });
        app.delete("/api/vehiculos/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if(vehiculoDAO.eliminar(id)) ctx.status(200); else ctx.status(500);
        });

        // Asignaciones
        app.get("/api/asignaciones", ctx -> ctx.json(asignacionDAO.obtenerTodas()));
        app.post("/api/asignaciones", ctx -> {
            Asignacion a = ctx.bodyAsClass(Asignacion.class);
            if(asignacionDAO.insertar(a)) ctx.status(201); else ctx.status(500);
        });
        app.put("/api/asignaciones", ctx -> {
            Asignacion a = ctx.bodyAsClass(Asignacion.class);
            if(asignacionDAO.actualizar(a)) ctx.status(200); else ctx.status(500);
        });
        app.delete("/api/asignaciones/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if(asignacionDAO.eliminar(id)) ctx.status(200); else ctx.status(500);
        });
        app.post("/api/asignaciones/finalizar", ctx -> {
            Map<String, Integer> body = ctx.bodyAsClass(Map.class);
            if(asignacionDAO.finalizar(body.get("idAsignacion"), body.get("idVehiculo"))) ctx.status(200); else ctx.status(500);
        });

        // Mantenimientos
        app.get("/api/mantenimientos", ctx -> ctx.json(mantenimientoDAO.obtenerTodos()));
        app.post("/api/mantenimientos", ctx -> {
            Mantenimiento m = ctx.bodyAsClass(Mantenimiento.class);
            if(mantenimientoDAO.insertar(m)) ctx.status(201); else ctx.status(500);
        });
        app.put("/api/mantenimientos", ctx -> {
            Mantenimiento m = ctx.bodyAsClass(Mantenimiento.class);
            if(mantenimientoDAO.actualizar(m)) ctx.status(200); else ctx.status(500);
        });
        app.delete("/api/mantenimientos/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if(mantenimientoDAO.eliminar(id)) ctx.status(200); else ctx.status(500);
        });
        app.post("/api/mantenimientos/finalizar", ctx -> {
            Map<String, Integer> body = ctx.bodyAsClass(Map.class);
            if(mantenimientoDAO.finalizar(body.get("idMantenimiento"), body.get("idVehiculo"))) ctx.status(200); else ctx.status(500);
        });

        // Usuarios
        app.get("/api/usuarios", ctx -> ctx.json(usuarioDAO.obtenerTodos()));
        app.post("/api/usuarios", ctx -> {
            Usuario u = ctx.bodyAsClass(Usuario.class);
            if(usuarioDAO.insertar(u)) ctx.status(201); else ctx.status(500);
        });
        app.put("/api/usuarios", ctx -> {
            Usuario u = ctx.bodyAsClass(Usuario.class);
            if(usuarioDAO.actualizar(u)) ctx.status(200); else ctx.status(500);
        });
        app.put("/api/usuarios/{id}/password", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            if(usuarioDAO.actualizarContrasena(id, body.get("password"))) ctx.status(200); else ctx.status(500);
        });
        app.delete("/api/usuarios/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if(usuarioDAO.desactivar(id)) ctx.status(200); else ctx.status(500);
        });
        
        // Búsqueda Global (simplificada)
        app.get("/api/search", ctx -> {
            String q = ctx.queryParam("q");
            if (q == null || q.isBlank()) {
                ctx.json(List.of());
                return;
            }
            q = q.toLowerCase();
            
            List<Map<String, Object>> results = new java.util.ArrayList<>();
            
            for (Conductor c : conductorDAO.obtenerTodos()) {
                if (c.getNombre().toLowerCase().contains(q) || c.getCedula().toLowerCase().contains(q)) {
                    results.add(Map.of("type", "Conductor", "entityType", "conductor", "id", c.getId(), "title", c.getNombre(), "subtitle", "C.C: " + c.getCedula(), "target", "conductores-view"));
                }
            }
            for (Vehiculo v : vehiculoDAO.obtenerTodos()) {
                if (v.getPlaca().toLowerCase().contains(q) || v.getMarca().toLowerCase().contains(q)) {
                    results.add(Map.of("type", "Vehículo", "entityType", "vehiculo", "id", v.getId(), "title", v.getPlaca() + " - " + v.getMarca(), "subtitle", "Modelo: " + v.getModelo(), "target", "vehiculos-view"));
                }
            }
            for (Usuario u : usuarioDAO.obtenerTodos()) {
                if (u.getNombre().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q)) {
                    results.add(Map.of("type", "Usuario", "entityType", "usuario", "id", u.getId(), "title", u.getNombre(), "subtitle", u.getEmail(), "target", "usuarios-view"));
                }
            }
            
            ctx.json(results);
        });
    }
}
