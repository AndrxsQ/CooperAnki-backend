ALTER TABLE coopera.projects
    ADD COLUMN starts_at TIMESTAMPTZ,
    ADD COLUMN closes_at TIMESTAMPTZ,
    ADD CONSTRAINT ck_projects_schedule
        CHECK (starts_at IS NULL OR closes_at IS NULL OR closes_at > starts_at);

CREATE INDEX idx_projects_closes_at ON coopera.projects (closes_at);
