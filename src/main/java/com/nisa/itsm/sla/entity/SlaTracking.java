package com.nisa.itsm.sla.entity;

import com.nisa.itsm.common.entity.BaseEntity;
import com.nisa.itsm.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sla_tracking")
public class SlaTracking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private SlaPolicy policy;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "first_response_due_date", nullable = false)
    private LocalDateTime firstResponseDueDate;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(nullable = false)
    private Boolean breached = false;

    @Column(name = "breached_at")
    private LocalDateTime breachedAt;
}
