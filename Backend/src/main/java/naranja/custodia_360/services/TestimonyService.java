package naranja.custodia_360.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TestimonyService {
    String saveTestimony(MultipartFile audio, String originalTranscription) throws IOException;
}
