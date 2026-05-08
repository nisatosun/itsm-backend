ALTER TABLE sla_tracking
    ADD COLUMN IF NOT EXISTS due_date TIMESTAMP;

ALTER TABLE sla_tracking
    ADD COLUMN IF NOT EXISTS first_response_due_date TIMESTAMP;
