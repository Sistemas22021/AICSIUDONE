package naranja.custodia_360.services;

import naranja.custodia_360.models.Testimony;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class TestimonyService {

    @Value("${app.storage.base-path}")
    private String baseStoragePath;

    public String saveTestimony(MultipartFile audio, String originalTranscription, String modifiedTranscription) throws IOException {
        String sessionId = UUID.randomUUID().toString();
        // Definir la ruta de la carpeta: /storage/base/testimonies/{UUID}/
        Path testimonyDir = Paths.get(baseStoragePath, "testimonies", sessionId);

        Files.createDirectories(testimonyDir);

        Path audioPath = testimonyDir.resolve("audio_original.webm");
        Path originalTextPath = testimonyDir.resolve("texto_original.md");
        Path modifiedTextPath = testimonyDir.resolve("texto_modificado.md"); // También en Markdown para consistencia

        Files.copy(audio.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(originalTextPath, originalTranscription, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.writeString(modifiedTextPath, modifiedTranscription, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return sessionId;
    }
}