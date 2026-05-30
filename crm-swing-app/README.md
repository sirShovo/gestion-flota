# CRM de Gestion de Flota Vehicular

## Descripcion del Proyecto
El CRM de Gestion de Flota Vehicular es una plataforma web moderna y completa, diseñada bajo la arquitectura de Aplicacion de Una Sola Pagina (SPA - Single Page Application) en el frontend y un robusto backend en Java. Este sistema permite centralizar y optimizar la gestion logistica de una empresa mediante el control total de conductores, vehiculos, asignaciones, mantenimientos y usuarios.

La aplicacion destaca por una interfaz premium inspirada en las tendencias de diseño Soft UI y Glassmorphism, con tipografias modernas, botones ergonomicos, transiciones fluidas, soporte nativo de modo oscuro, buscador global y un sistema interactivo de notificaciones tipo toast, eliminando por completo las interfaces de escritorio tradicionales para ofrecer una experiencia web premium.

---

## Guia de Instalacion y Ejecucion

### Requisitos Previos
Para poder compilar y ejecutar el proyecto localmente, es necesario contar con las siguientes herramientas instaladas:
* Java Development Kit (JDK): Version 17 o superior.
* Apache Maven: Para la gestion de dependencias y construccion del proyecto.
* MySQL Server: Version 8.0 o superior.

