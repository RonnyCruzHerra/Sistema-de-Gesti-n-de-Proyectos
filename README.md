# Sistema de Gestion de Proyectos

<!-- local-development:start -->
## Desarrollo local

Consulta [RUNNING.md](RUNNING.md) para ubicar cada componente, abrirlo en su IDE, compilarlo y conocer las comprobaciones realizadas y los requisitos pendientes.
<!-- local-development:end -->

# Project Management System - Java Swing + PostgreSQL

## Requisitos
- Java 17
- Maven
- PostgreSQL + pgAdmin
- NetBeans 21+

## Configuración rápida
1. Ejecutar `scripts/create_db.sql` en PostgreSQL.
2. Copiar `src/main/resources/config.example.properties` a `config.properties` y actualizar credenciales.
3. En NetBeans: Clean and Build.

## Estructura
- modelo/
- dao/
- servicio/
- ui/
- util/

## Día 2 – Modelado de Entidades y Creación de la Base de Datos

### Objetivo
Diseñar y crear la estructura de la base de datos **gestion_proyectos** en PostgreSQL, asegurando integridad referencial, normalización y seguridad en el acceso.

---

### 1️. Modelado de Entidades
Se definieron las entidades principales y sus relaciones, basadas en los requerimientos funcionales:

- **Usuario** (`id_usuario`, `nombre`, `email`, `password_hash`, `rol`, `fecha_creacion`)
- **Proyecto** (`id_proyecto`, `nombre`, `descripcion`, `fecha_inicio`, `fecha_fin`, `estado`)
- **Tarea** (`id_tarea`, `id_proyecto`, `id_usuario_asignado`, `titulo`, `descripcion`, `fecha_limite`, `completada`)
- **Recurso** (`id_recurso`, `id_proyecto`, `nombre`, `tipo`, `url`)
- **LogErrores** (`id_error`, `mensaje`, `fecha`, `stacktrace`)

Relacionamiento:
- Un **Usuario** puede tener 0 o muchas **Tareas**.
- Un **Proyecto** puede tener muchas **Tareas** y muchos **Recursos**.
- **LogErrores** es independiente para registrar errores del sistema.

---

### 2️. Creación de la Base de Datos y Tablas
**Ubicación:** `scripts/init.sql`  
**Herramienta:** pgAdmin 4 → Query Tool  

```sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE usuario (
    id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    rol VARCHAR(50) CHECK (rol IN ('ADMIN', 'USUARIO')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE proyecto (
    id_proyecto UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado VARCHAR(50) CHECK (estado IN ('EN_PROGRESO', 'COMPLETADO', 'PENDIENTE'))
);

CREATE TABLE tarea (
    id_tarea UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_proyecto UUID REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
    id_usuario_asignado UUID REFERENCES usuario(id_usuario) ON DELETE SET NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_limite DATE,
    completada BOOLEAN DEFAULT FALSE
);

CREATE TABLE recurso (
    id_recurso UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_proyecto UUID REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
    nombre VARCHAR(150) NOT NULL,
    tipo VARCHAR(50),
    url TEXT
);

CREATE TABLE log_errores (
    id_error SERIAL PRIMARY KEY,
    mensaje TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stacktrace TEXT
);


# Sistema de Gestión de Proyectos (PMS) - Día 3

## Resumen del Día 3

Durante este día, se implementaron los Data Access Objects (DAOs) para todas las entidades del sistema, asegurando una comunicación segura y eficiente con la base de datos PostgreSQL. Además, se configuró un pool de conexiones con HikariCP para optimizar el manejo de conexiones y evitar fugas o bloqueos.

---

## Qué se hizo

### 1. Implementación de DAOs

Se crearon las clases DAO para las siguientes entidades, ubicadas en el paquete `com.mycompany.pms.java.swing.dao`:

- `UsuarioDAO.java`
- `ProyectoDAO.java`
- `TareaDAO.java`
- `RecursoDAO.java`
- `LogErrorDAO.java`

Cada DAO incluye métodos CRUD (Crear, Leer, Actualizar, Eliminar) usando **PreparedStatements** para proteger contra SQL Injection.

En el caso de `UsuarioDAO`, se implementó el hash de contraseñas usando **BCrypt**, asegurando que las contraseñas no se almacenen en texto plano.

### 2. Configuración de conexión segura con HikariCP

Se implementó la clase `DBConnection` en el paquete `com.mycompany.pms.java.swing.util`, que:

- Lee parámetros de conexión desde un archivo `db.properties` en la carpeta `resources/`.
- Permite sobrescribir configuración con variables de entorno para facilitar despliegue en producción.
- Usa un pool de conexiones para optimizar el acceso concurrente a la base de datos.
- Provee métodos para obtener conexiones y cerrar el pool.

### 3. Adaptación de DAOs para usar la conexión centralizada

Todos los DAOs fueron modificados para obtener conexiones a través de `DBConnection.getConnection()`, unificando la configuración y mejorando la mantenibilidad.

---

## Archivos clave creados/modificados

- `src/dao/UsuarioDAO.java`  
- `src/dao/ProyectoDAO.java`  
- `src/dao/TareaDAO.java`  
- `src/dao/RecursoDAO.java`  
- `src/dao/LogErrorDAO.java`  
- `src/util/DBConnection.java`  
- `resources/db.properties`

---

## Cómo probar la conexión

En la clase principal `PmsJavaSwing.java` se incluyó un simple test para verificar la conexión a la base de datos:

```java
public class PmsJavaSwing {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Conexion exitosa a PostgreSQL");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


# Avances del Día 4

Durante el Día 4, el proyecto se enfocó en fortalecer la lógica de negocio mediante la implementación de los servicios (Service) para las entidades principales:

UsuarioService

ProyectoService

TareaService

RecursoService

Objetivos alcanzados

Validaciones básicas de negocio:

Campos obligatorios (nombre, email, password).

Fechas coherentes para proyectos y tareas.

Unicidad de usuarios por email.

Seguridad:

Hashing de contraseñas usando BCrypt.

Uso de PreparedStatement para evitar SQL Injection.

Gestión de errores:

Excepciones capturadas y registradas en la tabla log_errores mediante LogErrorDAO.

Conexión estable con PostgreSQL:

Pool de conexiones gestionado por HikariCP.

Archivo de configuración db.properties para parámetros de conexión.

## Entregables del Día 4

Clases de servicio funcionales:

UsuarioService.java

ProyectoService.java

TareaService.java

RecursoService.java

Registro de errores automatizado con LogErrorDAO.

Base de datos gestion_proyectos con tablas creadas (usuario, proyecto, tarea, recurso, log_errores).

Conexión y pool de conexiones configurados y funcionando.

Validaciones básicas implementadas.

<!-- build-repairs-20261115:start -->
## Reparación y comprobación — 2026-11-15

- Se documentaron los comandos de compilación y comprobación con Maven/JDK 24 y se conservaron los cambios previos de README y exclusiones.

Comprobado:

- Maven package terminó correctamente para `pms-java-swing/pom.xml`.

Comandos:

```powershell
mvn -f "pms-java-swing/pom.xml" package
```

Alcance pendiente:

- Compilar y ejecutar la suite disponible no demuestra todos los flujos gráficos, de datos o de instalación.

<!-- build-repairs-20261115:end -->
