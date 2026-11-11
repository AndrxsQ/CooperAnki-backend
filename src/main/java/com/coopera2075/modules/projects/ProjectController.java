package com.coopera2075.modules.projects;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.modules.projects.dto.ProjectPageResponse;
import com.coopera2075.modules.projects.dto.ProjectRequest;
import com.coopera2075.modules.projects.dto.ProjectResponse;
import com.coopera2075.modules.projects.dto.ProjectStatusRequest;
import com.coopera2075.modules.projects.dto.ProjectAttachmentResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PERM_SYSTEM_COOPERATION_ACCESS')")
public class ProjectController {
    private static final int MAX_PAGE_SIZE = 100;
    private static final java.util.Set<String> ALLOWED_SORT_PROPERTIES = java.util.Set.of(
            "id", "createdAt", "updatedAt", "title", "identifierCode", "requestedBudget", "status",
            "startsAt", "closesAt");

    private final ProjectService projectService;
    private final ProjectAttachmentService attachmentService;

    @GetMapping
    public ResponseEntity<ProjectPageResponse> findAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Long creatorId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El tamaño máximo de página es " + MAX_PAGE_SIZE);
        }
        boolean invalidSort = pageable.getSort().stream()
                .anyMatch(order -> !ALLOWED_SORT_PROPERTIES.contains(order.getProperty()));
        if (invalidSort) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El criterio de ordenamiento no es válido");
        }
        return ResponseEntity.ok(projectService.findAll(q, status, organizationId, creatorId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable long id,
            @Valid @RequestBody ProjectStatusRequest request) {
        return ResponseEntity.ok(projectService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}/attachments")
    public ResponseEntity<java.util.List<ProjectAttachmentResponse>> findAttachments(
            @PathVariable long projectId) {
        return ResponseEntity.ok(attachmentService.findAll(projectId));
    }

    @PostMapping(path = "/{projectId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProjectAttachmentResponse> uploadAttachment(
            @PathVariable long projectId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attachmentService.upload(projectId, file));
    }

    @GetMapping("/{projectId}/attachments/{attachmentId}/content")
    public ResponseEntity<StreamingResponseBody> downloadAttachment(
            @PathVariable long projectId,
            @PathVariable long attachmentId) {
        ProjectAttachmentService.DownloadedProjectAttachment attachment =
                attachmentService.download(projectId, attachmentId);
        StreamingResponseBody body = outputStream -> {
            try (var input = attachment.content()) {
                input.transferTo(outputStream);
            } catch (IOException exception) {
                throw new java.io.UncheckedIOException("No se pudo transmitir el adjunto", exception);
            }
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(attachment.fileName(), StandardCharsets.UTF_8)
                .build());
        headers.setContentType(parseMediaType(attachment.mimeType()));
        if (attachment.sizeBytes() != null) {
            headers.setContentLength(attachment.sizeBytes());
        }
        headers.setCacheControl("no-store");
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable long projectId,
            @PathVariable long attachmentId) {
        attachmentService.delete(projectId, attachmentId);
        return ResponseEntity.noContent().build();
    }

    private MediaType parseMediaType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(mimeType);
        } catch (org.springframework.http.InvalidMediaTypeException exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
