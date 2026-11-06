DROP TABLE public.auth_identities;
DROP TABLE public.users;

ALTER TABLE coopera.organizations RENAME TO cooperation_organizations;
ALTER INDEX coopera.organizations_pkey RENAME TO cooperation_organizations_pkey;
ALTER SEQUENCE coopera.organizations_id_seq RENAME TO cooperation_organizations_id_seq;
ALTER TABLE coopera.cooperation_organizations SET SCHEMA public;

ALTER TABLE coopera.roles SET SCHEMA public;
ALTER TABLE coopera.permissions SET SCHEMA public;
ALTER TABLE coopera.role_permissions SET SCHEMA public;
ALTER TABLE coopera.users SET SCHEMA public;
ALTER TABLE coopera.refresh_tokens SET SCHEMA public;
ALTER TABLE coopera.projects SET SCHEMA public;
ALTER TABLE coopera.project_attachments SET SCHEMA public;

DROP SCHEMA coopera;
