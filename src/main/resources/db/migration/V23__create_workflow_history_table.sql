CREATE TABLE workflow_history (
                                  id BIGSERIAL PRIMARY KEY,

                                  ticket_id BIGINT NOT NULL,
                                  from_status VARCHAR(30) NOT NULL,
                                  to_status VARCHAR(30) NOT NULL,

                                  action VARCHAR(50) NOT NULL,
                                  comment TEXT,

                                  performed_by_id BIGINT NOT NULL,
                                  performed_by_username VARCHAR(100) NOT NULL,

                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_workflow_history_ticket
                                      FOREIGN KEY (ticket_id) REFERENCES tickets(id),

                                  CONSTRAINT fk_workflow_history_performed_by
                                      FOREIGN KEY (performed_by_id) REFERENCES users(id)
);

CREATE INDEX idx_workflow_history_ticket_id
    ON workflow_history(ticket_id);
