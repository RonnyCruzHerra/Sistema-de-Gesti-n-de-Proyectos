-- Habilitar extensión para UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Crear base de datos
CREATE DATABASE gestion_proyectos;

-- Conectar a la nueva base
\c gestion_proyectos;

-- Tabla de usuarios
CREATE TABLE usuario (
    id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    rol VARCHAR(50) CHECK (rol IN ('ADMIN', 'USUARIO')),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de proyectos
CREATE TABLE proyecto (
    id_proyecto UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE,
    fecha_fin DATE,
    estado VARCHAR(50) CHECK (estado IN ('EN_PROGRESO', 'COMPLETADO', 'PENDIENTE'))
);

-- Tabla de tareas
CREATE TABLE tarea (
    id_tarea UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_proyecto UUID REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
    id_usuario_asignado UUID REFERENCES usuario(id_usuario) ON DELETE SET NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_limite DATE,
    completada BOOLEAN DEFAULT FALSE
);

-- Tabla de recursos
CREATE TABLE recurso (
    id_recurso UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_proyecto UUID REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
    nombre VARCHAR(150) NOT NULL,
    tipo VARCHAR(50),
    url TEXT
);

-- Tabla de logs de errores
CREATE TABLE log_errores (
    id_error SERIAL PRIMARY KEY,
    mensaje TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stacktrace TEXT
);


-- Crear usuario para la aplicación
CREATE USER gp_user WITH PASSWORD 'admin123';

-- Asignar privilegios
GRANT CONNECT ON DATABASE gestion_proyectos TO gp_user;
GRANT USAGE ON SCHEMA public TO gp_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO gp_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO gp_user;
