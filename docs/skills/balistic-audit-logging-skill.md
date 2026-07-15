# Skill: Registro de Auditoría Unificado con Hibernate Envers en Spring Boot

Esta skill documenta el proceso y los componentes requeridos para configurar e implementar un log de auditoría basado en **Hibernate Envers** con una vista consolidada expuesta en un endpoint paginado.

## Prerrequisitos
- Spring Boot 3.x/4.x con Spring Data JPA.
- Dependencia de Hibernate Envers compatible agregada al `pom.xml`.

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-envers</artifactId>
</dependency>
```

---

## 1. Configuración de Entidades Auditadas
Añade la anotación `@Audited` de `org.hibernate.envers` a las entidades que requieras auditar:

```java
@Entity
@Audited
public class BulletEntity {
    // ... campos ...
}
```

---

## 2. Personalización de la Revisión (Operador)
Para asociar metadatos (como el usuario operador) a cada transacción de revisión:

### CustomRevisionEntity
```java
@Entity
@Table(name = "custom_rev_info")
@RevisionEntity(CustomRevisionListener.class)
@Getter
@Setter
public class CustomRevisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    @Column(name = "revtstmp")
    private long timestamp;

    private String operator;
}
```

### CustomRevisionListener
```java
public class CustomRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        customRevisionEntity.setOperator("user"); // Reemplazar con lógica de sesión cuando exista
    }
}
```

---

## 3. Vista SQL de Auditoría Consolidada (`v_audit_logs`)
Define la vista SQL en tu archivo de inicialización `data.sql` realizando un `UNION ALL` entre las tablas de auditoría generadas por Envers cruzadas con tu tabla de revisión:

```sql
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
```

---

## 4. Mapeo de la Vista en JPA e Integración en Capa de Datos y Servicio

### AuditLogViewEntity
```java
@Entity
@Immutable
@Table(name = "v_audit_logs")
@Getter
@Setter
public class AuditLogViewEntity {
    @Id
    private String id;
    private Long rev;
    private Long revTimestamp;
    private Integer revType;
    private String entityType;
    private String entityId;
    private String operator;
}
```

### AuditLogViewRepository
```java
@Repository
public interface AuditLogViewRepository extends JpaRepository<AuditLogViewEntity, String> {
}
```

### Endpoint Paginado con Pageable (AuditLogController)
```java
@RestController
@RequestMapping("api/v1/audit-log")
public class AuditLogController {
    
    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditLogViewEntity>> getAuditLogs(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(pageable));
    }
}
```
