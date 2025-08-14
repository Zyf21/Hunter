CREATE TABLE IF NOT EXISTS public.vacancy (
                                              id            SERIAL PRIMARY KEY,
                                              vacancy_id    VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    employer      VARCHAR(255),
    work_type     VARCHAR(255),
    schedule      VARCHAR(255),
    description   TEXT,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
    );