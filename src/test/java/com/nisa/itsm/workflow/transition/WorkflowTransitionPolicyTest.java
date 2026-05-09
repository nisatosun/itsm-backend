package com.nisa.itsm.workflow.transition;

import com.nisa.itsm.common.enums.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowTransitionPolicyTest {

    private WorkflowTransitionPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new WorkflowTransitionPolicy();
    }

    @Test
    void isAllowed_ValidTransitions_ShouldReturnTrue() {
        assertThat(policy.isAllowed(TicketStatus.NEW, TicketStatus.TRIAGE)).isTrue();
        assertThat(policy.isAllowed(TicketStatus.TRIAGE, TicketStatus.ASSIGNED)).isTrue();
        assertThat(policy.isAllowed(TicketStatus.ASSIGNED, TicketStatus.IN_PROGRESS)).isTrue();

        assertThat(policy.isAllowed(TicketStatus.IN_PROGRESS, TicketStatus.WAITING_FOR_CUSTOMER)).isTrue();
        assertThat(policy.isAllowed(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED)).isTrue();

        assertThat(policy.isAllowed(TicketStatus.WAITING_FOR_CUSTOMER, TicketStatus.IN_PROGRESS)).isTrue();

        assertThat(policy.isAllowed(TicketStatus.RESOLVED, TicketStatus.CLOSED)).isTrue();
        assertThat(policy.isAllowed(TicketStatus.RESOLVED, TicketStatus.IN_PROGRESS)).isTrue();
    }

    @Test
    void isAllowed_InvalidTransitions_ShouldReturnFalse() {
        assertThat(policy.isAllowed(TicketStatus.NEW, TicketStatus.CLOSED)).isFalse();
        assertThat(policy.isAllowed(TicketStatus.NEW, TicketStatus.IN_PROGRESS)).isFalse();
        assertThat(policy.isAllowed(TicketStatus.TRIAGE, TicketStatus.IN_PROGRESS)).isFalse();

        assertThat(policy.isAllowed(TicketStatus.CLOSED, TicketStatus.IN_PROGRESS)).isFalse();
        assertThat(policy.isAllowed(TicketStatus.CLOSED, TicketStatus.NEW)).isFalse();

        assertThat(policy.isAllowed(TicketStatus.WAITING_FOR_CUSTOMER, TicketStatus.CLOSED)).isFalse();

        assertThat(policy.isAllowed(TicketStatus.RESOLVED, TicketStatus.NEW)).isFalse();
    }

    @Test
    void isAllowed_NullInputs_ShouldReturnFalse() {
        assertThat(policy.isAllowed(null, TicketStatus.IN_PROGRESS)).isFalse();
        assertThat(policy.isAllowed(TicketStatus.NEW, null)).isFalse();
        assertThat(policy.isAllowed(null, null)).isFalse();
    }

    @Test
    void isAllowed_SameStatusTransition_ShouldReturnFalse() {
        assertThat(policy.isAllowed(TicketStatus.NEW, TicketStatus.NEW)).isFalse();
        assertThat(policy.isAllowed(TicketStatus.IN_PROGRESS, TicketStatus.IN_PROGRESS)).isFalse();
    }
}
