package com.ccc.sistema_balistico.core.application.services;

import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.AuditLogViewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    Page<AuditLogViewEntity> getAuditLogs(Pageable pageable);
}
