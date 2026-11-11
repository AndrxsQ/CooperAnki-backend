CREATE INDEX idx_projects_embedding_hnsw
    ON public.projects
    USING hnsw (embedding_vector vector_cosine_ops);

CREATE INDEX idx_funding_opportunities_embedding_hnsw
    ON public.funding_opportunities
    USING hnsw (embedding_vector vector_cosine_ops);
