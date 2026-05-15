# CRM de Gestion de Flota Vehicular

## Descripcion del Proyecto
El CRM de Gestion de Flota Vehicular es una aplicacion de escritorio desarrollada en Java Swing bajo el patron de arquitectura MVC (Modelo-Vista-Controlador). Su objetivo principal es facilitar la administracion y el seguimiento de una flota de vehiculos, gestionando a los conductores, las asignaciones temporales de los vehiculos y el registro de mantenimientos.

A diferencia del sistema original basado en consola, esta version cuenta con una interfaz grafica moderna (estilizada con FlatLaf) y persistencia de datos mediante una base de datos relacional PostgreSQL.

## Flujo del Sistema
El flujo recomendado para operar esta aplicacion es secuencial, garantizando que todos los elementos existan y esten en regla:

1. Registrar Entidades Base:
   - Primero se debe registrar un Conductor desde el panel de Conductores.
   - Seguido, se debe registrar un Vehiculo en el panel respectivo. Todo vehiculo nuevo arranca su ciclo de vida en estado DISPONIBLE.
2. Crear Asignaciones:
   - Para que el vehiculo circule, se dirige al panel de Asignaciones y se entrega el vehiculo a un conductor.
   - El vehiculo pasa al estado EN_USO y no podra ser tomado por otro conductor, ni podra entrar a mantenimiento hasta ser devuelto.
3. Pausar/Finalizar Asignaciones:
   - Al finalizar la asignacion seleccionada en la tabla, el vehiculo es regresado y su estado vuelve de inmediato a DISPONIBLE.
4. Registrar Mantenimientos:
   - Un vehiculo que sufra algun percance o requiera rutina preventiva puede ser enviado al taller desde el panel de Mantenimientos.
   - El vehiculo cambia a estado MANTENIMIENTO y ningun conductor se lo podra llevar.
5. Retorno a Flota:
   - Al marcar un mantenimiento como terminado, el vehiculo vuelve a estar DISPONIBLE.
6. Dashboard y Reportes:
   - El Dashboard principal lee en tiempo real el estado de la flota, mostrando graficas de distribucion y calculando el gasto total acumulado de los mantenimientos.

## Cosas a Tener en Cuenta
- Integridad Referencial: El sistema utiliza llaves foraneas estrictas. No se recomienda borrar registros directamente en la base de datos para no perder el historial de asignaciones y mantenimientos. En su lugar, el sistema prevé el manejo mediante estados.
- Transacciones Base de Datos: La aplicacion asegura que las acciones complejas sean seguras. Si un vehiculo es prestado, su asignacion se crea y su estado cambia a EN_USO simultaneamente dentro de una sola transaccion SQL para evitar inconsistencias.
- Actualizacion Automatica: Al navegar entre los distintos paneles del menu lateral, las tablas de datos y graficas se refrescan de forma automatica haciendo peticiones a la base de datos para mostrar siempre la informacion mas reciente.

## Guia de Instalacion y Ejecucion

### 1. Preparacion de la Base de Datos (PostgreSQL)
Este proyecto requiere tener instalado PostgreSQL en su maquina local.

1. Abra su gestor de PostgreSQL local (como pgAdmin, DBeaver, o consola).
2. Cree una base de datos nueva y vacia. Se recomienda llamarla `flota_crm_db`.
3. Localice el script de inicializacion SQL ubicado en el proyecto en la ruta: `src/main/resources/schema.sql`.
4. Ejecute todo el contenido de `schema.sql` sobre la base de datos recien creada. Esto generara las tablas `conductores`, `vehiculos`, `asignaciones` y `mantenimientos`, ademas de insertar algunos datos de prueba iniciales.

### 2. Configuracion de la Conexion en Java
Para que la aplicacion pueda comunicarse exitosamente con la base de datos, debe configurar sus credenciales de acceso:

1. Navegue en el codigo fuente hasta el archivo `src/main/java/com/flota/crm/config/DatabaseConnection.java`.
2. Modifique las constantes de conexion segun su entorno local:
   - `URL`: Debe apuntar a su host, puerto y nombre de base de datos (por defecto `jdbc:postgresql://localhost:5432/flota_crm_db`).
   - `USER`: Su usuario de PostgreSQL (generalmente es `postgres`).
   - `PASSWORD`: La contrasena de su usuario de PostgreSQL (la que usa para entrar a pgAdmin o a la consola).

### 3. Compilacion y Ejecucion
El proyecto utiliza Maven para la gestion de dependencias.

1. Abra la carpeta `crm-swing-app` como proyecto en su IDE preferido (se recomienda IntelliJ IDEA, Eclipse, o VSCode con extension de Java).
2. Permita que el IDE descargue las dependencias listadas en el archivo `pom.xml` (Driver JDBC de PostgreSQL, FlatLaf y JFreeChart).
3. Busque la clase principal en `src/main/java/com/flota/crm/App.java`.
4. Ejecute el metodo `main` de la clase `App.java` para iniciar y lanzar la interfaz grafica de la aplicacion.
