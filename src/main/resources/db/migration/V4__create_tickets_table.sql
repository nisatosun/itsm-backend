CREATE TABLE tickets (
                         id BIGSERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         priority VARCHAR(50) NOT NULL,
                         workflow_state VARCHAR(100) NOT NULL,
                         requester_id BIGINT NOT NULL,
                         assignee_id BIGINT,
                         category_id BIGINT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_tickets_requester
                             FOREIGN KEY (requester_id) REFERENCES users(id),

                         CONSTRAINT fk_tickets_assignee
                             FOREIGN KEY (assignee_id) REFERENCES users(id),

                         CONSTRAINT fk_tickets_category
                             FOREIGN KEY (category_id) REFERENCES categories(id)
);
