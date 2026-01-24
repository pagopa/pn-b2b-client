-- =========================================================
-- TENANTS
-- =========================================================
CREATE TABLE tenants
(
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   UUID    NOT NULL,
    tenant_name VARCHAR NOT NULL
);

-- =========================================================
-- ESERVICES
-- =========================================================
CREATE TABLE eservices
(
    id                  BIGSERIAL PRIMARY KEY,
    eservice_id         UUID    NOT NULL,
    version_id          UUID    NOT NULL,
    eservice_name       VARCHAR NOT NULL,
    producer_name       VARCHAR NOT NULL,
    eservice_technology VARCHAR NOT NULL,
    base_path           VARCHAR[] NOT NULL,
    audience            VARCHAR[] NOT NULL,
    state               VARCHAR NOT NULL,
    version_number      INT     NOT NULL,
    lock_version        INT     NOT NULL,
    probing_enabled     BOOLEAN NOT NULL,
    polling_start_time  TIME,
    polling_end_time    TIME,
    polling_frequency   INT
);

-- =========================================================
-- ESERVICE PROBING REQUESTS
-- (1:1 con eservices)
-- =========================================================
CREATE TABLE eservice_probing_requests
(
    eservices_record_id BIGINT PRIMARY KEY,
    last_request        TIMESTAMPTZ,
    CONSTRAINT fk_eservice_probing_requests
        FOREIGN KEY (eservices_record_id)
            REFERENCES eservices (id)
            ON DELETE CASCADE
);

-- =========================================================
-- ESERVICE PROBING RESPONSES
-- (1:1 con eservices)
-- =========================================================
CREATE TABLE eservice_probing_responses
(
    eservices_record_id BIGINT PRIMARY KEY,
    response_received   TIMESTAMPTZ,
    status              VARCHAR NOT NULL,
    CONSTRAINT fk_eservice_probing_responses
        FOREIGN KEY (eservices_record_id)
            REFERENCES eservices (id)
            ON DELETE CASCADE
);
