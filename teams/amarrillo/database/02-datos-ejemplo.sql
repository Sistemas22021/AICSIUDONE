-- ============================================================================
-- Nexo Criminal - Datos de ejemplo (15 sucesos con vinculos detectables)
-- ============================================================================
-- Este script llena la base con datos DISENADOS para que el motor Red Thread
-- descubra vinculos: nodos logisticos, vehiculos escolta, intermediarios y
-- modus operandi compartido. Coordenadas reales de Isla Margarita.
--
-- REQUISITOS: PostGIS instalado (la columna geom se calcula por trigger).
-- USO: psql "URL" -f database/02-datos-ejemplo.sql
-- ============================================================================

BEGIN;

-- Limpieza previa (respeta el orden de las claves foraneas)
DELETE FROM suceso_testigo;
DELETE FROM alerta;
DELETE FROM vinculo;
DELETE FROM suceso;
DELETE FROM avistamiento;
DELETE FROM relacion;
DELETE FROM vehiculo;
DELETE FROM foto_desaparecida;
DELETE FROM persona_desaparecida;
DELETE FROM persona;
DELETE FROM ubicacion;

-- ============================================================================
-- 1. UBICACIONES (coordenadas reales de Isla Margarita)
-- ============================================================================
INSERT INTO ubicacion (direccion, latitud, longitud, tipo, nodo_sospechoso, creado_en) VALUES
('Taller El Yunque, Los Robles, Pampatar',            10.9930, -63.7960, 'TALLER',            true,  now()),
('Galpon Zona Industrial, Los Millanes, La Asuncion', 11.0290, -63.8630, 'GALPON',            true,  now()),
('Av. Bolivar, Porlamar',                             10.9577, -63.8492, 'COMERCIO',          false, now()),
('C.C. Sambil, Pampatar',                             10.9840, -63.8010, 'COMERCIO',          false, now()),
('Terreno baldio, El Cardon, Porlamar',               10.9420, -63.8600, 'TERRENO_BALDIO',    true,  now()),
('Av. 4 de Mayo, Porlamar',                           10.9610, -63.8560, 'TRANSPORTE_PUBLICO',false, now()),
('Domicilio, Urb. Playa El Angel, Pampatar',          10.9760, -63.8180, 'DOMICILIO',         false, now()),
('Cajero Banco, Av. Santiago Marino, Porlamar',       10.9595, -63.8470, 'CAJERO',            false, now()),
('Playa Parguito, El Tirano',                         11.0680, -63.8090, 'OTRO',              false, now()),
('Domicilio, Juan Griego',                            11.0810, -63.9640, 'DOMICILIO',         false, now());

-- ============================================================================
-- 2. PERSONAS (victimas, sospechosos, testigos, propietarios, intermediarios)
-- ============================================================================
INSERT INTO persona (documento, nombre, apellido, alias, fecha_nacimiento, rol, telefono, estado, creado_en, actualizado_en) VALUES
('V-12345678', 'Carlos',    'Rodriguez', NULL,        '1985-03-12', 'VICTIMA',       '0414-1234567', 'ACTIVO', now(), now()),
('V-13456789', 'Maria',     'Gonzalez',  NULL,        '1990-07-25', 'VICTIMA',       '0416-2345678', 'ACTIVO', now(), now()),
('V-14567890', 'Jose',      'Martinez',  NULL,        '1978-11-03', 'VICTIMA',       '0424-3456789', 'ACTIVO', now(), now()),
('V-15678901', 'Ana',       'Perez',     NULL,        '1995-01-18', 'VICTIMA',       '0412-4567890', 'ACTIVO', now(), now()),
('V-16789012', 'Luis',      'Hernandez', 'El Flaco',  '1982-09-30', 'SOSPECHOSO',    '0426-5678901', 'ACTIVO', now(), now()),
('V-17890123', 'Pedro',     'Ramirez',   'El Chino',  '1988-05-14', 'SOSPECHOSO',    '0414-6789012', 'ACTIVO', now(), now()),
('V-18901234', 'Miguel',    'Torres',    'El Gordo',  '1975-12-22', 'SOSPECHOSO',    '0416-7890123', 'ACTIVO', now(), now()),
('V-19012345', 'Rosa',      'Diaz',      NULL,        '1992-04-08', 'INTERMEDIARIO', '0424-8901234', 'ACTIVO', now(), now()),
('V-20123456', 'Juana',     'Fernandez', NULL,        '1986-08-19', 'TESTIGO',       '0412-9012345', 'ACTIVO', now(), now()),
('V-21234567', 'Francisco', 'Moreno',    NULL,        '1970-02-27', 'TESTIGO',       '0426-0123456', 'ACTIVO', now(), now()),
('V-22345678', 'Elena',     'Jimenez',   NULL,        '1998-06-11', 'PROPIETARIO',   '0414-1122334', 'ACTIVO', now(), now()),
('V-23456789', 'Roberto',   'Ruiz',      NULL,        '1983-10-05', 'PROPIETARIO',   '0416-2233445', 'ACTIVO', now(), now());

