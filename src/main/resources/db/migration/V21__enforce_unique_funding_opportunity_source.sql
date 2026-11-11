DO $$
BEGIN
    IF EXISTS (
        SELECT source_url
        FROM public.funding_opportunities
        GROUP BY source_url
        HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION
            'Cannot enforce unique funding opportunity source URLs: duplicate source_url values exist';
    END IF;
END;
$$;

CREATE UNIQUE INDEX uq_funding_opportunities_source_url
    ON public.funding_opportunities (source_url);
