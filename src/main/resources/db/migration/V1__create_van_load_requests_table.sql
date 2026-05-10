CREATE TABLE van_load_requests
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_volume  DECIMAL,
    total_revenue DECIMAL,
    max_volume    DECIMAL,
    created_at    TIMESTAMP,
    load_status   VARCHAR
);