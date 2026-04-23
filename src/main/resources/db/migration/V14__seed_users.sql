-- USERS
INSERT INTO users (username, email, password)
VALUES
    ('admin_seed', 'admin@test.com', 'dummy'),
    ('agent_seed', 'agent@test.com', 'dummy'),
    ('manager_seed', 'manager@test.com', 'dummy'),
    ('customer_seed', 'customer@test.com', 'dummy')
    ON CONFLICT (username) DO NOTHING;

-- USER ROLES
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'admin_seed'
    ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'AGENT' FROM users WHERE username = 'agent_seed'
    ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'MANAGER' FROM users WHERE username = 'manager_seed'
    ON CONFLICT (user_id, role) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'CUSTOMER' FROM users WHERE username = 'customer_seed'
    ON CONFLICT (user_id, role) DO NOTHING;
