package naranja.custodia_360.services;

import naranja.custodia_360.models.Testimony;
import naranja.custodia_360.repositories.TestimonyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para TestimonyService")
public class TestimonyServiceTest {

    @Mock
    private TestimonyRepository testimonyRepository;

    @InjectMocks
    private TestimonyService testimonyService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(testimonyService, "baseStoragePath", tempDir.toString());
    }

    @Test
    @DisplayName("saveTestimony - Debería crear los archivos físicos en disco y registrar el testimonio en la base de datos")
    void saveTestimonySuccessfully() throws IOException {
        String originalText = "Transcripción original de prueba";
        String modifiedText = "Transcripción modificada de prueba";
        String cedula = "12345678";
        String caseNumber = "CASE-2026";

        MockMultipartFile mockAudio = new MockMultipartFile(
                "audio",
                "test_audio.webm",
                "audio/webm",
                "datos-de-audio-falsos".getBytes()
        );

        when(testimonyRepository.save(any(Testimony.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        Testimony resultado = testimonyService.saveTestimony(mockAudio, originalText, modifiedText, cedula, caseNumber);

        assertNotNull(resultado);
        assertNotNull(resultado.getSessionId());
        assertEquals(cedula, resultado.getCedula());
        assertEquals(caseNumber, resultado.getCaseNumber());

        Path audioCreado = Path.of(resultado.getAudioPath());
        Path originalTextoCreado = Path.of(resultado.getOriginalTextPath());
        Path modificadoTextoCreado = Path.of(resultado.getModifiedTextPath());

        assertTrue(Files.exists(audioCreado), "El archivo de audio debería haberse creado");
        assertTrue(Files.exists(originalTextoCreado), "El archivo de texto original debería haberse creado");
        assertTrue(Files.exists(modificadoTextoCreado), "El archivo de texto modificado debería haberse creado");

        assertEquals("datos-de-audio-falsos", Files.readString(audioCreado));
        assertEquals(originalText, Files.readString(originalTextoCreado));
        assertEquals(modifiedText, Files.readString(modificadoTextoCreado));

        verify(testimonyRepository, times(1)).save(any(Testimony.class));
    }
}