package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.CaliberDTO;
import com.ccc.sistema_balistico.core.application.services.CaliberService;
import com.ccc.sistema_balistico.core.infrastructure.in.rest.mapper.CaliberMapper;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.CaliberEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.CaliberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaliberImpl implements CaliberService {

    @Autowired
    private CaliberRepository caliberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CaliberDTO> searchCalibers(String query) {
        if (query == null) query = "";
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CaliberEntity> caliberEntities = caliberRepository.findByNameContainingIgnoreCaseAndIsDeleteFalse(query.trim(), pageRequest);
        return caliberEntities.map(CaliberMapper::toDTO);
    }
}
