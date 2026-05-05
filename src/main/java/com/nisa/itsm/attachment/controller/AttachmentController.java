package com.nisa.itsm.attachment.controller;

import com.nisa.itsm.attachment.dto.response.AttachmentResponse;
import com.nisa.itsm.attachment.entity.Attachment;
import com.nisa.itsm.attachment.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.nisa.itsm.audit.annotation.Audit;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Audit(action = "ATTACHMENT_UPLOADED")
    @PostMapping("/tickets/{id}/attachments")
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        AttachmentResponse response = attachmentService.uploadAttachment(id, file, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tickets/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachmentsByTicket(
            @PathVariable Long id,
            Principal principal) {

        List<AttachmentResponse> responses = attachmentService.getAttachmentsByTicket(id, principal.getName());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/attachments/{id}")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable Long id,
            Principal principal) {

        Resource resource = attachmentService.downloadAttachment(id, principal.getName());
        Attachment attachment = attachmentService.getAttachmentEntity(id);

        MediaType mediaType = attachment.getContentType() != null
                ? MediaType.parseMediaType(attachment.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getOriginalFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/attachments/{id}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long id,
            Principal principal) {

        attachmentService.deleteAttachment(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
