CREATE TABLE shipments
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    request_id  UUID REFERENCES van_load_requests (id),
    name        VARCHAR(255),
    volume      DECIMAL,
    revenue     DECIMAL,
    is_selected BOOLEAN
);