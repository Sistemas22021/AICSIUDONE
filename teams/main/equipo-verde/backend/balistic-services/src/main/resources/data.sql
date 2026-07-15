-- =============================================================================
-- 1. CONTROL DE CALIBRES (Sintaxis Nativa de Postgres para Duplicados)
-- =============================================================================
-- Si el 'id' ya existe, ON CONFLICT DO NOTHING evita que el script falle.
INSERT INTO caliber_entity (id_caliber, name, is_delete) VALUES
                                                     (1, '9mm Parabellum', FALSE),
                                                     (2, '.22 Long Rifle', FALSE),
                                                     (3, '.45 ACP', FALSE),
                                                     (4, '.308 Winchester', FALSE),
                                                     (5, '7.62x39mm', FALSE),
                                                     (6, '5.56x45mm NATO', FALSE),
                                                     (7, '.357 Magnum', FALSE),
                                                     (8, '12 gauge', FALSE)
    ON CONFLICT (id_caliber) DO NOTHING;


-- =============================================================================
-- 2. CONTROL DE PROYECTILES (Evita duplicados basados en el 'case_file')
-- =============================================================================
-- NOTA: Para que 'ON CONFLICT (case_file)' funcione, el campo 'case_file'
-- debe tener una restricción UNIQUE (o ser la Primary Key) en tu entidad.

-- Inserción 1: Percusión Anular, Giro Dextrorsum
INSERT INTO bullet_entity (case_file, lands_and_grooves, percussion_type, twist_direction, id_caliber, manufacturer, created_at, is_delete)
VALUES ('EXP-2023-001', 6, 'ANULAR', 'DEXTRORSUM', 1, 'Winchester', '2023-10-15 10:30:00', false)
    ON CONFLICT (id_bullet) DO NOTHING;

-- Inserción 2: Percusión Central, Giro Sinistrorsum
INSERT INTO bullet_entity (case_file, lands_and_grooves, percussion_type, twist_direction, id_caliber, manufacturer, created_at, is_delete)
VALUES ('EXP-2023-045', 4, 'CENTRAL', 'SINISTRORSUM', 2, 'Remington', '2023-11-20 15:45:00', false)
    ON CONFLICT (id_bullet) DO NOTHING;

-- Inserción 3: Percusión Eléctrica, Sin giro (None)
INSERT INTO bullet_entity (case_file, lands_and_grooves, percussion_type, twist_direction, id_caliber, manufacturer, created_at, is_delete)
VALUES ('EXP-2024-012', 0, 'ELECTRICA', 'NONE', 1, 'Federal Premium', '2024-01-05 09:00:00', false)
    ON CONFLICT (id_bullet) DO NOTHING;

-- Inserción 4: Caso de percusión Lateral y eliminado lógicamente
INSERT INTO bullet_entity (case_file, lands_and_grooves, percussion_type, twist_direction, id_caliber, manufacturer, created_at, is_delete)
VALUES ('EXP-2022-099', 5, 'LATERAL', 'DEXTRORSUM', 2, 'Magtech', '2022-05-12 18:20:00', true)
    ON CONFLICT (id_bullet) DO NOTHING;

-- Inserción 5: Registro reciente
INSERT INTO bullet_entity (case_file, lands_and_grooves, percussion_type, twist_direction, id_caliber, manufacturer, created_at, is_delete)
VALUES ('EXP-2024-150', 8, 'CENTRAL', 'DEXTRORSUM', 1, 'Hornady', CURRENT_TIMESTAMP, false)
    ON CONFLICT (id_bullet) DO NOTHING;


-- =============================================================================
-- 3. VISTA DE AUDITORÍA (v_audit_logs)
-- =============================================================================
DROP VIEW IF EXISTS v_audit_logs CASCADE;
CREATE OR REPLACE VIEW v_audit_logs AS
SELECT
    CONCAT(CAST(b.rev AS VARCHAR), '_BULLET_', CAST(b.id_bullet AS VARCHAR)) AS id,
    b.rev AS rev,
    r.revtstmp AS rev_timestamp,
    b.revtype AS rev_type,
    'BULLET' AS entity_type,
    CAST(b.id_bullet AS VARCHAR) AS entity_id,
    r.operator AS operator
FROM bullet_entity_aud b
         JOIN custom_rev_info r ON b.rev = r.rev

UNION ALL

SELECT
    CONCAT(CAST(bi.rev AS VARCHAR), '_IMAGES_', CAST(bi.uuid_bullet_images AS VARCHAR)) AS id,
    bi.rev AS rev,
    r.revtstmp AS rev_timestamp,
    bi.revtype AS rev_type,
    'IMAGES' AS entity_type,
    CAST(bi.uuid_bullet_images AS VARCHAR) AS entity_id,
    r.operator AS operator
FROM bullet_images_entity_aud bi
         JOIN custom_rev_info r ON bi.rev = r.rev;

