package com.coopera2075.modules.projects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

class ProjectDocumentStorageServiceTest {
    private static final String BUCKET = "project-documents";

    private S3Client s3Client;
    private ProjectDocumentStorageService storageService;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        storageService = new ProjectDocumentStorageService(s3Client, BUCKET);
    }

    @Test
    void uploadsUsingPrivateProjectScopedObjectKey() {
        String key = storageService.upload(
                27,
                "propuesta.pdf",
                "application/pdf",
                new ByteArrayInputStream(new byte[] {1, 2, 3}),
                3);

        assertTrue(Pattern.matches("projects/27/[0-9a-f-]{36}\\.pdf", key));
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        assertEquals(BUCKET, requestCaptor.getValue().bucket());
        assertEquals(key, requestCaptor.getValue().key());
        assertEquals("application/pdf", requestCaptor.getValue().contentType());
    }

    @Test
    void downloadsOnlyProjectScopedObjectKeys() {
        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> response = mock(ResponseInputStream.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(response);

        ResponseInputStream<GetObjectResponse> downloaded = storageService.download(
                "projects/27/123e4567-e89b-12d3-a456-426614174000.pdf");

        assertEquals(response, downloaded);
        ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(requestCaptor.capture());
        assertEquals(BUCKET, requestCaptor.getValue().bucket());
        assertEquals("projects/27/123e4567-e89b-12d3-a456-426614174000.pdf",
                requestCaptor.getValue().key());
    }

    @Test
    void deletesOnlyProjectScopedObjectKeys() {
        String key = "projects/27/123e4567-e89b-12d3-a456-426614174000.pdf";

        storageService.delete(key);

        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(requestCaptor.capture());
        assertEquals(BUCKET, requestCaptor.getValue().bucket());
        assertEquals(key, requestCaptor.getValue().key());
    }

    @Test
    void rejectsObjectKeysOutsideProjectNamespace() {
        assertThrows(IllegalArgumentException.class, () -> storageService.delete("../secrets"));
        verifyNoInteractions(s3Client);
    }

    @Test
    void reportsMissingBucketConfigurationWhenUploading() {
        ProjectDocumentStorageService unconfiguredService =
                new ProjectDocumentStorageService(s3Client, "");

        assertThrows(
                IllegalStateException.class,
                () -> unconfiguredService.upload(
                        27,
                        "propuesta.pdf",
                        "application/pdf",
                        new ByteArrayInputStream(new byte[] {1}),
                        1));
        verifyNoInteractions(s3Client);
    }
}
