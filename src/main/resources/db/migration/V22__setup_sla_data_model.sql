CREATE TABLE IF NOT EXISTS sla_policies (
                                            id BIGSERIAL PRIMARY KEY,
                                            priority VARCHAR(20) NOT NULL UNIQUE,
    response_time_hours INT NOT NULL,
    resolution_time_hours INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS sla_tracking (
                                            id BIGSERIAL PRIMARY KEY,
                                            ticket_id BIGINT NOT NULL UNIQUE,
                                            policy_id BIGINT NOT NULL,
                                            start_time TIMESTAMP NOT NULL,
                                            due_date TIMESTAMP NOT NULL,
                                            first_response_due_date TIMESTAMP NOT NULL,
                                            resolved_at TIMESTAMP,
                                            breached BOOLEAN NOT NULL DEFAULT FALSE,
                                            breached_at TIMESTAMP,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            updated_at TIMESTAMP,

                                            CONSTRAINT fk_sla_tracking_ticket_ref
                                            FOREIGN KEY (ticket_id) REFERENCES tickets(id),

    CONSTRAINT fk_sla_tracking_policy_ref
    FOREIGN KEY (policy_id) REFERENCES sla_policies(id)
    );

INSERT INTO sla_policies (priority, response_time_hours, resolution_time_hours, active)
VALUES
    ('LOW', 24, 72, true),
    ('MEDIUM', 8, 48, true),
    ('HIGH', 4, 24, true),
    ('CRITICAL', 1, 8, true)
    ON CONFLICT (priority) DO UPDATE SET
    response_time_hours = EXCLUDED.response_time_hours,
                                  resolution_time_hours = EXCLUDED.resolution_time_hours,
                                  active = EXCLUDED.active;