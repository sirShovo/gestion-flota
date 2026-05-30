# CRM de Gestión de Flota Vehicular

## Descripción del Proyecto
El CRM de Gestión de Flota Vehicular es una aplicación de escritorio desarrollada en Java Swing bajo el patrón de arquitectura MVC (Modelo-Vista-Controlador). Su objetivo principal es facilitar la administración y el seguimiento de una flota de vehículos, gestionando a los conductores, las asignaciones temporales de los vehículos, el registro de mantenimientos y la seguridad del sistema mediante un módulo de control de accesos.

Esta versión cuenta con una interfaz gráfica premium inspirada en **Soft UI / Glassmorphism** (estilizada con FlatLaf), validaciones de formularios integradas con avisos dinámicos en pantalla, una pantalla de carga animada (Splash Screen) de 3 segundos, cifrado seguro de contraseñas mediante **BCrypt**, y persistencia de datos mediante una base de datos relacional **MySQL**.

## Flujo del Sistema
El flujo recomendado para operar esta aplicación es secuencial, garantizando que todos los elementos existan y estén en regla:

1. **Pantalla de Carga y Login**:
   - Al iniciar la aplicación, se mostrará una pantalla de carga animada durante 3 segundos.
   - Posteriormente, se presentará el Login. Para ingresar, se requiere un correo y contraseña válidos. 
   - El sistema viene preconfigurado para auto-inicializar un usuario administrador por defecto si la tabla está vacía:
     - **Usuario**: `bornacelly99@gmail.com`
     - **Contraseña**: `admin123`
2. **Módulo de Usuarios**:
   - Permite la gestión (CRUD) de los usuarios que pueden acceder al sistema.
   - En cumplimiento de las reglas de negocio, los usuarios no se eliminan físicamente de la base de datos; se desactivan. Si un usuario está inactivo, el sistema impedirá su inicio de sesión.
   - Registra de forma automática la fecha y hora de creación (`created_at`) y de última actualización (`updated_at`).
3. **Registrar Entidades Base**:
   - Primero se debe registrar un Conductor desde el panel de Conductores.
   - Luego, se registra un Vehículo en el panel respectivo. Todo vehículo nuevo inicia en estado `DISPONIBLE`.
4. **Crear Asignaciones**:
   - En el panel de Asignaciones se entrega el vehículo a un conductor.
   - El vehículo pasa al estado `EN_USO` y no podrá ser tomado por otro conductor, ni podrá entrar a mantenimiento hasta ser devuelto.
5. **Pausar/Finalizar Asignaciones**:
   - Al finalizar la asignación seleccionada en la tabla, el vehículo es regresado y su estado vuelve de inmediato a `DISPONIBLE`.
6. **Registrar Mantenimientos**:
   - Un vehículo en estado `DISPONIBLE` puede ser enviado al taller desde el panel de Mantenimientos.
   - El vehículo cambia a estado `MANTENIMIENTO` y ningún conductor se lo podrá llevar.
7. **Retorno a Flota**:
   - Al marcar un mantenimiento como terminado, el vehículo vuelve a estar `DISPONIBLE`.
8. **Dashboard y Reportes**:
   - El Dashboard principal lee en tiempo real el estado de la flota, mostrando gráficas de distribución y calculando el gasto total acumulado.

---

## Guía de Instalación y Ejecución

### Requisitos Previos
Antes de ejecutar el proyecto, asegúrese de tener instalado en su sistema:
- **Java Development Kit (JDK)**: Versión 17 o superior.
- **Apache Maven**: Para la gestión de dependencias y construcción del proyecto.
- **MySQL Server**: Versión 8.0 o superior.

---

### Step 1: Preparación de la Base de Datos (MySQL)

1. Inicie su servidor de MySQL local.
2. Abra su gestor de bases de datos preferido (como DBeaver, MySQL Workbench, o la terminal).
3. Conéctese a su servidor y ejecute el script de inicialización SQL ubicado en el proyecto en la ruta:
   `src/main/resources/schema.sql`
   
   *Nota: El script creará automáticamente la base de datos `flota_crm_db` y todas las tablas necesarias si no existen.*

---

### Step 2: Configuración de Credenciales

1. Abra el archivo de propiedades en la siguiente ruta del proyecto:
   `src/main/resources/database.properties`
2. Modifique las credenciales de conexión según su configuración local de MySQL:
   - `db.url`: URL de conexión (por defecto `jdbc:mysql://localhost:3306/flota_crm_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`).
   - `db.user`: Su usuario de MySQL (por defecto `root`).
   - `db.password`: La contraseña de su usuario de MySQL (cámbiela por la suya).

---

### Step 3: Compilación y Ejecución

Esta aplicación ha sido modernizada a una **Arquitectura Web**. Al ejecutar el proyecto Java, no se abrirán ventanas de escritorio anticuadas, sino que se iniciará un servidor web de alto rendimiento.

1. Abra una terminal de comandos en la carpeta raíz del proyecto (`crm-swing-app`) y ejecute los siguientes comandos:

   **Limpiar y compilar el proyecto**:
   ```bash
   mvn clean compile
   ```

   **Ejecutar el Servidor**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.flota.crm.App"
   ```

2. **Acceder a la Interfaz Web**:
   Abra su navegador de preferencia (Chrome, Edge, Firefox) e ingrese a la siguiente dirección:
   
   **`http://localhost:8080`**

### Credenciales de Acceso al Iniciar
Una vez que cargue la interfaz web en su navegador:
1. Aparecerá la ventana de inicio de sesión (Login) con el nuevo diseño Soft UI premium. 
2. El sistema cuenta con un inicializador automático. Si la tabla de usuarios de MySQL está vacía, creará por defecto el siguiente usuario administrador para que pueda ingresar inmediatamente:
   * **Correo electrónico**: `bornacelly99@gmail.com`
   * **Contraseña**: `admin123`
