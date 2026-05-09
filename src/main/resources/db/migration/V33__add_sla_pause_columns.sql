ALTER TABLE sla_tracking
    ADD COLUMN IF NOT EXISTS paused_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS total_paused_duration_minutes BIGINT NOT NULL DEFAULT 0;