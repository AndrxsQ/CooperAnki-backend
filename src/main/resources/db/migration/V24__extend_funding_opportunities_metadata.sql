-- Amplía public.funding_opportunities con la información de convocatorias que
-- no tenía columna (ver convocatorias_descripcion.txt, sección 4).
-- Todas las columnas son opcionales (NULL): no se añaden defaults ni NOT NULL
-- y no se modifican las columnas ni constraints existentes.
ALTER TABLE public.funding_opportunities
    ADD COLUMN IF NOT EXISTS donor_country VARCHAR(150),
    ADD COLUMN IF NOT EXISTS cooperation_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS organization_type TEXT,
    ADD COLUMN IF NOT EXISTS project_type VARCHAR(100),
    ADD COLUMN IF NOT EXISTS target_populations TEXT,
    ADD COLUMN IF NOT EXISTS application_method TEXT,
    ADD COLUMN IF NOT EXISTS language VARCHAR(150),
    ADD COLUMN IF NOT EXISTS currency VARCHAR(10),
    ADD COLUMN IF NOT EXISTS amount_in_cop NUMERIC(15, 2),
    ADD COLUMN IF NOT EXISTS source_page VARCHAR(100),
    ADD COLUMN IF NOT EXISTS source_document TEXT,
    ADD COLUMN IF NOT EXISTS opportunity_status VARCHAR(50),
    ADD COLUMN IF NOT EXISTS opportunity_status_detail TEXT,
    ADD COLUMN IF NOT EXISTS contact_person VARCHAR(150),
    ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(100),
    ADD COLUMN IF NOT EXISTS contact_fax VARCHAR(100),
    ADD COLUMN IF NOT EXISTS contact_address TEXT,
    ADD COLUMN IF NOT EXISTS contact_city VARCHAR(150),
    ADD COLUMN IF NOT EXISTS contact_email VARCHAR(150),
    ADD COLUMN IF NOT EXISTS local_contact_person VARCHAR(150),
    ADD COLUMN IF NOT EXISTS local_contact_phone VARCHAR(100),
    ADD COLUMN IF NOT EXISTS local_contact_address TEXT,
    ADD COLUMN IF NOT EXISTS local_contact_city VARCHAR(150),
    ADD COLUMN IF NOT EXISTS local_contact_email VARCHAR(150),
    ADD COLUMN IF NOT EXISTS notes TEXT;

-- Índice para futuros filtros por país del donante (mismo estilo que V19).
CREATE INDEX IF NOT EXISTS idx_funding_opportunities_donor_country
    ON public.funding_opportunities (LOWER(donor_country));
