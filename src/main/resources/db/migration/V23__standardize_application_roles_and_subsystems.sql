INSERT INTO public.roles (name, description) VALUES
    ('ADMIN', 'Administrator with access to all application subsystems'),
    ('GENERAL_EXE', 'General executive with access to cooperation and information management'),
    ('LEGAL_EXE', 'Legal executive with access to information management'),
    ('TERRITORIAL_EXE', 'Territorial executive with access to information management'),
    ('CLIENT', 'Personal account user')
ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM public.users account
        JOIN public.roles role ON role.id = account.role_id
        WHERE role.name NOT IN (
            'ADMIN',
            'GENERAL_EXE',
            'LEGAL_EXE',
            'TERRITORIAL_EXE',
            'CLIENT',
            'COOPERANT',
            'TERRITORIAL_TRAINER',
            'EXECUTIVE'
        )
    ) THEN
        RAISE EXCEPTION
            'Cannot standardize application roles: users have roles without an approved migration mapping';
    END IF;
END $$;

UPDATE public.users account
SET role_id = target_role.id
FROM public.roles current_role
JOIN public.roles target_role
    ON target_role.name = CASE current_role.name
        WHEN 'COOPERANT' THEN 'GENERAL_EXE'
        WHEN 'EXECUTIVE' THEN 'GENERAL_EXE'
        WHEN 'TERRITORIAL_TRAINER' THEN 'TERRITORIAL_EXE'
    END
WHERE account.role_id = current_role.id
  AND current_role.name IN ('COOPERANT', 'EXECUTIVE', 'TERRITORIAL_TRAINER');

DELETE FROM public.role_permissions;

DELETE FROM public.permissions
WHERE code NOT IN (
    'SYSTEM_COOPERATION_ACCESS',
    'SYSTEM_INFORMATION_MANAGEMENT_ACCESS',
    'SYSTEM_PERSONAL_ACCOUNT_ACCESS'
);

INSERT INTO public.permissions (code) VALUES
    ('SYSTEM_COOPERATION_ACCESS'),
    ('SYSTEM_INFORMATION_MANAGEMENT_ACCESS'),
    ('SYSTEM_PERSONAL_ACCOUNT_ACCESS')
ON CONFLICT (code) DO NOTHING;

INSERT INTO public.role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM (VALUES
    ('ADMIN', 'SYSTEM_COOPERATION_ACCESS'),
    ('ADMIN', 'SYSTEM_INFORMATION_MANAGEMENT_ACCESS'),
    ('ADMIN', 'SYSTEM_PERSONAL_ACCOUNT_ACCESS'),
    ('GENERAL_EXE', 'SYSTEM_COOPERATION_ACCESS'),
    ('GENERAL_EXE', 'SYSTEM_INFORMATION_MANAGEMENT_ACCESS'),
    ('LEGAL_EXE', 'SYSTEM_INFORMATION_MANAGEMENT_ACCESS'),
    ('TERRITORIAL_EXE', 'SYSTEM_INFORMATION_MANAGEMENT_ACCESS'),
    ('CLIENT', 'SYSTEM_PERSONAL_ACCOUNT_ACCESS')
) AS expected(role_name, permission_code)
JOIN public.roles role ON role.name = expected.role_name
JOIN public.permissions permission ON permission.code = expected.permission_code
ON CONFLICT (role_id, permission_id) DO NOTHING;

DELETE FROM public.roles
WHERE name NOT IN ('ADMIN', 'GENERAL_EXE', 'LEGAL_EXE', 'TERRITORIAL_EXE', 'CLIENT');

ALTER TABLE public.roles
    ADD CONSTRAINT ck_roles_allowed_names
    CHECK (name IN ('ADMIN', 'GENERAL_EXE', 'LEGAL_EXE', 'TERRITORIAL_EXE', 'CLIENT'));

DO $$
BEGIN
    IF (SELECT COUNT(*) FROM public.roles) <> 5
        OR (SELECT COUNT(*) FROM public.permissions) <> 3
        OR (SELECT COUNT(*) FROM public.role_permissions) <> 8 THEN
        RAISE EXCEPTION
            'Application role migration did not produce the five roles and their subsystem permissions';
    END IF;
END $$;
