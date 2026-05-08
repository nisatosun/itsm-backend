package com.nisa.itsm.workflow.assignment;

import com.nisa.itsm.common.enums.Role;
import com.nisa.itsm.ticket.entity.Ticket;
import com.nisa.itsm.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Policy class responsible for determining allowed ticket assignment and claim
 * rules.
 */
@Component
public class WorkflowAssignmentPolicy {

    /**
     * Checks if the authenticated user is allowed to assign the ticket to the
     * target assignee.
     *
     * @param ticket         The ticket to be assigned.
     * @param targetAssignee The user to whom the ticket will be assigned.
     * @param authentication The authentication context of the user performing the
     *                       action.
     * @return true if assignment is allowed, false otherwise.
     */
    public boolean canAssign(Ticket ticket, User targetAssignee, Authentication authentication) {
        if (ticket == null || targetAssignee == null || authentication == null) {
            return false;
        }

        boolean isAdmin = hasRole(authentication, "ADMIN");
        boolean isManager = hasRole(authentication, "MANAGER");
        boolean isAgent = hasRole(authentication, "AGENT");

        // Admin and Manager can assign any ticket to an Agent
        if (isAdmin || isManager) {
            return targetAssignee.getRoles() != null && targetAssignee.getRoles().contains(Role.AGENT);
        }

        // Agent logic
        if (isAgent) {
            boolean isAssigningToSelf = authentication.getName().equals(targetAssignee.getUsername());

            // Agents cannot assign to other agents
            if (!isAssigningToSelf) {
                return false;
            }

            // Agents can only claim unassigned tickets
            if (ticket.getAssignee() != null) {
                return false;
            }

            return true;
        }

        // Customers and other roles cannot assign or claim
        return false;
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role) || a.getAuthority().equals("ROLE_" + role));
    }
}
