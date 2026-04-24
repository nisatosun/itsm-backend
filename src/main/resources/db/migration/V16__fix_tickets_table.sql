ALTER TABLE tickets
    ADD COLUMN ticket_no VARCHAR(30) UNIQUE;

ALTER TABLE tickets
    ADD COLUMN process_instance_id BIGINT;

ALTER TABLE tickets
    ADD COLUMN resolved_at TIMESTAMP;

ALTER TABLE tickets
    ADD COLUMN closed_at TIMESTAMP;

ALTER TABLE tickets
DROP COLUMN workflow_state;
