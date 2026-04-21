CREATE TABLE worklogs (
                          id BIGSERIAL PRIMARY KEY,
                          ticket_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          time_spent INTEGER NOT NULL,
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_worklogs_ticket
                              FOREIGN KEY (ticket_id) REFERENCES tickets(id),

                          CONSTRAINT fk_worklogs_user
                              FOREIGN KEY (user_id) REFERENCES users(id)
);
