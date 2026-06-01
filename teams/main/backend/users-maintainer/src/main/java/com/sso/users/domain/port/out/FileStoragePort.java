package com.sso.users.domain.port.out;

public interface FileStoragePort {
    /**
     * Stores a file and returns its access URL.
     */
    String storeFile(String fileName, byte[] content, String contentType);
}
