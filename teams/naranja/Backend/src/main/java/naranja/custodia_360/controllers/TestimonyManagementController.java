package naranja.custodia_360.controllers;


import naranja.custodia_360.models.ResourceType;
import naranja.custodia_360.models.TestimonyHistoryDTO;
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

    @GetMapping("/{testimonyId}/download")
    public ResponseEntity<Resource> downloadResource(
            @PathVariable String testimonyId,
            @RequestParam(value = "type", required = false) ResourceType resourceType) {

        Resource resource = managementService.loadTestimonyResource(testimonyId, resourceType);

        // Determinar el nombre final del archivo de descarga
        String downloadName = (resourceType == null)
                ? "testimony-" + testimonyId + ".zip"
                : resource.getFilename();

        String contentType = null;
        try {
            contentType = URLConnection.guessContentTypeFromName(downloadName);
        } catch (Exception e) {
            contentType = "application/octet-stream";
        }

        if (contentType == null) {
            contentType = (resourceType == null) ? "application/zip" : "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadName + "\"")
                .body(resource);
    }
}