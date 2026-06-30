package naranja.custodia_360.services;

import naranja.custodia_360.exception.type.StorageException;
import naranja.custodia_360.models.TestimonyHistoryDTO;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class TestimonyManagementService {

    @Value("${app.storage.base-path}")
    private String baseStoragePath;

    // [AIC-178] Historial temporal: Escanea las carpetas reales en el disco
    public List<TestimonyHistoryDTO> getHistory() {
        Path testimoniesRoot = Paths.get(baseStoragePath).resolve("testimonies").normalize();

        if (!Files.exists(testimoniesRoot)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(testimoniesRoot)) {
            return stream
                    .filter(Files::isDirectory) // Filtra solo las carpetas (los UUIDs)
                    .map(folder -> {
                        String testimonyId = folder.getFileName().toString();
                        List<String> files = getFilesForTestimony(testimonyId);

                        // Retorna el DTO sencillito basado solo en carpetas y archivos
                        return new TestimonyHistoryDTO(testimonyId, files);

                        /* // TODO: Reemplazar por el mapeo de BD cuando esté listo:
                        // Testimony testimony = testimonyRepository.findByUuid(testimonyId);
                        // return new TestimonyHistoryDTO(
                        //     testimony.getId(), testimony.getCaseCode(), testimony.getCreatedAt(),
                        //     testimony.getStatus(), testimony.getInvolvedPartyName(),
                        //     testimony.getAudioDuration(), files
                        // );
                        */
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    // [AIC-179] Carga un archivo específico o genera un ZIP si fileName es null
    public Resource loadTestimonyResource(String testimonyId, String fileName) {
        Path testimonyFolder = Paths.get(baseStoragePath).resolve("testimonies").resolve(testimonyId).normalize();

        // 1. Verificación previa: Si la carpeta del testimonio no existe, es un 404 directo
        if (!Files.exists(testimonyFolder)) {
            throw new StorageException("La carpeta del testimonio solicitado no existe en el servidor.", HttpStatus.NOT_FOUND);
        }

        try {
            // COMPORTAMIENTO: Si no se especifica archivo, se delega al empaquetador ZIP
            if (fileName == null || fileName.isBlank()) {
                return createZipFromFolder(testimonyFolder, testimonyId);
            }

            // Si se especifica un archivo, se busca de forma individual
            Path filePath = testimonyFolder.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Si el testimonio existe pero ese archivo en específico no (ej. un audio que no se grabó)
                throw new StorageException("El archivo '" + fileName + "' no existe para este testimonio.", HttpStatus.NOT_FOUND);
            }

        } catch (StorageException e) {
            // IMPORTANTE: Dejamos pasar tu excepción personalizada intacta para que el GlobalExceptionHandler la lea fina
            throw e;
        } catch (Exception e) {
            // Cualquier otro desastre de bajo nivel (ej. problemas de red extraños al instanciar UrlResource)
            throw new StorageException("Error inesperado al procesar el recurso del testimonio: " + testimonyId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper para escanear archivos dentro de la carpeta del testimonio
    private List<String> getFilesForTestimony(String testimonyId) {
        Path folderPath = Paths.get(baseStoragePath).resolve("testimonies").resolve(testimonyId).normalize();
        if (!Files.exists(folderPath)) return List.of();

        try (Stream<Path> stream = Files.list(folderPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }

    // Helper para crear el ZIP al vuelo
    private Resource createZipFromFolder(Path folderPath, String testimonyId) {
        if (!Files.exists(folderPath)) {
            throw new StorageException("El recurso o carpeta de testimonio solicitado no existe en el servidor.", HttpStatus.NOT_FOUND);
        }

        try {
            // 1. Creamos el archivo temporal primero
            Path zipPath = Files.createTempFile("testimony-" + testimonyId + "-", ".zip");

            // 2. Abrimos el Stream en su propio try-with-resources para asegurar su cierre inmediato
            List<Path> filesToZip;
            try (Stream<Path> stream = Files.list(folderPath)) {
                filesToZip = stream.filter(Files::isRegularFile).collect(Collectors.toList());
            }

            // 3. Abrimos el ZipOutputStream para empaquetar la lista de archivos obtenida
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
                for (Path file : filesToZip) {
                    zos.putNextEntry(new ZipEntry(file.getFileName().toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                }
            }

            // 4. Devolvemos el recurso listo
            return new FileSystemResource(zipPath.toFile());

        } catch (IOException e) {
            // Tu manejador de excepciones se encargará de este 500 limpiamente
            throw new StorageException("No se pudo procesar el empaquetado del recurso en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}