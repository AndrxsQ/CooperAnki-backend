package com.coopera2075.modules.projects;

import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ProjectDocumentStorageService {
    private static final Pattern SAFE_EXTENSION = Pattern.compile("\\.[A-Za-z0-9]{1,15}");
    private static final Pattern PROJECT_OBJECT_KEY =
            Pattern.compile("projects/[1-9][0-9]*/[0-9a-f-]{36}(?:\\.[A-Za-z0-9]{1,15})?");

    private final S3Client s3Client;
    private final String bucket;

    public ProjectDocumentStorageService(
            S3Client s3Client,
            @Value("${app.storage.s3.bucket:}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public String upload(
            long projectId,
            String originalFileName,
            String contentType,
            InputStream content,
            long contentLength) {
        if (projectId <= 0) {
            throw new IllegalArgumentException("El identificador del proyecto debe ser positivo");
        }
        if (!StringUtils.hasText(originalFileName)) {
            throw new IllegalArgumentException("El nombre del archivo es obligatorio");
        }
        if (content == null || contentLength < 0) {
            throw new IllegalArgumentException("El contenido del archivo y su tamaño son obligatorios");
        }

        String key = createObjectKey(projectId, originalFileName);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(requireBucket())
                .key(key)
                .contentType(StringUtils.hasText(contentType)
                        ? contentType
                        : "application/octet-stream")
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(content, contentLength));
        return key;
    }

    public ResponseInputStream<GetObjectResponse> download(String objectKey) {
        String validatedKey = validateObjectKey(objectKey);
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(requireBucket())
                .key(validatedKey)
                .build());
    }

    public void delete(String objectKey) {
        String validatedKey = validateObjectKey(objectKey);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(requireBucket())
                .key(validatedKey)
                .build());
    }

    private String createObjectKey(long projectId, String originalFileName) {
        String fileName = StringUtils.getFilename(originalFileName);
        String extension = "";
        if (fileName != null) {
            int extensionStart = fileName.lastIndexOf('.');
            if (extensionStart >= 0) {
                String candidate = fileName.substring(extensionStart);
                if (SAFE_EXTENSION.matcher(candidate).matches()) {
                    extension = candidate.toLowerCase(java.util.Locale.ROOT);
                }
            }
        }
        return "projects/" + projectId + "/" + UUID.randomUUID() + extension;
    }

    private String validateObjectKey(String objectKey) {
        if (!StringUtils.hasText(objectKey) || !PROJECT_OBJECT_KEY.matcher(objectKey).matches()) {
            throw new IllegalArgumentException("La clave del objeto de proyecto no es válida");
        }
        return objectKey;
    }

    private String requireBucket() {
        if (!StringUtils.hasText(bucket)) {
            throw new IllegalStateException("La configuración app.storage.s3.bucket es obligatoria");
        }
        return bucket;
    }
}
