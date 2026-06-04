package naranja.custodia_360.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class TestimonyServiceImpl implements TestimonyService {

    @Value("${app.storage.base-path}")
    private String baseStoragePath;

    @Override
    public String saveTestimony(MultipartFile audio, String originalTranscription) throws IOException {
        String testimonyId = UUID.randomUUID().toString();

        // Definir la ruta de la carpeta: /storage/base/testimonies/{UUID}/
        Path testimonyDir = Paths.get(baseStoragePath, "testimonies", testimonyId);
        Files.createDirectories(testimonyDir);

        Path audioPath = testimonyDir.resolve("audio_original.webm");
        Files.copy(audio.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);

        Path textPath = testimonyDir.resolve("texto_original.txt");
        Files.writeString(textPath, originalTranscription, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return testimonyId;
    }
}
