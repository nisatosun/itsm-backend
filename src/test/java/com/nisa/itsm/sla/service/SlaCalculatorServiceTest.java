package com.nisa.itsm.sla.service;

import com.nisa.itsm.sla.entity.SlaPolicy;
import com.nisa.itsm.common.enums.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlaCalculatorServiceTest {

    private SlaCalculatorService slaCalculatorService;

    @BeforeEach
    void setUp() {
        slaCalculatorService = new SlaCalculatorService();
    }

    @Test
    void shouldCalculateDueDateCorrectly() {

        SlaPolicy policy = new SlaPolicy();
        policy.setPriority(Priority.HIGH);
        policy.setResolutionTimeHours(4);

        LocalDateTime startTime = LocalDateTime.of(
                2026,
                5,
                8,
                10,
                0
        );

        LocalDateTime dueDate =
                slaCalculatorService.calculateDueDate(
                        startTime,
                        policy.getResolutionTimeHours()
                );

        assertEquals(
                LocalDateTime.of(2026, 5, 8, 14, 0),
                dueDate
        );
    }
}
