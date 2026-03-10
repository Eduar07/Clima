-- ============================================================
-- PROYECTO: Vacaciones-Clima
-- ARCHIVO:  schema.sql
-- AUTOR:    Eduar Guerrero
-- RAMA:     feature/database
-- DESCRIPCION: Script para crear la base de datos y la tabla
--              principal del historial de consultas de clima
-- ============================================================

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS vacaciones_clima
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Seleccionar la base de datos
USE vacaciones_clima;

-- ============================================================
-- TABLA: consultas_clima
-- Guarda cada consulta que un usuario realiza en el sistema
-- ============================================================
CREATE TABLE IF NOT EXISTS consultas_clima (

    -- Identificador unico, se incrementa automaticamente
    -- Ejemplo: 1, 2, 3, 4...
    id INT AUTO_INCREMENT PRIMARY KEY,

    -- Ciudad que el usuario consulto
    -- NOT NULL significa que este campo es obligatorio
    ciudad VARCHAR(100) NOT NULL,

    -- Pais de la ciudad (codigo de 2 letras)
    -- Ejemplo: CO, US, MX, ES
    pais VARCHAR(10),

    -- Temperatura en grados Celsius
    -- DECIMAL(5,2) permite valores como 14.50 o -3.20
    temperatura DECIMAL(5,2) NOT NULL,

    -- Descripcion del clima que devuelve la API
    -- Ejemplo: "lluvia ligera", "cielo despejado", "nublado"
    condicion_climatica VARCHAR(200) NOT NULL,

    -- Porcentaje de humedad (0 a 100)
    humedad INT,

    -- Velocidad del viento en km/h
    velocidad_viento DECIMAL(6,2),

    -- Recomendacion de vestimenta generada por el sistema
    -- TEXT permite textos largos sin limite fijo
    recomendacion TEXT NOT NULL,

    -- Indica si hay lluvia o no
    -- TRUE = hay lluvia, FALSE = no hay lluvia
    hay_lluvia BOOLEAN DEFAULT FALSE,

    -- Fecha y hora exacta de la consulta
    -- Se asigna automaticamente al insertar el registro
    fecha_consulta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Direccion IP del usuario (opcional)
    ip_usuario VARCHAR(45)
);

-- ============================================================
-- DATOS DE EJEMPLO
-- Sirven para verificar que la tabla funciona correctamente
-- ============================================================
INSERT INTO consultas_clima
    (ciudad, pais, temperatura, condicion_climatica,
     humedad, velocidad_viento, recomendacion, hay_lluvia)
VALUES
    (
        'Bogota', 'CO', 14.5, 'Nublado',
        80, 15.0,
        'Usa chaqueta o sueter. ',
        FALSE
    ),
    (
        'Barranquilla', 'CO', 32.0, 'Soleado',
        65, 20.0,
        'Usa ropa ligera, hace calor. ',
        FALSE
    ),
    (
        'Cartagena', 'CO', 35.0, 'Lluvia ligera',
        85, 25.0,
        'Usa ropa ligera, hace calor. Lleva paraguas o impermeable.',
        TRUE
    );

-- ============================================================
-- CONSULTAS DE VERIFICACION
-- Ejecuta estas consultas para verificar que todo quedo bien
-- ============================================================

-- Ver todos los registros insertados
SELECT * FROM consultas_clima;

-- Ver cuantos registros hay en la tabla
SELECT COUNT(*) AS total_registros FROM consultas_clima;

-- Ver solo las consultas donde hubo lluvia
SELECT ciudad, temperatura, recomendacion
FROM consultas_clima
WHERE hay_lluvia = TRUE;