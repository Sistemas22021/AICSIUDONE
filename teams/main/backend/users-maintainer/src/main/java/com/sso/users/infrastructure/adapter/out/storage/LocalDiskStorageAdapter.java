package com.sso.users.infrastructure.adapter.out.storage;

import com.sso.users.domain.port.out.FileStoragePort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalDiskStorageAdapter implements FileStoragePort {

    private final Path rootLocation;

    public LocalDiskStorageAdapter() {
        this.rootLocation = Paths.get("storage/profile-photos");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    @Override
    public String storeFile(String fileName, byte[] content, String contentType) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        try {
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFileName)).normalize().toAbsolutePath();
            Files.write(destinationFile, content);
            return "/api/users/photos/" + uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
