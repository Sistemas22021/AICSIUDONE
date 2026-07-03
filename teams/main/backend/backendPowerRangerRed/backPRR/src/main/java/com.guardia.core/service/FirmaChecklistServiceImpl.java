package com.guardia.core.service;

import com.guardia.core.dto.request.FirmarPasoRequest;
import com.guardia.core.dto.response.FirmaChecklistResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.EscenaChecklist;
import com.guardia.core.model.FirmaChecklist;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.EscenaChecklistRepository;
import com.guardia.core.repository.FirmaChecklistRepository;
import com.guardia.core.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class FirmaChecklistServiceImpl implements FirmaChecklistService {

    private final FirmaChecklistRepository firmaRepository;
    private final EscenaChecklistRepository checklistRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public FirmaChecklistResponse firmarPaso(Long pasoChecklistId, FirmarPasoRequest request) {

        EscenaChecklist paso = checklistRepository.findById(pasoChecklistId)
                .orElseThrow(() -> new ResourceNotFoundException("PasoChecklist", pasoChecklistId));

        Usuario investigador = usuarioRepository.findById(request.investigadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.investigadorId()));

        boolean pinValido = investigador.autenticar(request.pin());

        // Siempre persiste el intento (exitoso o fallido) — inmutabilidad de auditoría
        FirmaChecklist firma = FirmaChecklist.builder()
                .pasoChecklist(paso)
                .investigador(investigador)
                .paso(paso.getPaso())
                .timestampFirma(LocalDateTime.now())
                .exitoso(pinValido)
                .motivoFallo(pinValido ? null : "PIN incorrecto")
                .build();

        FirmaChecklist guardada = firmaRepository.save(firma);

        if (!pinValido) {
            // Registra el intento fallido pero NO cierra el paso
            throw new BusinessException(
                    "PIN incorrecto. El intento ha sido registrado con timestamp " +
                            guardada.getTimestampFirma() + "."
            );
        }

        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FirmaChecklistResponse> obtenerHistorialFirmas(Long escenaId) {
        return firmaRepository
                .findByPasoChecklistEscenaIdOrderByTimestampFirmaAsc(escenaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FirmaChecklistResponse toResponse(FirmaChecklist f) {
        return new FirmaChecklistResponse(
                f.getId(),
                f.getPasoChecklist().getId(),
                f.getPaso(),
                f.getInvestigador().getId(),
                f.getInvestigador().getNombre(),
                f.getTimestampFirma(),
                f.getExitoso(),
                f.getMotivoFallo()
        );
    }
}