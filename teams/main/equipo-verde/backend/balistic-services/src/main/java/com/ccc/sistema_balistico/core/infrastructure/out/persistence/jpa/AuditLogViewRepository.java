package com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.AuditLogViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogViewRepository extends JpaRepository<AuditLogViewEntity, String> {
}
