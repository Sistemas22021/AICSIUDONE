package com.ccc.sistema_balistico.core.application.services;

import com.ccc.sistema_balistico.core.application.dto.CaliberDTO;
import org.springframework.data.domain.Page;

public interface CaliberService {
    Page<CaliberDTO> searchCalibers(String query);
}
