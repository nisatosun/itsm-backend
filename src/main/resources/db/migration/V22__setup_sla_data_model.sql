CREATE TABLE IF NOT EXISTS sla_policies (
                                            id BIGSERIAL PRIMARY KEY,
                                            priority VARCHAR(20) NOT NULL UNIQUE,
    response_time_hours INT NOT NULL,
    resolution_time_hours INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
    );

ALTER TABLE sla_policies
    ADD COLUMN IF NOT EXISTS priority VARCHAR(20);

ALTER TABLE sla_policies
    ADD COLUMN IF NOT EXISTS response_time_hours INT;

ALTER TABLE sla_policies
    ADD COLUMN IF NOT EXISTS resolution_time_hours INT;

ALTER TABLE sla_policies
    ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE;

ALTER TABLE sla_policies
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE sla_policies
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE sla_policies
SET active = TRUE
WHERE active IS NULL;

ALTER TABLE sla_policies
    ALTER COLUMN active SET DEFAULT TRUE;

ALTER TABLE sla_policies
    ALTER COLUMN active SET NOT NULL;

UPDATE sla_policies
SET response_time_hours = 8
WHERE response_time_hours IS NULL;

UPDATE sla_policies
SET resolution_time_hours = 48
WHERE resolution_time_hours IS NULL;

ALTER TABLE sla_policies
    ALTER COLUMN response_time_hours SET NOT NULL;

ALTER TABLE sla_policies
    ALTER COLUMN resolution_time_hours SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_sla_policies_priority
    ON sla_policies(priority);

CREATE TABLE IF NOT EXISTS sla_tracking (
                                            id BIGSERIAL PRIMARY KEY,
                                            ticket_id BIGINT NOT NULL UNIQUE,
                                            policy_id BIGINT NOT NULL,
                                            start_time TIMESTAMP NOT NULL,
                                            due_date TIMESTAMP NOT NULL,
                                            first_response_due_date TIMESTAMP,
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

ALTER TABLE sla_tracking
    ADD COLUMN IF NOT EXISTS policy_id BIGINT;

ALTER TABLE sla_tracking
    ADD COLUMN IF NOT EXISTS first_response_due_date TIMESTAMP;

ALTER TABLE sla_tracking
    ADD COLUMN IF NOT EXISTS resolved_at TIMESTAMP;

INSERT INTO sla_policies (
    name,
    priority,
    response_time_minutes,
    resolution_time_minutes,
    response_time_hours,
    resolution_time_hours,
    active
)
VALUES
    ('Low Priority Policy', 'LOW', 1440, 4320, 24, 72, true),
    ('Medium Priority Policy', 'MEDIUM', 480, 2880, 8, 48, true),
    ('High Priority Policy', 'HIGH', 240, 1440, 4, 24, true),
    ('Critical Priority Policy', 'CRITICAL', 60, 480, 1, 8, true)
    ON CONFLICT (priority) DO UPDATE SET
    name = EXCLUDED.name,
                                  response_time_minutes = EXCLUDED.response_time_minutes,
                                  resolution_time_minutes = EXCLUDED.resolution_time_minutes,
                                  response_time_hours = EXCLUDED.response_time_hours,
                                  resolution_time_hours = EXCLUDED.resolution_time_hours,
                                  active = EXCLUDED.active;
