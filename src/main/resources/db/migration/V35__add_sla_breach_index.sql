-- Create composite index for SLA breach background job
CREATE INDEX IF NOT EXISTS idx_sla_tracking_breached_due_date ON sla_tracking(breached, due_date);
