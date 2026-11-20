-- V6: heritage
CREATE TABLE historical_event (
    id             uuid PRIMARY KEY,
    archive_id     uuid NOT NULL REFERENCES archive(id),
    title          text NOT NULL,
    description    text,
    event_date     date,
    date_precision text NOT NULL DEFAULT 'UNKNOWN'
                   CHECK (date_precision IN
                     ('DAY','MONTH','YEAR','DECADE','APPROXIMATE','UNKNOWN')),
    location_id    uuid REFERENCES location(id),
    event_type     text NOT NULL
                   CHECK (event_type IN ('POLITICAL','SOCIAL','CULTURAL','LEGAL','PERSONAL','OTHER')),
    asserted_by    uuid NOT NULL REFERENCES contributor(id),
    created_at     timestamptz NOT NULL DEFAULT now()
);
CREATE INDEX ix_historical_event_date     ON historical_event (event_date);
CREATE INDEX ix_historical_event_location ON historical_event (location_id);

CREATE TABLE organisation (
    id                uuid PRIMARY KEY,
    archive_id        uuid NOT NULL REFERENCES archive(id),
    name              text NOT NULL,
    organisation_type text NOT NULL
                      CHECK (organisation_type IN (
                        'POLITICAL','CIVIC','RELIGIOUS','EDUCATION','MILITARY','BUSINESS','OTHER')),
    founded_date      date,
    dissolved_date    date,
    description       text,
    created_at        timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT ck_organisation_period
        CHECK (dissolved_date IS NULL OR founded_date IS NULL OR dissolved_date >= founded_date)
);

CREATE TABLE organisation_membership (
    id              uuid PRIMARY KEY,
    person_id       uuid NOT NULL REFERENCES person(id),
    organisation_id uuid NOT NULL REFERENCES organisation(id),
    role            text,
    start_date      date,
    end_date        date,
    asserted_by     uuid NOT NULL REFERENCES contributor(id),
    CONSTRAINT ck_org_membership_period
        CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);
CREATE UNIQUE INDEX ux_org_membership
    ON organisation_membership
       (person_id, organisation_id, COALESCE(start_date, DATE '0001-01-01'));
