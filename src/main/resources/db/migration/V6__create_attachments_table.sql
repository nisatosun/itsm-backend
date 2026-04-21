CREATE TABLE attachments (
                             id BIGSERIAL PRIMARY KEY,
                             ticket_id BIGINT NOT NULL,
                             uploaded_by BIGINT NOT NULL,
                             file_name VARCHAR(255) NOT NULL,
                             file_type VARCHAR(100),
                             file_path VARCHAR(500) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_attachments_ticket
                                 FOREIGN KEY (ticket_id) REFERENCES tickets(id),

                             CONSTRAINT fk_attachments_uploaded_by
                                 FOREIGN KEY (uploaded_by) REFERENCES users(id)
);
