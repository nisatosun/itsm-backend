CREATE TABLE IF NOT EXISTS attachments (
                                           id BIGSERIAL PRIMARY KEY,

                                           ticket_id BIGINT NOT NULL,
                                           uploaded_by BIGINT NOT NULL,

                                           original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_attachments_ticket
    FOREIGN KEY (ticket_id) REFERENCES tickets(id),

    CONSTRAINT fk_attachments_uploaded_by
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
    );
