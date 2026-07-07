package naranja.custodia_360.controllers;


import naranja.custodia_360.dtos.TestimonyContentDTO;
import naranja.custodia_360.exception.type.StorageException;
import naranja.custodia_360.models.ResourceType;
import naranja.custodia_360.dtos.TestimonyHistoryDTO;
import naranja.custodia_360.services.TestimonyManagementService;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/testimonies")
public class TestimonyManagementController {

    private final TestimonyManagementService managementService;

    public TestimonyManagementController(TestimonyManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<TestimonyHistoryDTO>> getTestimonyHistory() {
        return ResponseEntity.ok(managementService.getHistory());
    }

    @GetMapping("/{sessionId}/download")
    public ResponseEntity<Resource> downloadResource(
            @PathVariable String sessionId,
            @RequestParam(value = "type", required = false) ResourceType resourceType) {

        Resource resource = managementService.loadTestimonyResource(sessionId, resourceType);

        String downloadName = (resourceType == null)
                ? "testimony-" + sessionId + ".zip"
                : resource.getFilename();

        String contentType = getContentTypeOfFile(downloadName, resourceType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadName + "\"")
                .body(resource);
    }

    @GetMapping("/{sessionId}/details")
    public ResponseEntity<TestimonyContentDTO> getTestimonyDetails(@PathVariable String sessionId) {
        return ResponseEntity.ok(managementService.getFullTestimonyContent(sessionId));
    }

    @GetMapping("/{sessionId}/audio")
    public ResponseEntity<ResourceRegion> streamTestimonyAudio(
            @PathVariable String sessionId,
            @RequestHeader HttpHeaders headers) {

        // Cargamos el recurso usando tu servicio actual asegurando que sea el AUDIO
        Resource resource = managementService.loadTestimonyResource(sessionId, ResourceType.AUDIO);

        // Forzamos el tipo de medio correcto para el reproductor de audio webm
        MediaType mediaType = MediaType.parseMediaType("audio/webm");

        try {
            long contentLength = resource.contentLength();
            List<HttpRange> ranges = headers.getRange();

            // Si el navegador pide un rango (comportamiento por defecto de la etiqueta <audio>)
            if (!ranges.isEmpty()) {
                ResourceRegion region = getResourceRegion(ranges, contentLength, resource);

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT) // 206 Partial Content
                        .contentType(mediaType)
                        .body(region);
            } else {
                // Si no hay rangos (petición inicial rara), enviamos el primer chunk completo por defecto
                long rangeLength = Math.min(1024 * 1024L, contentLength);
                ResourceRegion region = new ResourceRegion(resource, 0, rangeLength);
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .contentType(mediaType)
                        .body(region);
            }
        } catch (IOException e) {
            throw new StorageException("Error al procesar el tamaño del archivo de audio.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @NotNull
    private static ResourceRegion getResourceRegion(List<HttpRange> ranges, long contentLength, Resource resource) {
        HttpRange range = ranges.getFirst();
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);

        // Definimos un tamaño de chunk prudente (ej. 1 MB) para no saturar la red
        long chunkSize = Math.min(1024 * 1024L, contentLength);
        if (end == -1 || (end - start + 1) > chunkSize) {
            end = Math.min(start + chunkSize - 1, contentLength - 1);
        }

        return new ResourceRegion(resource, start, end - start + 1);
    }

    private String getContentTypeOfFile(String filename, ResourceType resourceType) {
        String contentType;
        try {
            contentType = URLConnection.guessContentTypeFromName(filename);
        } catch (Exception e) {
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            if (resourceType == ResourceType.AUDIO) return "audio/webm";
            if (resourceType == ResourceType.ORIGINAL || resourceType == ResourceType.MODIFIED) return "text/plain";
            contentType = "application/octet-stream";
        }
        return contentType;
    }

}