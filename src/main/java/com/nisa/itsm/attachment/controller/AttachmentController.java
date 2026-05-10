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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Attachment Controller", description = "File upload and retrieval for tickets")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping(value = "/tickets/{id}/attachments", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload attachment", description = "Uploads a multipart file attachment to a specific ticket")
    @ApiResponse(responseCode = "201", description = "Attachment successfully uploaded")
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        AttachmentResponse response = attachmentService.uploadAttachment(id, file, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tickets/{id}/attachments")
    @Operation(summary = "Get attachments by ticket", description = "Retrieves all attachment metadata for a specific ticket")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachmentsByTicket(
            @PathVariable Long id,
            Principal principal) {

        List<AttachmentResponse> responses = attachmentService.getAttachmentsByTicket(id, principal.getName());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/attachments/{id}")
    @Operation(summary = "Download attachment", description = "Downloads the binary file content of an attachment")
    @ApiResponse(responseCode = "200", description = "Successfully downloaded file")
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
    @Operation(summary = "Delete attachment", description = "Deletes an attachment and its associated file")
    @ApiResponse(responseCode = "204", description = "Attachment successfully deleted")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable Long id,
            Principal principal) {

        attachmentService.deleteAttachment(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
