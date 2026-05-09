-- Create composite index for auto-close scheduled job
CREATE INDEX IF NOT EXISTS idx_tickets_status_resolved_at ON tickets(status, resolved_at);

-- Seed the system user for background jobs
INSERT INTO users (username, email, password)
VALUES ('system', 'system@local', 'system')
ON CONFLICT (username) DO NOTHING;

-- Grant SYSTEM user the ADMIN role so it can perform any operation
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'system'
ON CONFLICT DO NOTHING;
