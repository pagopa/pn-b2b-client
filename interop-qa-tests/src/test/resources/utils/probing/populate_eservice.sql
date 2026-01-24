-- Function UUID deterministici
CREATE
OR REPLACE FUNCTION uuid_from_int(p_seed bigint)
RETURNS uuid
LANGUAGE sql
IMMUTABLE
AS $$
SELECT (
           substr(md5(p_seed::text), 1, 8) || '-' ||
           substr(md5(p_seed::text), 9, 4) || '-' ||
           substr(md5(p_seed::text), 13, 4) || '-' ||
           substr(md5(p_seed::text), 17, 4) || '-' ||
           substr(md5(p_seed::text), 21, 12)
           ) ::uuid;
$$;

DO
$$
DECLARE
  -- =====================
  -- CONFIG (MODIFICABILE)
  -- =====================
v_eservices_count int := 20000;

  -- tecnologie ammesse
  v_technologies
text[] := ARRAY['REST','SOAP'];

  -- valori di default uguali per tutti
  v_polling_start_time
time := time '08:00';
  v_polling_end_time
time := time '20:00';
  v_polling_frequency
int  := 15;

  -- probing di default
  v_probing_enabled_default
boolean := false;

BEGIN
INSERT INTO eservices (eservice_id,
                       version_id,
                       eservice_name,
                       producer_name,
                       eservice_technology,
                       base_path,
                       audience,
                       state,
                       version_number,
                       lock_version,
                       probing_enabled,
                       polling_start_time,
                       polling_end_time,
                       polling_frequency)
SELECT
    -- UUID deterministici (stabili ad ogni run)
    uuid_from_int(gs::bigint)              AS eservice_id,
    uuid_from_int(1000000000 + gs::bigint) AS version_id,

    -- Nome univoco e deterministico
    ('ESVC-' || lpad(gs::text, 8, '0'))    AS eservice_name,

    -- Producer deterministico (puoi sostituire con una lista se vuoi)
    ('Producer ' || ((gs - 1) % 50 + 1))      AS producer_name,

    -- Tecnologia SOLO REST/SOAP, alternata deterministicamente
    v_technologies[1 + ((gs - 1) % array_length(v_technologies, 1))] AS eservice_technology,

    -- base_path deterministica
    ARRAY[
      '/api/v1/' || lower('esvc-' || lpad(gs::text, 8, '0')),
      '/health'
    ]::varchar[] AS base_path,

    -- audience deterministica (esempio)
    ARRAY[
      'AUD_' || (1 + ((gs - 1) % 20))
    ]::varchar[] AS audience,

    -- stato deterministico (metti pure fisso se preferisci)
    'PUBLISHED'                                AS state,

    -- versioning deterministico
    1                                          AS version_number,
    0                                          AS lock_version,

    -- probing_enabled sempre false (default richiesto)
    v_probing_enabled_default                  AS probing_enabled,

    -- polling uguale per tutti (default richiesto)
    v_polling_start_time                       AS polling_start_time,
    v_polling_end_time                         AS polling_end_time,
    v_polling_frequency                        AS polling_frequency

FROM generate_series(1, v_eservices_count) gs;

RAISE
NOTICE 'Inserted % rows into eservices (deterministic, REST/SOAP, polling fixed, probing=false).', v_eservices_count;
END $$;
