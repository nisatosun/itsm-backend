CREATE TABLE sla_tracking (
                              id BIGSERIAL PRIMARY KEY,
                              ticket_id BIGINT NOT NULL,
                              policy_id BIGINT NOT NULL,

                              response_due TIMESTAMP,
                              resolution_due TIMESTAMP,

                              response_breached BOOLEAN DEFAULT FALSE,
                              resolution_breached BOOLEAN DEFAULT FALSE,

                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_sla_ticket
                                  FOREIGN KEY (ticket_id) REFERENCES tickets(id),

                              CONSTRAINT fk_sla_policy
                                  FOREIGN KEY (policy_id) REFERENCES sla_policies(id)
);
