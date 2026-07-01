package com.ccc.sistema_balistico.core.application.services;

import com.ccc.sistema_balistico.core.application.dto.CorrelationResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CorrelationService {


    Page<CorrelationResultDTO> correlateBullet(Long evidenceId, Pageable pageable);
}
