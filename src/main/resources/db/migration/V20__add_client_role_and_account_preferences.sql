INSERT INTO public.roles (name, description) VALUES
    ('CLIENT', 'Personal account for Coopera2075 platform users')
ON CONFLICT (name) DO NOTHING;

ALTER TABLE public.users
    ADD COLUMN email_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE;
