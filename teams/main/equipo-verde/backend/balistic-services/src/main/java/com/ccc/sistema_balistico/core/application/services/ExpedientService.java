package com.ccc.sistema_balistico.core.application.services;

import com.ccc.sistema_balistico.core.application.dto.ExpedientDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpedientService {
    ExpedientDTO createExpedient(ExpedientDTO expedientDTO);
    ExpedientDTO updateExpedient(Long idExpedient, ExpedientDTO expedientDTO);
    ExpedientDTO getExpedientById(Long idExpedient);
    Page<ExpedientDTO> getAllExpedients(String keyword, Pageable pageable);
    void deleteExpedient(Long idExpedient);
}
