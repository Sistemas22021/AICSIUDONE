package naranja.custodia_360.controllers;


import naranja.custodia_360.dtos.TestimonyContentDTO;
import naranja.custodia_360.models.ResourceType;
import naranja.custodia_360.dtos.TestimonyHistoryDTO;
import naranja.custodia_360.services.TestimonyManagementService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{sessionId}/content")
    public ResponseEntity<Resource> viewResourceContent(
            @PathVariable String sessionId,
            @RequestParam("type") ResourceType resourceType) {

        Resource resource = managementService.loadTestimonyResource(sessionId, resourceType);
        String contentType = getContentTypeOfFile(resource.getFilename(), resourceType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private String getContentTypeOfFile(String filename, ResourceType resourceType) {
        String contentType = null;
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