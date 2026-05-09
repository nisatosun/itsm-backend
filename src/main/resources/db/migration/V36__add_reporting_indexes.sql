-- Indexes to improve reporting query performance

-- Agent Performance query aggregates worklogs by user_id
CREATE INDEX IF NOT EXISTS idx_worklogs_user_id ON worklogs(user_id);