### Paso 1: Preparacion de la Base de Datos (MySQL)
1. Inicie su servidor local de MySQL.
2. Abra su herramienta de administracion de bases de datos de preferencia (DBeaver, MySQL Workbench, o la consola de comandos).
3. Ejecute el script SQL de inicializacion que se encuentra en la ruta del proyecto:
   [schema.sql](file:///c:/Users/sebastian.agudelo/Documents/me/gestion-flota/crm-swing-app/src/main/resources/schema.sql)
   
   Este script se encargara de crear de forma automatica la base de datos llamada `flota_crm_db` junto con todas sus tablas, relaciones y claves foraneas requeridas si aun no existen.

### Paso 2: Configuracion de las Credenciales de Conexion
1. Dirijase al archivo de propiedades de base de datos ubicado en la ruta:
   [database.properties](file:///c:/Users/sebastian.agudelo/Documents/me/gestion-flota/crm-swing-app/src/main/resources/database.properties)
2. Edite los valores segun las credenciales locales de su servidor de MySQL:
   * `db.url`: Cadena de conexion JDBC (por defecto configurada para `jdbc:mysql://localhost:3306/flota_crm_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`).
   * `db.user`: Nombre de usuario de la base de datos (por defecto `root`).
   * `db.password`: Contraseña de su usuario de MySQL (debe ser actualizada con su clave local).

### Paso 3: Compilacion y Puesta en Marcha del Servidor
1. Abra una terminal o consola de comandos en el directorio raiz del proyecto (`crm-swing-app`).
2. Ejecute los siguientes comandos de Maven para compilar y levantar la aplicacion:

   **Limpieza y compilacion de dependencias**:
   ```bash
   mvn clean compile
   ```

   **Ejecucion del servidor web embebido**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.flota.crm.App"
   ```

### Paso 4: Acceso a la Plataforma Web
1. Una vez que la consola muestre que el servidor se ha iniciado correctamente, abra su navegador web (Chrome, Firefox, Edge, etc.).
2. Acceda a la siguiente URL:
   **http://localhost:8080**
3. El sistema cuenta con un mecanismo de auto-inicializacion. Si la tabla de usuarios en la base de datos se encuentra vacia, el backend creara de forma automatica una cuenta de administrador por defecto para permitirle el acceso inmediato:
   * **Usuario / Correo**: `bornacelly99@gmail.com`
   * **Contraseña**: `admin123`

---

## Flujo Completo de la Aplicacion

El flujo logico y de datos de la aplicacion esta diseñado como una arquitectura de capas desacopladas, lo que asegura que las acciones en la interfaz de usuario se reflejen inmediatamente en la persistencia de datos bajo estrictas reglas de negocio:

### 1. Autenticacion y Acceso Seguro
* Al ingresar a la aplicacion, la SPA detecta el estado de sesion del usuario. Si no esta autenticado, despliega una pantalla de Login con diseño Soft UI.
* Las credenciales se envian mediante un metodo asincrono POST a la ruta de la API `/api/login`.
* El backend Java recibe los datos, consulta la base de datos a traves de la capa DAO (`UsuarioDAO`) y verifica la contraseña utilizando cifrado seguro unidireccional **BCrypt**.
* Si la autenticacion es valida y el usuario se encuentra en estado ACTIVO, el servidor retorna la informacion del usuario, lo que le permite a la aplicacion cargar la vista principal.

### 2. Panel Principal (Dashboard)
* Al iniciar sesion, el usuario es recibido por el Dashboard.
* El frontend realiza una peticion GET asincrona a la ruta `/api/dashboard`.
* La base de datos calcula en tiempo real los indicadores basicos del negocio a traves de consultas de agregacion (`DashboardDAO`): total de conductores, vehiculos en taller, vehiculos en uso, unidades disponibles y la sumatoria historica de gastos de mantenimiento.
* Los datos se representan en paneles interactivos visualmente impactantes y graficos dinamicos de distribucion de estados.

### 3. Gestion Operativa de Entidades (CRUD Completo)
Todas las entidades cuentan con flujos CRUD completos que operan de forma asincrona para evitar recargas completas de pagina:
* **Conductores**: CRUD completo que permite el registro, visualizacion, edicion y eliminacion. Los formularios cuentan con validaciones automaticas en frontend y backend (por ejemplo, validando formato de correo electronico y obligatoriedad de cedula/licencia).
* **Vehiculos**: Registro y edicion de vehiculos. Cada vehiculo nuevo se crea por defecto con el estado inicial `DISPONIBLE`.
* **Usuarios (Mapeados como Tarjetas)**: Para mejorar la experiencia de usuario, este modulo se renderiza mediante tarjetas (cards) horizontales en lugar de una tabla clasica. Muestra el estado del usuario e incorpora un flujo logico de eliminacion: de acuerdo con las politicas de auditoria de la empresa, los usuarios no se eliminan fisicamente, sino que se marcan como INACTIVOS (desactivados), impidiendo su inicio de sesion futuro.

### 4. Ciclo de Vida de las Asignaciones y Mantenimientos
El sistema gestiona dinamicamente los estados de los vehiculos para evitar colisiones operativas:
* **Asignaciones**: 
  * Permite asociar un conductor con un vehiculo.
  * Para garantizar la consistencia, el formulario de creacion carga dinamicamente desplegables interactivos con la lista de conductores registrados y vehiculos disponibles en base de datos.
  * Al realizar una asignacion exitosa, el estado del vehiculo cambia automaticamente a `EN_USO`.
  * Mientras un vehiculo este en estado `EN_USO`, no podra ser asignado a otro conductor ni podra ser enviado a mantenimiento.
  * Al finalizar la asignacion desde el panel, la relacion se da por terminada y el estado del vehiculo regresa instantaneamente a `DISPONIBLE`.
* **Mantenimientos**:
  * Un vehiculo en estado `DISPONIBLE` puede ser enviado al taller mecanico registrando un mantenimiento (indicando taller, motivo y costo proyectado).
  * Al registrarse el mantenimiento, el estado del vehiculo cambia a `MANTENIMIENTO`.
  * Durante su estancia en taller, el vehiculo queda bloqueado para nuevas asignaciones.
  * Al finalizar el mantenimiento desde la interfaz, el vehiculo es retornado a la flota y vuelve a estar en estado `DISPONIBLE`.

### 5. Funcionalidades Globales del Sistema
* **Buscador Universal Superior**: Incorpora una barra de busqueda global en el menu superior. El usuario puede ingresar texto y el sistema buscara coincidencias en tiempo real a lo largo de conductores, vehiculos, asignaciones, mantenimientos y usuarios. Al hacer clic en cualquiera de los resultados devueltos por el buscador, la aplicacion redirige automaticamente al usuario al panel correspondiente y resalta/despliega los detalles de la entidad consultada.
* **Modo Oscuro Dinamico**: El menu superior contiene un boton selector de tema. Al activarlo, se alternan las clases del cuerpo de la pagina y se redefinen las variables CSS en tiempo real, ofreciendo un modo oscuro sofisticado y descansado para operaciones nocturnas, con persistencia en el navegador.
* **Alertas Emergentes (Toasts)**: Las notificaciones tradicionales del navegador (como el comando alert de JS) se han reemplazado por un sistema elegante de toasters emergentes personalizados en la esquina inferior derecha. Cada vez que una operacion de creacion, actualizacion, eliminacion o inicio de sesion tiene exito o genera algun error en el servidor, se dispara un toast estilizado con colores especificos segun la naturaleza del evento.

---

## Tecnologias Utilizadas

El stack tecnologico del proyecto fue elegido cuidadosamente para balancear rendimiento, mantenibilidad, velocidad de desarrollo y una experiencia de usuario moderna:

### Backend
* **Java 17 (LTS)**: El lenguaje principal de programacion del servidor. Se selecciono por su tipado fuerte, alto rendimiento en entornos multitarea, portabilidad absoluta y soporte a largo plazo.
* **Javalin (v6.1.3)**: Un framework de desarrollo web extremadamente ligero para Java y Kotlin. A diferencia de soluciones robustas pero pesadas como Spring Boot, Javalin proporciona un arranque instantaneo, un consumo minimo de memoria y un enrutamiento REST intuitivo, lo que facilita el desarrollo agil de la API y el servicio eficiente de archivos estaticos.
* **Jackson Databind**: Libreria estandar de facto en Java para el procesamiento de JSON. Se utiliza para la serializacion automatica de objetos del modelo Java a cadenas JSON y la deserializacion de las solicitudes entrantes del frontend.
* **JDBC & MySQL Connector**: Conector nativo de MySQL para Java. Se prefirio usar acceso directo JDBC para optimizar la velocidad de ejecucion y evitar la sobrecarga cognitiva y de rendimiento que introducen los mapeadores ORM pesados (como Hibernate) en sistemas transaccionales directos.
* **BCrypt**: Algoritmo de hashing seguro y adaptativo para contraseñas. Implementado para garantizar que las contraseñas de los usuarios nunca se almacenen en texto plano en la base de datos, cumpliendo con estandares internacionales de seguridad informatica.

### Frontend
* **HTML5**: Estructuracion semantica de la interfaz, utilizando contenedores modernos que facilitan la accesibilidad y la indexacion de los componentes del Single Page Application.
* **CSS3 (Vanilla CSS con Variables / Custom Properties)**: Permite implementar un sistema de diseño premium, cohesivo y altamente personalizable. El uso de variables CSS facilita la transicion instantanea del tema visual (modo claro a modo oscuro) y permite esculpir las sombras de Soft UI, los bordes ergonomicos mas gruesos de los botones y el efecto traslucido de Glassmorphism sin necesidad de compilar frameworks externos.
* **Vanilla JavaScript (ES6+)**: Logica del cliente pura, robusta y dinamica. Al prescindir de frameworks complejos como React o Angular, la aplicacion elimina el peso de dependencias innecesarias, cargando al instante en cualquier dispositivo. Utiliza la API moderna de Fetch para interactuar asincronamente con la API REST del backend, gestionando el renderizado dinamico del DOM, el ruteo de paneles, los estados de sesion, la busqueda global y la instanciacion de ventanas modales.

### Base de Datos
* **MySQL 8.0**: Servidor de base de datos relacional robusto y de alto desempeño. Garantiza la integridad referencial y consistencia de toda la informacion transaccional del negocio, gestionando eficientemente indices y restricciones de claves foraneas entre las tablas de conductores, vehiculos, asignaciones, mantenimientos y usuarios.

---

## Arbol de Archivos (File Tree)

La estructura del proyecto sigue la organizacion estandar de Maven para proyectos en Java, separando de forma clara los archivos de codigo fuente de los recursos de configuracion y frontend:

```text
crm-swing-app/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── flota/
        │           └── crm/
        │               ├── App.java
        │               ├── config/
        │               │   └── DatabaseConnection.java
        │               ├── dao/
        │               │   ├── AsignacionDAO.java
        │               │   ├── ConductorDAO.java
        │               │   ├── DashboardDAO.java
        │               │   ├── MantenimientoDAO.java
        │               │   ├── UsuarioDAO.java
        │               │   └── VehiculoDAO.java
        │               └── models/
        │                   ├── Asignacion.java
        │                   ├── Conductor.java
        │                   ├── Mantenimiento.java
        │                   ├── Usuario.java
        │                   └── Vehiculo.java
        └── resources/
            ├── database.properties
            ├── schema.sql
            ├── icons/
            └── public/
                ├── app.js
                ├── index.html
                └── styles.css
```

---

## Estructura del Proyecto y Patrones de Diseño

El sistema implementa una arquitectura clasica organizada en capas y patrones de diseño bien establecidos que promueven la separacion de responsabilidades y la facilidad de mantenimiento:

### 1. Capa de Inicializacion y Enrutamiento (App.java)
* **App.java** actua como el nucleo y punto de entrada de la aplicacion. 
* Inicializa el servidor web embebido Javalin en el puerto `8080`.
* Configura el mapeo de recursos estaticos para servir la SPA del frontend ubicada en `src/main/resources/public`.
* Configura la serializacion JSON para usar Jackson.
* Define y expone de forma estructurada los endpoints de la API REST que seran consumidos por el cliente JavaScript (por ejemplo: `/api/login`, `/api/dashboard`, `/api/conductores`, `/api/vehiculos`, `/api/asignaciones`, `/api/mantenimientos` y `/api/usuarios`).
* Gestiona el manejo centralizado de excepciones y estados de respuesta HTTP en la comunicacion cliente-servidor.

### 2. Capa de Configuracion (Config)
* **DatabaseConnection.java** implementa el patron **Singleton** para centralizar y administrar la conexion con la base de datos de MySQL.
* Se encarga de cargar en memoria las credenciales y propiedades de conexion definidas en el archivo [database.properties](file:///c:/Users/sebastian.agudelo/Documents/me/gestion-flota/crm-swing-app/src/main/resources/database.properties).
* Provee un punto unico y seguro de acceso a la conexion de base de datos activa para toda la aplicacion, evitando la apertura redundante e ineficiente de sockets de red.

### 3. Capa de Modelo (Models)
* Contiene clases POJO (Plain Old Java Objects) que representan de forma directa las entidades de negocio presentes en el esquema relacional de la base de datos (`Usuario`, `Conductor`, `Vehiculo`, `Asignacion`, `Mantenimiento`).
* Encapsulan las propiedades de cada entidad utilizando encapsulamiento estandar (atributos privados con sus respectivos metodos getter y setter).

### 4. Capa de Acceso a Datos (DAO - Data Access Object)
* Implementa el patron **DAO** para aislar por completo la logica de negocio de la tecnologia de base de datos.
* Cada clase de esta capa (`UsuarioDAO`, `ConductorDAO`, `VehiculoDAO`, etc.) se encarga exclusivamente de ejecutar las sentencias SQL preparadas (PreparedStatements), mapear los conjuntos de resultados de la base de datos (`ResultSet`) a instancias del modelo de objetos en Java, y encapsular el control de transacciones basicas.
* Protege la aplicacion contra ataques de inyeccion SQL mediante la parametrizacion rigurosa de las consultas.

### 5. Capa de Interfaz de Usuario y Presentacion (Public)
* Ubicada en la carpeta `resources/public`, funciona como una aplicacion SPA autocontenida.
* **index.html**: Declara la estructura semantica de toda la aplicacion, conteniendo las secciones de barra de navegacion lateral, barra de menu superior con buscador universal, modales emergentes y el contenedor dinamico donde se inyectan las diferentes vistas segun la accion del usuario.
* **styles.css**: Implementa de forma centralizada la estetica visual Soft UI y Glassmorphism, valiendose de variables de CSS para definir los colores del tema claro y oscuro, sombras complejas para dar sensacion de volumen y profundidad, y reglas responsivas para adaptar la interfaz a diferentes resoluciones de pantalla.
* **app.js**: Controla el ciclo de vida del frontend de la SPA. Administra el estado de la sesion del usuario, maneja la navegacion dinamica mediante la activacion y ocultacion de paneles del DOM, implementa las peticiones Fetch asincronas a los endpoints del servidor web, y procesa la logica interactiva para el buscador global, modales dinamicos de edicion/registro y el despliegue de alertas toast personalizadas.
