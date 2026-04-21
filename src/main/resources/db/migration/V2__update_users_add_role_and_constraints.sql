ALTER TABLE users
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'USER';

ALTER TABLE users
    ADD CONSTRAINT uq_users_username UNIQUE (username);

ALTER TABLE users
    ADD CONSTRAINT uq_users_email UNIQUE (email);
