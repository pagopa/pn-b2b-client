TRUNCATE TABLE qa_probing.eservices CASCADE;
DO
$$ DECLARE
-- =====================
-- CONFIG (MODIFICABILE)
-- =====================
v_ok_count int := 10000;
v_error_count
int := 5000;
v_random_count
int := 5000;
v_base_host
text := 'http://probing-be-eservice-mock.qa:8080';
-- polling (UTC)
v_polling_start_time
timetz := '08:00:00+00';
v_polling_end_time
timetz := '17:00:00+00';
v_polling_frequency
int := 1;
v_probing_enabled_default
boolean := false;
v_eservices_count
int := 0;
BEGIN v_eservices_count
:= v_ok_count + v_error_count + v_random_count;
IF
v_eservices_count <= 0 THEN RAISE EXCEPTION 'Total eservices count must be > 0 (ok=% , error=% , random=%).',
v_ok_count,
v_error_count,
v_random_count;
END IF;
INSERT INTO qa_probing.eservices (id, eservice_id, version_id, eservice_name,
                                  producer_name, eservice_technology,
                                  base_path, audience, state, version_number,
                                  lock_version, probing_enabled, polling_start_time,
                                  polling_end_time, polling_frequency) OVERRIDING SYSTEM VALUE
SELECT gs :: bigint AS id,
  -- UUID deterministico da gs (
    substr(
            md5(gs :: bigint :: text),
            1,
            8
    ) || '-' || substr(
            md5(gs :: bigint :: text),
            9,
            4
                ) || '-' || substr(
            md5(gs :: bigint :: text),
            13,
            4
                            ) || '-' || substr(
            md5(gs :: bigint :: text),
            17,
            4
                                        ) || '-' || substr(
            md5(gs :: bigint :: text),
            21,
            12
                                                    )
    ):: uuid AS eservice_id,
-- UUID deterministico da (1000000000 + gs) (
    substr(
            md5(
                    (1000000000 + gs :: bigint):: text
            ),
            1,
            8
    ) || '-' || substr(
            md5(
                    (1000000000 + gs :: bigint):: text
            ),
            9,
            4
                ) || '-' || substr(
            md5(
                    (1000000000 + gs :: bigint):: text
            ),
            13,
            4
                            ) || '-' || substr(
            md5(
                    (1000000000 + gs :: bigint):: text
            ),
            17,
            4
                                        ) || '-' || substr(
            md5(
                    (1000000000 + gs :: bigint):: text
            ),
            21,
            12
                                                    )
    ):: uuid AS version_id, (
    'ESVC-' || lpad(gs :: text, 8, '0')
    ) AS eservice_name,
       (
           'Producer ' || (
               (gs - 1) % 50 + 1
           ) ) AS producer_name,
-- Tecnologia: deterministica e NON NULL (pari/dispari)
CASE WHEN (gs % 2) = 0 THEN 'SOAP' ELSE 'REST'
END AS eservice_technology,
-- base_path: host fisso + endpoint coerente con tecnologia + outcome configurabile
ARRAY[ v_base_host || CASE WHEN (gs % 2) = 0 THEN '/soap/interop/probing/' ELSE '/rest/interop/probing/'
END || CASE WHEN gs <= v_ok_count THEN 'ok' WHEN gs <= v_ok_count + v_error_count THEN 'error' ELSE 'random' END ] :: varchar[] AS base_path,
ARRAY[ 'AUD_' || (
  1 + (
    (gs - 1) % 20
  )
) ] :: varchar[] AS audience,
'ACTIVE' AS state,
1 AS version_number,
0 AS lock_version,
v_probing_enabled_default AS probing_enabled,
v_polling_start_time AS polling_start_time,
v_polling_end_time AS polling_end_time,
v_polling_frequency AS polling_frequency
FROM
  generate_series(1, v_eservices_count) gs;
RAISE
NOTICE 'Inserted % rows (ok=% , error=% , random=%).',
v_eservices_count,
v_ok_count,
v_error_count,
v_random_count;
END $$;
