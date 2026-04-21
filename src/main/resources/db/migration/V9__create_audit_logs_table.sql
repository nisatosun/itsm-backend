CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT,
                            action VARCHAR(100) NOT NULL,
                            entity_type VARCHAR(50),
                            entity_id BIGINT,
                            description TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_user
                                FOREIGN KEY (user_id) REFERENCES users(id)
);
