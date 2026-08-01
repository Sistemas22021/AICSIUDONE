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
