-- USERS
INSERT INTO users (username, email, password, role)
VALUES
    ('admin_seed', 'admin@test.com', 'dummy', 'ADMIN'),
    ('agent_seed', 'agent@test.com', 'dummy', 'AGENT')
    ON CONFLICT (username) DO NOTHING;

-- USER ROLES
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'admin_seed'
    ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'AGENT' FROM users WHERE username = 'agent_seed'
    ON CONFLICT (user_id, role) DO NOTHING;
