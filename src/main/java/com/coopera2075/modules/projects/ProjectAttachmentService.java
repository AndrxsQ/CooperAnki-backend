package com.coopera2075.modules.projects;

import java.io.InputStream;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.projects.dto.ProjectAttachmentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectAttachmentService {
    private static final long MAX_FILE_SIZE_BYTES = 20 * 1024 * 1024;

    private final ProjectRepository projectRepository;
    private final ProjectAttachmentRepository attachmentRepository;
    private final ProjectDocumentStorageService storageService;

    @Transactional(readOnly = true)
    public List<ProjectAttachmentResponse> findAll(long projectId) {
        requireProject(projectId);
        return attachmentRepository.findAllByProjectIdOrderByIdDesc(projectId).stream()
                .map(attachment -> toResponse(projectId, attachment))
                .toList();
    }

    @Transactional
    public ProjectAttachmentResponse upload(long projectId, MultipartFile file) {
        requireProject(projectId);
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no puede estar vacío");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "El archivo supera el límite de 20 MB");
        }

        String fileName = normalizeFileName(file.getOriginalFilename());
        if (!StringUtils.hasText(fileName) || fileName.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del archivo no es válido");
        }

        String objectKey;
        try (InputStream input = file.getInputStream()) {
            objectKey = storageService.upload(
                    projectId,
                    fileName,
                    file.getContentType(),
                    input,
                    file.getSize());
        } catch (java.io.IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo recibido", exception);
        }

        ProjectAttachment attachment = new ProjectAttachment();
        attachment.setProject(projectRepository.getReferenceById(projectId));
        attachment.setFileName(fileName);
        attachment.setS3Url(objectKey);
        attachment.setMimeType(file.getContentType());
        attachment.setSizeBytes(file.getSize());

        try {
            return toResponse(projectId, attachmentRepository.saveAndFlush(attachment));
        } catch (DataAccessException exception) {
            try {
                storageService.delete(objectKey);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public DownloadedProjectAttachment download(long projectId, long attachmentId) {
        requireProject(projectId);
        ProjectAttachment attachment = findAttachment(projectId, attachmentId);
        return new DownloadedProjectAttachment(
                attachment.getFileName(),
                attachment.getMimeType(),
                attachment.getSizeBytes(),
                storageService.download(attachment.getS3Url()));
    }

    @Transactional
    public void delete(long projectId, long attachmentId) {
        requireProject(projectId);
        ProjectAttachment attachment = findAttachment(projectId, attachmentId);
        storageService.delete(attachment.getS3Url());
        attachmentRepository.delete(attachment);
    }

    private Project requireProject(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Proyecto no encontrado: " + projectId));
    }

    private ProjectAttachment findAttachment(long projectId, long attachmentId) {
        return attachmentRepository.findByIdAndProjectId(attachmentId, projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Adjunto no encontrado: " + attachmentId));
    }

    private ProjectAttachmentResponse toResponse(long projectId, ProjectAttachment attachment) {
        return new ProjectAttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getMimeType(),
                attachment.getSizeBytes(),
                "/api/v1/projects/" + projectId + "/attachments/" + attachment.getId() + "/content");
    }

    private String normalizeFileName(String originalFileName) {
        if (!StringUtils.hasText(originalFileName)) {
            return null;
        }
        String fileName = originalFileName.replace('\\', '/');
        return StringUtils.getFilename(fileName);
    }

    public record DownloadedProjectAttachment(
            String fileName,
            String mimeType,
            Long sizeBytes,
            software.amazon.awssdk.core.ResponseInputStream<
                    software.amazon.awssdk.services.s3.model.GetObjectResponse> content) {
    }
}
