package com.nisa.itsm.workflow.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WorkflowService {

    public Long startTicketProcess(Map<String, Object> variables) {

        System.out.println("Workflow started for ticket: "
                + variables.get("ticketId"));

        return System.currentTimeMillis();
    }
}