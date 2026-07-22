package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.dto.ExpedientDTO;
import com.ccc.sistema_balistico.core.application.services.ExpedientService;
import com.ccc.sistema_balistico.core.domain.exceptions.ExpedientNotFound;
import com.ccc.sistema_balistico.core.infrastructure.in.rest.mapper.ExpedientMapper;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.ExpedientEntity;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.jpa.ExpedientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExpedientImpl implements ExpedientService {

    private final ExpedientRepository expedientRepository;

    @Autowired
    public ExpedientImpl(ExpedientRepository expedientRepository) {
        this.expedientRepository = expedientRepository;
    }

    @Override
    public ExpedientDTO createExpedient(ExpedientDTO expedientDTO) {
        ExpedientEntity entity = ExpedientMapper.toEntity(expedientDTO);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIsDelete(false);
        ExpedientEntity savedEntity = expedientRepository.save(entity);
        return ExpedientMapper.toDTO(savedEntity);
    }

    @Override
    public ExpedientDTO updateExpedient(Long idExpedient, ExpedientDTO expedientDTO) {
        ExpedientEntity existing = expedientRepository.findByIdAndIsDeleteFalse(idExpedient)
                .orElseThrow(() -> new ExpedientNotFound("Expediente no encontrado con el id: " + idExpedient));

        existing.setCaseNumber(expedientDTO.getCaseNumber());
        existing.setDescription(expedientDTO.getDescription());
        existing.setStatus(expedientDTO.getStatus());

        ExpedientEntity updated = expedientRepository.save(existing);
        return ExpedientMapper.toDTO(updated);
    }

    @Override
    public ExpedientDTO getExpedientById(Long idExpedient) {
        ExpedientEntity entity = expedientRepository.findByIdAndIsDeleteFalse(idExpedient)
                .orElseThrow(() -> new ExpedientNotFound("Expediente no encontrado con el id: " + idExpedient));
        return ExpedientMapper.toDTO(entity);
    }

    @Override
    public Page<ExpedientDTO> getAllExpedients(String keyword, Pageable pageable) {
        Page<ExpedientEntity> page;
        if (keyword != null && !keyword.trim().isEmpty()) {
            page = expedientRepository.findAllByIsDeleteFalseAndKeyword(keyword, pageable);
        } else {
            page = expedientRepository.findAllByIsDeleteFalse(pageable);
        }
        return page.map(ExpedientMapper::toDTO);
    }

    @Override
    public void deleteExpedient(Long idExpedient) {
        ExpedientEntity entity = expedientRepository.findByIdAndIsDeleteFalse(idExpedient)
                .orElseThrow(() -> new ExpedientNotFound("Expediente no encontrado con el id: " + idExpedient));
        
        entity.setIsDelete(true);
        expedientRepository.save(entity);
    }
}
