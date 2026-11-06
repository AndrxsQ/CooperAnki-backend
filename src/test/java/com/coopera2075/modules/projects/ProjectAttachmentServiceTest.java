package com.coopera2075.modules.projects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProjectAttachmentServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectAttachmentRepository attachmentRepository;

    @Mock
    private ProjectDocumentStorageService storageService;

    @InjectMocks
    private ProjectAttachmentService attachmentService;

    @Test
    void listsProjectAttachmentsWithLazyDownloadLinks() {
        givenProjectExists();
        ProjectAttachment attachment = attachment(21L, "proposal.pdf", "projects/15/object.pdf");
        when(attachmentRepository.findAllByProjectIdOrderByIdDesc(15L)).thenReturn(List.of(attachment));

        var result = attachmentService.findAll(15L);

        assertEquals(1, result.size());
        assertEquals("/api/v1/projects/15/attachments/21/content", result.get(0).downloadUrl());
        assertEquals("proposal.pdf", result.get(0).fileName());
        verify(storageService, never()).download(any());
    }

    @Test
    void uploadsFileAndPersistsOnlyItsStorageKey() throws IOException {
        givenProjectExists();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "C:\\client\\proposal.pdf",
                "application/pdf",
                new byte[] {1, 2, 3});
        when(storageService.upload(
                any(Long.class),
                any(String.class),
                any(String.class),
                any(),
                any(Long.class))).thenReturn("projects/15/generated-key.pdf");
        when(attachmentRepository.saveAndFlush(any(ProjectAttachment.class)))
                .thenAnswer(invocation -> {
                    ProjectAttachment saved = invocation.getArgument(0);
                    saved.setId(21L);
                    return saved;
                });

        var result = attachmentService.upload(15L, file);

        assertEquals("proposal.pdf", result.fileName());
        assertEquals("/api/v1/projects/15/attachments/21/content", result.downloadUrl());
        verify(storageService).upload(
                any(Long.class),
                org.mockito.ArgumentMatchers.eq("proposal.pdf"),
                org.mockito.ArgumentMatchers.eq("application/pdf"),
                any(),
                org.mockito.ArgumentMatchers.eq(3L));
        verify(attachmentRepository).saveAndFlush(any(ProjectAttachment.class));
    }

    @Test
    void rejectsFilesLargerThanTwentyMegabytes() {
        givenProjectExists();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                new byte[20 * 1024 * 1024 + 1]);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.upload(15L, file));

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, exception.getStatusCode());
        verify(storageService, never()).upload(any(Long.class), any(), any(), any(), any(Long.class));
    }

    @Test
    void rejectsMissingProjectsBeforeListingAttachments() {
        when(projectRepository.findById(404L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> attachmentService.findAll(404L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(attachmentRepository, never()).findAllByProjectIdOrderByIdDesc(404L);
    }

    @Test
    void deletesObjectAndMetadataOnlyWhenAttachmentBelongsToProject() {
        givenProjectExists();
        ProjectAttachment attachment = attachment(21L, "proposal.pdf", "projects/15/object.pdf");
        when(attachmentRepository.findByIdAndProjectId(21L, 15L)).thenReturn(Optional.of(attachment));

        attachmentService.delete(15L, 21L);

        verify(storageService).delete("projects/15/object.pdf");
        verify(attachmentRepository).delete(attachment);
    }

    private ProjectAttachment attachment(long id, String fileName, String objectKey) {
        ProjectAttachment attachment = new ProjectAttachment();
        attachment.setId(id);
        attachment.setFileName(fileName);
        attachment.setS3Url(objectKey);
        attachment.setMimeType("application/pdf");
        attachment.setSizeBytes(3L);
        return attachment;
    }

    private void givenProjectExists() {
        Project project = new Project();
        project.setId(15L);
        when(projectRepository.findById(15L)).thenReturn(Optional.of(project));
    }
}
