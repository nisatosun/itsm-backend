CREATE TABLE sla_policies (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(100) NOT NULL,
                              response_time_minutes INT NOT NULL,
                              resolution_time_minutes INT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
