package com.nexocriminal.files;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio de almacenamiento de imagenes. Sube las fotos a Cloudinary, que
 * las sirve desde una URL publica permanente (sobrevive a reinicios del backend,
 * a diferencia del disco local que es efimero en Render).
 */
@Service
public class FileStorageService {

    @Value("${nexo.cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${nexo.cloudinary.api-key:}")
    private String apiKey;

    @Value("${nexo.cloudinary.api-secret:}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        if (cloudName == null || cloudName.isBlank()) {
            System.err.println("[Cloudinary] ADVERTENCIA: credenciales no configuradas. La subida de fotos fallara.");
            return;
        }
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    /**
     * Sube una imagen a Cloudinary y retorna la URL publica permanente.
     * Mantiene la misma firma que la version anterior para no romper llamadas.
     */
    public String guardarFotoDesaparecida(MultipartFile archivo) {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se aceptan archivos de imagen");
        }

        if (archivo.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen no puede superar los 5MB");
        }

        if (cloudinary == null) {
            throw new RuntimeException("Cloudinary no está configurado (faltan credenciales)");
        }

        try {
            // public_id unico dentro de una carpeta 'desaparecidas'
            String publicId = "desaparecidas/" + UUID.randomUUID();
            @SuppressWarnings("unchecked")
            Map<String, Object> resultado = cloudinary.uploader().upload(
                    archivo.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image"
                    )
            );
            // La URL segura y permanente de la imagen
            Object secureUrl = resultado.get("secure_url");
            if (secureUrl == null) {
                throw new RuntimeException("Cloudinary no devolvió la URL de la imagen");
            }
            return secureUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen a Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una imagen de Cloudinary a partir de su URL.
     * Extrae el public_id de la URL y llama a destroy.
     */
    public void eliminarArchivo(String url) {
        if (url == null || cloudinary == null || !url.contains("cloudinary.com")) return;
        try {
            // Extraer el public_id de la URL de Cloudinary.
            // Formato: https://res.cloudinary.com/<cloud>/image/upload/v123/desaparecidas/uuid.jpg
            String sinExtension = url.substring(0, url.lastIndexOf('.'));
            int idx = sinExtension.indexOf("/upload/");
            if (idx == -1) return;
            String despuesUpload = sinExtension.substring(idx + "/upload/".length());
            // Quitar el prefijo de version (v123456789/)
            String publicId = despuesUpload.replaceFirst("^v\\d+/", "");
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("No se pudo eliminar la imagen de Cloudinary: " + e.getMessage());
        }
    }
}