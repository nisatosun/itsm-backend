package com.nisa.itsm.sla.service;

import com.nisa.itsm.audit.service.AuditLogService;
import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.sla.dto.request.CreateSlaPolicyRequest;
import com.nisa.itsm.sla.dto.request.UpdateSlaPolicyRequest;
import com.nisa.itsm.sla.dto.response.SlaPolicyResponse;
import com.nisa.itsm.sla.dto.response.SlaTrackingResponse;
import com.nisa.itsm.sla.entity.SlaPolicy;
import com.nisa.itsm.sla.entity.SlaTracking;
import com.nisa.itsm.sla.repository.SlaPolicyRepository;
import com.nisa.itsm.sla.repository.SlaTrackingRepository;
import com.nisa.itsm.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class SlaService {

    private final SlaPolicyRepository policyRepository;
    private final SlaTrackingRepository trackingRepository;
    private final SlaCalculatorService calculatorService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public void initializeForTicket(Ticket ticket) {
        SlaPolicy policy = policyRepository
                .findByPriorityAndActiveTrue(ticket.getPriority())
                .orElseThrow(() -> new ResourceNotFoundException("SLA policy not found"));

        SlaTracking tracking = calculatorService.initializeSlaTracking(
                ticket,
                policy,
                LocalDateTime.now()
        );

        trackingRepository.save(tracking);
    }

    @Transactional(readOnly = true)
    public List<SlaPolicyResponse> getPolicies() {
        return policyRepository.findAll()
                .stream()
                .map(this::toPolicyResponse)
                .toList();
    }

    public SlaPolicyResponse createPolicy(CreateSlaPolicyRequest request) {
        SlaPolicy policy = new SlaPolicy();

        boolean changed =
                !Objects.equals(policy.getResponseTimeHours(), request.getResponseTimeHours()) ||
                        !Objects.equals(policy.getResolutionTimeHours(), request.getResolutionTimeHours()) ||
                        !Objects.equals(policy.getActive(), request.getActive());

        if (!changed) {
            return toPolicyResponse(policy);
        }

        policy.setPriority(request.getPriority());
        policy.setResponseTimeHours(request.getResponseTimeHours());
        policy.setResolutionTimeHours(request.getResolutionTimeHours());
        policy.setActive(request.getActive() != null ? request.getActive() : true);

        SlaPolicy savedPolicy = policyRepository.save(policy);
        return toPolicyResponse(savedPolicy);
    }

    public SlaPolicyResponse updatePolicy(Long id, UpdateSlaPolicyRequest request) {
        SlaPolicy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SLA policy not found"));

        Integer oldResponseHours = policy.getResponseTimeHours();
        Integer oldResolutionHours = policy.getResolutionTimeHours();
        Boolean oldActive = policy.getActive();

        if (request.getResponseTimeHours() != null) {
            policy.setResponseTimeHours(request.getResponseTimeHours());
        }

        if (request.getResolutionTimeHours() != null) {
            policy.setResolutionTimeHours(request.getResolutionTimeHours());
        }

        if (request.getActive() != null) {
            policy.setActive(request.getActive());
        }

        SlaPolicy savedPolicy = policyRepository.save(policy);

        String oldValue =
                "response=" + oldResponseHours +
                        ", resolution=" + oldResolutionHours +
                        ", active=" + oldActive;

        String newValue =
                "response=" + savedPolicy.getResponseTimeHours() +
                        ", resolution=" + savedPolicy.getResolutionTimeHours() +
                        ", active=" + savedPolicy.getActive();

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        auditLogService.logAction(
                "SLA_POLICY",
                savedPolicy.getId(),
                "SLA_POLICY_UPDATED",
                currentUser.getId(),
                "SLA policy updated for priority: " + savedPolicy.getPriority(),
                oldValue,
                newValue
        );

        return toPolicyResponse(savedPolicy);
    }

    @Transactional(readOnly = true)
    public SlaTrackingResponse getTicketSla(Long ticketId) {
        SlaTracking tracking = trackingRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SLA tracking not found"));

        return toTrackingResponse(tracking);
    }

    private SlaPolicyResponse toPolicyResponse(SlaPolicy policy) {
        SlaPolicyResponse response = new SlaPolicyResponse();
        response.setId(policy.getId());
        response.setPriority(policy.getPriority());
        response.setResponseTimeHours(policy.getResponseTimeHours());
        response.setResolutionTimeHours(policy.getResolutionTimeHours());
        response.setActive(policy.getActive());
        return response;
    }

    private SlaTrackingResponse toTrackingResponse(SlaTracking tracking) {
        SlaTrackingResponse response = new SlaTrackingResponse();
        response.setTicketId(tracking.getTicket().getId());
        response.setPolicyId(tracking.getPolicy().getId());
        response.setStartTime(tracking.getStartTime());
        response.setDueDate(tracking.getDueDate());
        response.setFirstResponseDueDate(tracking.getFirstResponseDueDate());
        response.setBreached(tracking.getBreached());
        response.setBreachedAt(tracking.getBreachedAt());
        response.setRemainingMinutes(calculateRemainingMinutes(tracking));
        response.setWarningLevel(calculateWarningLevel(tracking));
        return response;
    }

    private Long calculateRemainingMinutes(SlaTracking tracking) {
        if (Boolean.TRUE.equals(tracking.getBreached())) {
            return 0L;
        }

        long minutes = Duration.between(LocalDateTime.now(), tracking.getDueDate()).toMinutes();
        return Math.max(minutes, 0);
    }

    private String calculateWarningLevel(SlaTracking tracking) {
        if (Boolean.TRUE.equals(tracking.getBreached())) {
            return "BREACHED";
        }

        long remainingMinutes = calculateRemainingMinutes(tracking);

        if (remainingMinutes <= 60) {
            return "CRITICAL";
        }

        if (remainingMinutes <= 240) {
            return "WARNING";
        }

        return "NONE";
    }
}