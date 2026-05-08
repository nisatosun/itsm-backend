package com.nisa.itsm.ticket.service;

import com.nisa.itsm.common.enums.TicketStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

import com.nisa.itsm.workflow.transition.WorkflowTransitionPolicy;

class TicketWorkflowValidationTest {

        @Test
        void shouldRejectInvalidTransitionFromNewToClosed() throws Exception {

                WorkflowTransitionPolicy policy = new WorkflowTransitionPolicy();

                TicketService ticketService = new TicketService(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                policy,
                                null,
                                null);

                Method method = TicketService.class.getDeclaredMethod(
                                "isValidTransition",
                                TicketStatus.class,
                                TicketStatus.class);

                method.setAccessible(true);

                boolean result = (boolean) method.invoke(
                                ticketService,
                                TicketStatus.NEW,
                                TicketStatus.CLOSED);

                assertFalse(result);
        }

        @Test
        void shouldAllowValidTransitionFromNewToInProgress() throws Exception {

                WorkflowTransitionPolicy policy = new WorkflowTransitionPolicy();

                TicketService ticketService = new TicketService(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                policy,
                                null,
                                null);

                Method method = TicketService.class.getDeclaredMethod(
                                "isValidTransition",
                                TicketStatus.class,
                                TicketStatus.class);

                method.setAccessible(true);

                boolean result = (boolean) method.invoke(
                                ticketService,
                                TicketStatus.NEW,
                                TicketStatus.IN_PROGRESS);

                assertTrue(result);
        }
}
