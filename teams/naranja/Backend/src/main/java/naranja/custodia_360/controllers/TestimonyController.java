package naranja.custodia_360.controllers;

import naranja.custodia_360.models.Testimony;
import naranja.custodia_360.services.AiService;
import naranja.custodia_360.services.TestimonyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<?> registerTestimony(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("transcription") String originalTranscription
    ) throws IOException {

        if (audio.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo de audio no puede estar vacío.");
        }
        if (originalTranscription.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La transcripción original no puede estar vacía.");
        }

        log.info(originalTranscription);

        String modifiedTranscription = aiService.generateJudicialReport(originalTranscription);
        String sessionId = testimonyService.saveTestimony(audio, originalTranscription, modifiedTranscription);

        return ResponseEntity.ok(new Testimony(modifiedTranscription, sessionId));
    }
}