# Sistema de Gestion de Proyectos

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
