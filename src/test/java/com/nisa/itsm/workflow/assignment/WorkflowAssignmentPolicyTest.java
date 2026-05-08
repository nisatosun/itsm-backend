package com.nisa.itsm.workflow.assignment;

import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkflowAssignmentPolicyTest {

    private WorkflowAssignmentPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new WorkflowAssignmentPolicy();
    }

    @Test
    void canAssign_AdminCanAssignToAgent_ReturnsTrue() {
        Ticket ticket = new Ticket();
        User agent = createAgentUser("agentUser");
        Authentication adminAuth = createAuth("adminUser", "ADMIN");

        assertThat(policy.canAssign(ticket, agent, adminAuth)).isTrue();
    }

    @Test
    void canAssign_ManagerCanAssignToAgent_ReturnsTrue() {
        Ticket ticket = new Ticket();
        User agent = createAgentUser("agentUser");
        Authentication managerAuth = createAuth("managerUser", "MANAGER");

        assertThat(policy.canAssign(ticket, agent, managerAuth)).isTrue();
    }

    @Test
    void canAssign_AdminCannotAssignToCustomer_ReturnsFalse() {
        Ticket ticket = new Ticket();
        User customer = new User();
        customer.setUsername("customerUser");
        customer.setRoles(Set.of(Role.CUSTOMER));
        Authentication adminAuth = createAuth("adminUser", "ADMIN");

        assertThat(policy.canAssign(ticket, customer, adminAuth)).isFalse();
    }

    @Test
    void canAssign_AgentCanClaimUnassignedTicket_ReturnsTrue() {
        Ticket ticket = new Ticket(); // unassigned
        User agent = createAgentUser("agentUser");
        Authentication agentAuth = createAuth("agentUser", "AGENT");

        assertThat(policy.canAssign(ticket, agent, agentAuth)).isTrue();
    }

    @Test
    void canAssign_AgentCannotAssignToAnotherAgent_ReturnsFalse() {
        Ticket ticket = new Ticket();
        User anotherAgent = createAgentUser("anotherAgentUser");
        Authentication agentAuth = createAuth("agentUser", "AGENT");

        assertThat(policy.canAssign(ticket, anotherAgent, agentAuth)).isFalse();
    }

    @Test
    void canAssign_AgentCannotClaimAssignedTicket_ReturnsFalse() {
        Ticket ticket = new Ticket();
        ticket.setAssignee(createAgentUser("someOtherAgent"));

        User agent = createAgentUser("agentUser");
        Authentication agentAuth = createAuth("agentUser", "AGENT");

        assertThat(policy.canAssign(ticket, agent, agentAuth)).isFalse();
    }

    @Test
    void canAssign_CustomerCannotAssignOrClaim_ReturnsFalse() {
        Ticket ticket = new Ticket();
        User agent = createAgentUser("agentUser");
        Authentication customerAuth = createAuth("customerUser", "CUSTOMER");

        assertThat(policy.canAssign(ticket, agent, customerAuth)).isFalse();

        User customer = new User();
        customer.setUsername("customerUser");
        assertThat(policy.canAssign(ticket, customer, customerAuth)).isFalse();
    }

    @Test
    void canAssign_NullInputs_ReturnsFalse() {
        assertThat(policy.canAssign(null, new User(), mock(Authentication.class))).isFalse();
        assertThat(policy.canAssign(new Ticket(), null, mock(Authentication.class))).isFalse();
        assertThat(policy.canAssign(new Ticket(), new User(), null)).isFalse();
    }

    private User createAgentUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setRoles(Set.of(Role.AGENT));
        return user;
    }

    private Authentication createAuth(String username, String role) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        // Ensure mocking type match
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role));
        // Use an unchecked cast explicitly to avoid warnings or compile errors with
        // mockito generics if needed
        org.mockito.Mockito.<java.util.Collection<? extends GrantedAuthority>>when(auth.getAuthorities())
                .thenReturn(authorities);
        return auth;
    }
}
