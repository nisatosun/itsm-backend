package com.nisa.itsm.worklog.entity;

import com.nisa.itsm.common.entity.BaseEntity;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "worklogs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Worklog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "minutes_spent", nullable = false)
    private Integer minutesSpent;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
