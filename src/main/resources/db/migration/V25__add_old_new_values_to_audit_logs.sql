ALTER TABLE audit_logs
    ADD COLUMN IF NOT EXISTS old_value TEXT;

ALTER TABLE audit_logs
    ADD COLUMN IF NOT EXISTS new_value TEXT;
