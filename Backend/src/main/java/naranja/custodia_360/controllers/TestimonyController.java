package naranja.custodia_360.controllers;

import naranja.custodia_360.models.Testimony;
import naranja.custodia_360.services.TestimonyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/testimonies")
public class TestimonyController {

    @Autowired
    private final TestimonyService testimonyService;

    public TestimonyController(TestimonyService testimonyService) {

        this.testimonyService = testimonyService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> registerTestimony(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("transcripcionOriginal") String originalTranscription
    ) throws IOException {

        if (audio.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo de audio no puede estar vacío.");
        }
        if (originalTranscription.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La transcripción original no puede estar vacía.");
        }

        String sessionId = testimonyService.saveTestimony(audio, originalTranscription);

        return ResponseEntity.ok(new Testimony(originalTranscription, sessionId));
    }
}