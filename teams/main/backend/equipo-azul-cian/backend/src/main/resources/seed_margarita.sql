-- =======================================================================================
-- SCRIPT DE PRE-CARGA DE DATOS (SEEDER) - ISLA DE MARGARITA, NUEVA ESPARTA
-- =======================================================================================

-- 1. LIMPIAR LAS TABLAS EXISTENTES
-- (Se usa TRUNCATE con CASCADE para borrar todo rápidamente y resetear los IDs autoincrementables)
TRUNCATE TABLE assignments, incidents, patrols RESTART IDENTITY CASCADE;

-- 2. INSERTAR PATRULLAS (4 Patrullas en distintos puntos de Margarita)
-- Coordenadas base (Margarita): Lat ~10.9 a 11.1 | Lng ~-64.2 a -63.8
INSERT INTO patrols (code, officer_name, latitude, longitude, status, last_updated) VALUES
('PAT-001', 'Cap. Miguel Rodríguez', 10.9576, -63.8687, 'AVAILABLE', CURRENT_TIMESTAMP), -- Porlamar
('PAT-002', 'Tte. Ana García', 10.9950, -63.8794, 'AVAILABLE', CURRENT_TIMESTAMP),      -- Pampatar
('PAT-003', 'Sgto. Luis Bermúdez', 11.0827, -63.9806, 'AVAILABLE', CURRENT_TIMESTAMP),   -- Juan Griego
('PAT-004', 'Of. María Fernández', 10.9856, -64.1678, 'AVAILABLE', CURRENT_TIMESTAMP);    -- Punta de Piedras

-- 3. INSERTAR INCIDENTES (5 Incidentes distribuidos en la Isla)
INSERT INTO incidents (type, description, latitude, longitude, status, priority, created_at, updated_at) VALUES
('Robo a mano armada', 'Sujeto armado robó un comercio cerca de la Plaza Bolívar de Porlamar.', 10.9540, -63.8500, 'ACTIVE', 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Accidente de Tránsito', 'Choque entre dos vehículos en la Avenida Jovito Villalba, sin heridos graves.', 10.9833, -63.8667, 'ACTIVE', 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Alteración del Orden', 'Grupo de personas causando disturbios en el malecón de Juan Griego.', 11.0800, -63.9850, 'ACTIVE', 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Emergencia Médica', 'Turista con desmayo por golpe de calor en Playa El Agua.', 11.1444, -63.8647, 'ACTIVE', 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Incendio Estructural', 'Conato de incendio en un galpón industrial cerca del terminal de ferrys.', 10.9800, -64.1700, 'ACTIVE', 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Opcionalmente podrías insertar asignaciones pre-existentes aquí si quisieras:
-- INSERT INTO assignments (assigned_at, incident_id, patrol_id) VALUES (CURRENT_TIMESTAMP, 1, 1);
