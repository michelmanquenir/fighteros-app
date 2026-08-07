package com.killerdev.fighteros_app.storage;

import com.killerdev.fighteros_app.exception.ArchivoInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class StorageService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp", "video/mp4", "video/quicktime"
    );

    private final RestClient restClient = RestClient.create();
    private final String storageUrl;
    private final String serviceKey;
    private final String bucket;

    public StorageService(@Value("${supabase.storage.url}") String storageUrl,
                           @Value("${supabase.storage.service-key}") String serviceKey,
                           @Value("${supabase.storage.bucket}") String bucket) {
        this.storageUrl = storageUrl;
        this.serviceKey = serviceKey;
        this.bucket = bucket;
    }

    public String subirArchivo(MultipartFile archivo, String carpeta) {
        validar(archivo);
        String contentType = archivo.getContentType();
        String path = carpeta + "/" + UUID.randomUUID() + extraerExtension(archivo.getOriginalFilename());

        byte[] contenido;
        try {
            contenido = archivo.getBytes();
        } catch (IOException e) {
            throw new ArchivoInvalidoException("No se pudo leer el archivo");
        }

        restClient.post()
                .uri(storageUrl + "/storage/v1/object/{bucket}/{path}", bucket, path)
                .header("Authorization", "Bearer " + serviceKey)
                .contentType(MediaType.parseMediaType(contentType))
                .body(contenido)
                .retrieve()
                .toBodilessEntity();

        return storageUrl + "/storage/v1/object/public/" + bucket + "/" + path;
    }

    private void validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoInvalidoException("El archivo está vacío");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            throw new ArchivoInvalidoException("Tipo de archivo no permitido: " + contentType);
        }
    }

    private String extraerExtension(String nombreOriginal) {
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            return "";
        }
        return nombreOriginal.substring(nombreOriginal.lastIndexOf('.'));
    }
}
