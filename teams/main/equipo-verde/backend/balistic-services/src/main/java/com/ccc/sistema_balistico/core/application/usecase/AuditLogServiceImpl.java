package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.services.AuditLogService;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.AuditLogViewEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.AuditLogViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogViewRepository auditLogViewRepository;

    public AuditLogServiceImpl(AuditLogViewRepository auditLogViewRepository) {
        this.auditLogViewRepository = auditLogViewRepository;
    }

    @Override
    public Page<AuditLogViewEntity> getAuditLogs(Pageable pageable) {
        return auditLogViewRepository.findAll(pageable);
    }
}