-- ============================================================================
-- 3. VEHICULOS (varios robados; algunos de apoyo para escolta)
-- ============================================================================
INSERT INTO vehiculo (placa, marca, modelo, anio, color, estado, propietario_id, chasis, declaracion, creado_en) VALUES
('AB123CD', 'Toyota',    'Corolla',  2019, 'Gris',   'ROBADO',         (SELECT id FROM persona WHERE documento='V-12345678'), 'CH0001AAA', 'Robado a mano armada.', now()),
('EF456GH', 'Chevrolet', 'Aveo',     2018, 'Blanco', 'ROBADO',         (SELECT id FROM persona WHERE documento='V-13456789'), 'CH0002BBB', 'Sustraido del estacionamiento.', now()),
('IJ789KL', 'Ford',      'Fiesta',   2020, 'Rojo',   'ROBADO',         (SELECT id FROM persona WHERE documento='V-14567890'), 'CH0003CCC', 'Robo con violencia.', now()),
('MN012OP', 'Hyundai',   'Accent',   2017, 'Negro',  'ROBADO',         (SELECT id FROM persona WHERE documento='V-15678901'), 'CH0004DDD', 'Interceptado en la via.', now()),
('QR345ST', 'Jeep',      'Cherokee', 2015, 'Verde',  'VEHICULO_APOYO', (SELECT id FROM persona WHERE documento='V-22345678'), 'CH0005EEE', 'Visto acompanando robos.', now()),
('UV678WX', 'Toyota',    'Hilux',    2016, 'Plata',  'VEHICULO_APOYO', (SELECT id FROM persona WHERE documento='V-23456789'), 'CH0006FFF', 'Vehiculo escolta sospechoso.', now()),
('YZ901AB', 'Chevrolet', 'Optra',    2014, 'Azul',   'RECUPERADO',     (SELECT id FROM persona WHERE documento='V-22345678'), 'CH0007GGG', 'Recuperado tras operativo.', now());

-- ============================================================================
-- 4. RELACIONES SOCIALES (para la regla de intermediario / circulo de confianza)
-- ============================================================================
INSERT INTO relacion (persona_a_id, persona_b_id, tipo_relacion, peso, creado_en) VALUES
((SELECT id FROM persona WHERE documento='V-12345678'), (SELECT id FROM persona WHERE documento='V-19012345'), 'AMIGO',   2, now()),
((SELECT id FROM persona WHERE documento='V-19012345'), (SELECT id FROM persona WHERE documento='V-16789012'), 'CONTACTO_TELEFONICO', 3, now()),
((SELECT id FROM persona WHERE documento='V-13456789'), (SELECT id FROM persona WHERE documento='V-19012345'), 'FAMILIAR', 2, now()),
((SELECT id FROM persona WHERE documento='V-19012345'), (SELECT id FROM persona WHERE documento='V-17890123'), 'LABORAL',  2, now()),
((SELECT id FROM persona WHERE documento='V-16789012'), (SELECT id FROM persona WHERE documento='V-17890123'), 'AMIGO',   4, now()),
((SELECT id FROM persona WHERE documento='V-17890123'), (SELECT id FROM persona WHERE documento='V-18901234'), 'AMIGO',   4, now());

-- ============================================================================
-- 5. SUCESOS (15 en total)
-- ============================================================================
-- Bloque A: 4 ROBOS cerca del Taller El Yunque (NODO LOGISTICO + MODUS)
INSERT INTO suceso (tipo, fecha_hora, descripcion, modus_operandi, ubicacion_id, vehiculo_id, victima_id, creado_en) VALUES
('ROBO_VEHICULO', '2026-07-10 08:15:00', 'Robo a mano armada de un Toyota Corolla gris.',    'ROBO_ARMADO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Taller El Yunque%'),
   (SELECT id FROM vehiculo  WHERE placa='AB123CD'),
   (SELECT id FROM persona   WHERE documento='V-12345678'), now()),
('ROBO_VEHICULO', '2026-07-10 09:40:00', 'Sustraccion de un Chevrolet Aveo blanco.',         'ROBO_ARMADO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Taller El Yunque%'),
   (SELECT id FROM vehiculo  WHERE placa='EF456GH'),
   (SELECT id FROM persona   WHERE documento='V-13456789'), now()),
('ROBO_VEHICULO', '2026-07-10 11:20:00', 'Robo con violencia de un Ford Fiesta rojo.',       'ROBO_ARMADO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Taller El Yunque%'),
   (SELECT id FROM vehiculo  WHERE placa='IJ789KL'),
   (SELECT id FROM persona   WHERE documento='V-14567890'), now()),
