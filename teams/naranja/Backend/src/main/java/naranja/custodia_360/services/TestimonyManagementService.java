package naranja.custodia_360.services;

import naranja.custodia_360.exception.type.StorageException;
import naranja.custodia_360.models.ResourceType;
import naranja.custodia_360.models.Testimony;
import naranja.custodia_360.dtos.TestimonyHistoryDTO;
import naranja.custodia_360.repositories.TestimonyRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class TestimonyManagementService {

    // @Value("${app.storage.base-path}")
    // private String baseStoragePath;

    private final TestimonyRepository testimonyRepository;

    public TestimonyManagementService(TestimonyRepository testimonyRepository) {
        this.testimonyRepository = testimonyRepository;
    }

    // [AIC-178] Historial temporal: Escanea las carpetas reales en el disco
    public List<TestimonyHistoryDTO> getHistory() {
        return testimonyRepository.findAll().stream()
                .map(t -> new TestimonyHistoryDTO(
                        t.getSessionId(),
                        t.getCedula(),
                        t.getCaseNumber(),
                        t.getAudioPath() != null && !t.getAudioPath().isBlank(),
                        t.getOriginalTextPath() != null && !t.getOriginalTextPath().isBlank(),
                        t.getModifiedTextPath() != null && !t.getModifiedTextPath().isBlank()
                ))
                .toList();
    }

    // [AIC-179] Carga un archivo específico o genera un ZIP
    public Resource loadTestimonyResource(String testimonyId, ResourceType fileType) {
        Testimony testimony = testimonyRepository.findById(testimonyId)
                .orElseThrow(() -> new StorageException("El testimonio con ID " + testimonyId + " no existe.", HttpStatus.NOT_FOUND));

        if(fileType == null) {
            return createZipFromTestimony(testimony);
        }

        String targetPath = switch (fileType) {
            case AUDIO -> testimony.getAudioPath();
            case ORIGINAL -> testimony.getOriginalTextPath();
            case MODIFIED -> testimony.getModifiedTextPath();
        };

        if (targetPath == null || targetPath.isBlank()) {
            throw new StorageException("El recurso de tipo '" + fileType + "' no está disponible para este registro.", HttpStatus.NOT_FOUND);
        }

        try {
            Path filePath = Paths.get(targetPath).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("El archivo físico no se encuentra o no es legible en el servidor.", HttpStatus.NOT_FOUND);
            }

        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Error inesperado al procesar el recurso del testimonio: " + testimonyId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Resource createZipFromTestimony(Testimony testimony) {
        List<String> pathsToZip = getStrings(testimony);

        try {
            // 1. Creamos el archivo temporal primero
            Path zipPath = Files.createTempFile("testimony-" + testimony.getSessionId() + "-", ".zip");

            // 2. Abrimos el ZipOutputStream para empaquetar la lista de archivos obtenida
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
                for (String stringPath : pathsToZip) {
                    Path file = Paths.get(stringPath).normalize();
                    if(Files.exists(file)) {
                        zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
                        Files.copy(file, zos);
                        zos.closeEntry();
                    }
                }
            }

            // 3. Devolvemos el recurso listo
            return new FileSystemResource(zipPath.toFile());

        } catch (IOException e) {
            throw new StorageException("No se pudo procesar el empaquetado del recurso en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @NotNull
    private static List<String> getStrings(Testimony testimony) {
        List<String> pathsToZip = new ArrayList<>();
        if (testimony.getAudioPath() != null && !testimony.getAudioPath().isBlank()) {
            pathsToZip.add(testimony.getAudioPath());
        }
        if (testimony.getOriginalTextPath() != null && !testimony.getOriginalTextPath().isBlank()) {
            pathsToZip.add(testimony.getOriginalTextPath());
        }
        if (testimony.getModifiedTextPath() != null && !testimony.getModifiedTextPath().isBlank()) {
            pathsToZip.add(testimony.getModifiedTextPath());
        }

        if (pathsToZip.isEmpty()) {
            throw new StorageException("Este testimonio no contiene ningún archivo para descargar.", HttpStatus.NOT_FOUND);
        }
        return pathsToZip;
    }
}