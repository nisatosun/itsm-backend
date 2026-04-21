CREATE TABLE comments (
                          id BIGSERIAL PRIMARY KEY,
                          ticket_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          is_internal BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_comments_ticket
                              FOREIGN KEY (ticket_id) REFERENCES tickets(id),

                          CONSTRAINT fk_comments_user
                              FOREIGN KEY (user_id) REFERENCES users(id)
);
