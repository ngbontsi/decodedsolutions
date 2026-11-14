-- Repeatable: read models. Safe to re-run.
CREATE OR REPLACE VIEW v_person_display_name AS
SELECT p.id AS person_id,
       COALESCE(
           (SELECT n.full_name
              FROM person_name n
             WHERE n.person_id = p.id
               AND n.is_preferred
               AND (n.valid_to IS NULL OR n.valid_to >= current_date)
             LIMIT 1),
           (SELECT n.full_name
              FROM person_name n
             WHERE n.person_id = p.id
               AND (n.valid_to IS NULL OR n.valid_to >= current_date)
             ORDER BY n.valid_from DESC NULLS LAST, n.created_at DESC
             LIMIT 1)
       ) AS display_name
FROM person p
WHERE p.status = 'ACTIVE';
