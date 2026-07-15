package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Table(name = "v_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Subselect("select * from v_audit_logs")
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
