package naranja.custodia_360.services;

import naranja.custodia_360.dtos.TestimonyContentDTO;
import naranja.custodia_360.dtos.TestimonyHistoryDTO;
import naranja.custodia_360.exception.type.StorageException;
import naranja.custodia_360.models.ResourceType;
import naranja.custodia_360.models.Testimony;
import naranja.custodia_360.repositories.TestimonyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para TestimonyManagementService")
class TestimonyManagementServiceTest {

    @Mock
    private TestimonyRepository testimonyRepository;

    @InjectMocks
    private TestimonyManagementService managementService;

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("getHistory - Debería mapear correctamente los campos de la entidad al DTO")
    void getHistoryMapsFields() {
        // GIVEN
        Testimony t1 = new Testimony("id-1", "123", "CASE-1", "path/audio.webm", "path/orig.md", "");
        when(testimonyRepository.findAll()).thenReturn(List.of(t1));

        // WHEN
        List<TestimonyHistoryDTO> result = managementService.getHistory();

        // THEN
        assertEquals(1, result.size());
        assertTrue(result.getFirst().hasAudio());
        assertTrue(result.getFirst().hasOriginalText());
        assertFalse(result.getFirst().hasModifiedText());
    }

    @Test
    @DisplayName("loadTestimonyResource - Debería generar un ZIP cuando el tipo de recurso es nulo")
    void loadResourceGeneratesZip() throws IOException {
        Path fakeOriginalFile = Files.writeString(tempDir.resolve("original.md"), "Contenido original");
        Testimony testimony = new Testimony("id-123", "123", "CASE-2", "", fakeOriginalFile.toString(), "");
        when(testimonyRepository.findById("id-123")).thenReturn(Optional.of(testimony));

        Resource resource = managementService.loadTestimonyResource("id-123", null);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.getFilename().endsWith(".zip"));
    }

    @Test
    @DisplayName("loadTestimonyResource - Debería lanzar excepción si el testimonio no existe en BD")
    void loadResourceThrowsOnNotFound() {
        when(testimonyRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(StorageException.class, () -> {
            managementService.loadTestimonyResource("invalid-id", ResourceType.AUDIO);
        });
    }

    @Test
    @DisplayName("getFullTestimonyContent - Debería leer y retornar el contenido de los archivos de texto")
    void getFullContentReadsFiles() throws IOException {
        Path fakeOriginalFile = Files.writeString(tempDir.resolve("texto_original.md"), "Hola mundo original");
        Path fakeModifiedFile = Files.writeString(tempDir.resolve("texto_modificado.md"), "Hola mundo modificado");

        Testimony testimony = new Testimony("session-abc", "111", "CASE-3",
                "path/audio.webm", fakeOriginalFile.toString(), fakeModifiedFile.toString());
        when(testimonyRepository.findById("session-abc")).thenReturn(Optional.of(testimony));

        TestimonyContentDTO dto = managementService.getFullTestimonyContent("session-abc");

        assertNotNull(dto);
        assertEquals("Hola mundo original", dto.originalText());
        assertEquals("Hola mundo modificado", dto.modifiedText());
    }

    @Test
    @DisplayName("getFullTestimonyContent - Debería manejar errores de lectura cuando el archivo no existe en el disco")
    void getFullContentHandlesIoException() {
        Testimony testimony = new Testimony("session-abc", "111", "CASE-3",
                "path/audio.webm", "ruta/invalida/original.md", "ruta/invalida/modificado.md");
        when(testimonyRepository.findById("session-abc")).thenReturn(Optional.of(testimony));

        TestimonyContentDTO dto = managementService.getFullTestimonyContent("session-abc");

        assertTrue(dto.originalText().contains("Error: No se pudo leer"));
        assertTrue(dto.modifiedText().contains("Error: No se pudo leer"));
    }
}