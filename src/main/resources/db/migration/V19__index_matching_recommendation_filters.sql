ALTER TABLE public.funding_opportunities
    ADD COLUMN impact_area VARCHAR(150);

CREATE INDEX idx_funding_opportunities_impact_area
    ON public.funding_opportunities (LOWER(impact_area));

CREATE INDEX idx_funding_opportunities_max_amount
    ON public.funding_opportunities (max_amount);

CREATE INDEX idx_funding_opportunities_closing_date
    ON public.funding_opportunities (closing_date);
