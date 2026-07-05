package naranja.custodia_360.controllers;

import naranja.custodia_360.exception.type.BadRequestException;
import naranja.custodia_360.models.Testimony;
import naranja.custodia_360.services.AiService;
import naranja.custodia_360.services.TestimonyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/testimonies")
public class TestimonyController {

    private static final Logger log = LoggerFactory.getLogger(TestimonyController.class);
    private final TestimonyService testimonyService;
    private final AiService aiService;

    public TestimonyController(TestimonyService testimonyService, AiService aiService) {

        this.testimonyService = testimonyService;
        this.aiService = aiService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Testimony> registerTestimony(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("transcription") String originalTranscription,
            @RequestParam("cedula") String cedula,
            @RequestParam("caseNumber") String caseNumber
    ) throws IOException {

        // Validaciones de cliente con su excepción semántica correcta (400 Bad Request)
        if (audio == null || audio.isEmpty()) {
            throw new BadRequestException("El archivo de audio no puede estar vacío y es requerido.");
        }

        if (originalTranscription == null || originalTranscription.trim().isEmpty()) {
            throw new BadRequestException("La transcripción original no puede estar vacía y es requerida.");
        }
        if (cedula.trim().isEmpty() || caseNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La cédula y el número de casos no pueden estar vacios");
        }

        log.info(originalTranscription);

        String modifiedTranscription = aiService.generateJudicialReport(originalTranscription);
        Testimony savedTestimony = testimonyService.saveTestimony(audio, originalTranscription, modifiedTranscription, cedula, caseNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", savedTestimony.getSessionId());
        response.put("content", modifiedTranscription);
        response.put("metadata", savedTestimony);

        return ResponseEntity.ok(response);
    }
}