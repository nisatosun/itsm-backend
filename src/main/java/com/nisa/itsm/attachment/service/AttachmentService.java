package com.nisa.itsm.attachment.service;

import com.nisa.itsm.attachment.dto.response.AttachmentResponse;
import com.nisa.itsm.attachment.entity.Attachment;
import com.nisa.itsm.attachment.repository.AttachmentRepository;
import com.nisa.itsm.audit.entity.AuditLog;
import com.nisa.itsm.audit.repository.AuditLogRepository;
import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.exception.custom.BadRequestException;
import com.nisa.itsm.exception.custom.CustomAccessDeniedException;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.ticket.repository.TicketRepository;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import com.nisa.itsm.audit.annotation.Audit;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    private static final Path UPLOAD_PATH = Paths.get("uploads/attachments").toAbsolutePath().normalize();
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS =
            List.of(".pdf", ".png", ".jpg", ".jpeg", ".doc", ".docx");

    @Transactional
    public AttachmentResponse uploadAttachment(Long ticketId, MultipartFile file, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateTicketAccess(ticket, user);
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename).toLowerCase();
        String storedFilename = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(UPLOAD_PATH);

            Path filePath = UPLOAD_PATH.resolve(storedFilename).normalize();

            if (!filePath.startsWith(UPLOAD_PATH)) {
                throw new BadRequestException("Invalid file path");
            }

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Attachment attachment = Attachment.builder()
                    .ticket(ticket)
                    .uploadedBy(user)
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .storagePath(filePath.toString())
                    .build();

            Attachment saved = attachmentRepository.save(attachment);

            auditLogRepository.save(
                    AuditLog.builder()
                            .entityType("TICKET")
                            .entityId(ticket.getId())
                            .action("ATTACHMENT_UPLOADED")                            .performedBy(user.getId())
                            .details("Attachment uploaded: " + originalFilename)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            return mapToResponse(saved);

        } catch (IOException e) {
            log.error("Failed to store file: {}", originalFilename, e);
            throw new BadRequestException("File processing failed");
        }
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByTicket(Long ticketId, String username) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateTicketAccess(ticket, user);

        return attachmentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Resource downloadAttachment(Long attachmentId, String username) {
        Attachment attachment = getAttachmentEntity(attachmentId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateTicketAccess(attachment.getTicket(), user);

        try {
            Path filePath = Paths.get(attachment.getStoragePath()).toAbsolutePath().normalize();

            if (!filePath.startsWith(UPLOAD_PATH)) {
                throw new BadRequestException("Invalid file path");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found on disk");
            }

            return resource;

        } catch (IOException e) {
            log.error("Failed to read attachment file. attachmentId={}", attachmentId, e);
            throw new BadRequestException("File could not be read");
        }
    }

    @Transactional(readOnly = true)
    public Attachment getAttachmentEntity(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));
    }

    @Transactional
    public void deleteAttachment(Long attachmentId, String username) {
        Attachment attachment = getAttachmentEntity(attachmentId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Ticket ticket = attachment.getTicket();

        boolean isAdminOrManager = hasRole(user, Role.ADMIN) || hasRole(user, Role.MANAGER);

        boolean isAssignedAgent = hasRole(user, Role.AGENT)
                && ticket.getAssignee() != null
                && ticket.getAssignee().getUsername().equals(user.getUsername());

        boolean isUploaderCustomer = hasRole(user, Role.CUSTOMER)
                && attachment.getUploadedBy().getUsername().equals(user.getUsername());

        if (!isAdminOrManager && !isAssignedAgent && !isUploaderCustomer) {
            throw new CustomAccessDeniedException("You do not have permission to delete this attachment");
        }

        try {
            Path filePath = Paths.get(attachment.getStoragePath()).toAbsolutePath().normalize();

            if (filePath.startsWith(UPLOAD_PATH)) {
                Files.deleteIfExists(filePath);
            }

        } catch (IOException e) {
            log.error("Failed to delete attachment file. attachmentId={}", attachmentId, e);
            throw new BadRequestException("File could not be deleted");
        }

        auditLogRepository.save(
                AuditLog.builder()
                        .entityType("TICKET")
                        .entityId(ticket.getId())
                        .action("ATTACHMENT_DELETED")
                        .performedBy(user.getId())
                        .details("Attachment deleted: " + attachment.getOriginalFilename())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        attachmentRepository.delete(attachment);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds 10MB limit");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        if (!StringUtils.hasText(originalFilename)) {
            throw new BadRequestException("File name is required");
        }

        if (originalFilename.contains("..")) {
            throw new BadRequestException("Invalid file name");
        }

        String extension = getExtension(originalFilename).toLowerCase();

        if (extension.isBlank() || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Invalid file extension: " + extension);
        }
    }

    private void validateTicketAccess(Ticket ticket, User user) {
        if (hasRole(user, Role.ADMIN) || hasRole(user, Role.MANAGER)) {
            return;
        }

        if (hasRole(user, Role.CUSTOMER)) {
            boolean isRequester = ticket.getRequester() != null
                    && ticket.getRequester().getUsername().equals(user.getUsername());

            if (isRequester) {
                return;
            }
        }

        if (hasRole(user, Role.AGENT)) {
            boolean isAssignee = ticket.getAssignee() != null
                    && ticket.getAssignee().getUsername().equals(user.getUsername());

            if (isAssignee) {
                return;
            }
        }

        throw new CustomAccessDeniedException("You do not have permission to access this ticket's attachments");
    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles() != null
                && user.getRoles()
                .stream()
                .anyMatch(userRole -> userRole.name().equals(role.name()));
    }

    private String getExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .ticketId(attachment.getTicket().getId())
                .uploadedById(attachment.getUploadedBy().getId())
                .uploadedByUsername(attachment.getUploadedBy().getUsername())
                .originalFilename(attachment.getOriginalFilename())
                .contentType(attachment.getContentType())
                .fileSize(attachment.getFileSize())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}