('ROBO_VEHICULO', '2026-07-10 13:05:00', 'Interceptacion y robo de un Hyundai Accent negro.','ROBO_ARMADO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Taller El Yunque%'),
   (SELECT id FROM vehiculo  WHERE placa='MN012OP'),
   (SELECT id FROM persona   WHERE documento='V-15678901'), now());

-- Bloque B: 3 AVISTAMIENTOS del vehiculo de apoyo (VEHICULO ESCOLTA)
INSERT INTO suceso (tipo, fecha_hora, descripcion, modus_operandi, ubicacion_id, vehiculo_id, creado_en) VALUES
('AVISTAMIENTO', '2026-07-10 08:20:00', 'Jeep Cherokee verde escoltando la salida del robo.', 'ESCOLTA',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Taller El Yunque%'),
   (SELECT id FROM vehiculo  WHERE placa='QR345ST'), now()),
('AVISTAMIENTO', '2026-07-10 09:45:00', 'Mismo Jeep Cherokee verde en segundo robo.',         'ESCOLTA',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Taller El Yunque%'),
   (SELECT id FROM vehiculo  WHERE placa='QR345ST'), now()),
('AVISTAMIENTO', '2026-07-10 11:25:00', 'Toyota Hilux plata acompanando la huida.',           'ESCOLTA',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Taller El Yunque%'),
   (SELECT id FROM vehiculo  WHERE placa='UV678WX'), now());

-- Bloque C: 2 TRANSACCIONES sospechosas en el galpon (desguace)
INSERT INTO suceso (tipo, fecha_hora, descripcion, modus_operandi, ubicacion_id, creado_en) VALUES
('TRANSACCION', '2026-07-11 15:00:00', 'Movimiento de repuestos sospechoso en el galpon.', 'DESGUACE',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Galpon%'), now()),
('TRANSACCION', '2026-07-11 18:30:00', 'Venta de piezas sin factura en el galpon.',         'DESGUACE',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Galpon%'), now());

-- Bloque D: 3 DESAPARICIONES en zona cercana (CLUSTER)
INSERT INTO suceso (tipo, fecha_hora, descripcion, modus_operandi, ubicacion_id, victima_id, creado_en) VALUES
('DESAPARICION', '2026-07-12 20:00:00', 'Persona vista por ultima vez cerca del terreno baldio.', 'RAPTO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Terreno baldio%'),
   (SELECT id FROM persona   WHERE documento='V-15678901'), now()),
('DESAPARICION', '2026-07-13 21:30:00', 'Desaparicion reportada cerca del mismo sector.',          'RAPTO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Terreno baldio%'),
   (SELECT id FROM persona   WHERE documento='V-14567890'), now()),
('DESAPARICION', '2026-07-14 19:15:00', 'Tercer caso en la zona de El Cardon.',                    'RAPTO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Terreno baldio%'),
   (SELECT id FROM persona   WHERE documento='V-13456789'), now());

-- Bloque E: 3 sucesos normales dispersos (control)
INSERT INTO suceso (tipo, fecha_hora, descripcion, modus_operandi, ubicacion_id, vehiculo_id, victima_id, creado_en) VALUES
('ROBO_VEHICULO', '2026-06-28 10:00:00', 'Robo aislado en Juan Griego, sin conexion aparente.', 'HURTO_SIMPLE',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Domicilio, Juan Griego%'),
   (SELECT id FROM vehiculo  WHERE placa='YZ901AB'),
   (SELECT id FROM persona   WHERE documento='V-22345678'), now());
INSERT INTO suceso (tipo, fecha_hora, descripcion, modus_operandi, ubicacion_id, creado_en) VALUES
('AVISTAMIENTO', '2026-06-30 16:45:00', 'Avistamiento rutinario en la playa, sin relevancia.', 'RUTINARIO',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Playa Parguito%'), now()),
('TRANSACCION', '2026-07-01 12:00:00', 'Transaccion legitima registrada en comercio.', 'LEGITIMA',
   (SELECT id FROM ubicacion WHERE direccion LIKE 'Av. Bolivar%'), now());

-- ============================================================================
-- 6. TESTIGOS de algunos sucesos
-- ============================================================================
INSERT INTO suceso_testigo (suceso_id, persona_id) VALUES
((SELECT id FROM suceso WHERE descripcion LIKE 'Robo a mano armada de un Toyota%' LIMIT 1),
 (SELECT id FROM persona WHERE documento='V-20123456')),
((SELECT id FROM suceso WHERE descripcion LIKE 'Robo a mano armada de un Toyota%' LIMIT 1),
 (SELECT id FROM persona WHERE documento='V-21234567')),
((SELECT id FROM suceso WHERE descripcion LIKE 'Robo con violencia de un Ford%' LIMIT 1),
 (SELECT id FROM persona WHERE documento='V-20123456'));

COMMIT;
