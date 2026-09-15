-- V2__insert_test_data.sql

INSERT INTO users (id, username, role, created_at) VALUES
(1, 'jdoe', 'MANAGER', '2026-01-10 09:00:00'),
(2, 'mgarcia', 'MANAGER', '2026-01-12 10:30:00'),
(3, 'alopez', 'FUNDER', '2026-01-15 14:15:00'),
(4, 'csmith', 'FUNDER', '2026-01-18 11:45:00');

INSERT INTO auth_identities (id, user_id, email, password_hash) VALUES
(1, 1, 'jdoe@example.com', '$2a$12$eImiTXuWVxfM37uY4JANjO8GzK2P.x13h8G9'),
(2, 2, 'mgarcia@example.com', '$2a$12$eImiTXuWVxfM37uY4JANjO8GzK2P.x13h8G10'),
(3, 3, 'alopez@example.com', '$2a$12$eImiTXuWVxfM37uY4JANjO8GzK2P.x13h8G11'),
(4, 4, 'csmith@example.com', '$2a$12$eImiTXuWVxfM37uY4JANjO8GzK2P.x13h8G12');

INSERT INTO projects (id, name, description, created_at) VALUES
(101, 'Planta Solar Norte', 'Proyecto de energía renovable en zona industrial', '2026-02-01 08:00:00'),
(102, 'App Logística Verde', 'Plataforma para optimizar rutas de carga pesada', '2026-02-05 12:00:00');

INSERT INTO arrangements (id, manager_id, project_id) VALUES
(1, 2, 101),
(2, 2, 102);

INSERT INTO calls_prop (id, title, body, project_id, deadline, total_amount, status, created_at) VALUES
(501, 'Convocatoria Fase 1 - Solar', 'Búsqueda de capital semilla para paneles', 101, '2026-06-30', 150000.00, 'ACTIVE', '2026-02-10 10:00:00'),
(502, 'Convocatoria Software Beta', 'Financiamiento para desarrollo de prototipo', 102, '2026-08-15', 45000.00, 'OPEN', '2026-02-12 15:30:00');

INSERT INTO investments (id, funder_id, call_prop_id, amount) VALUES
(1, 3, 501, 50000.00),
(2, 4, 501, 10000.00),
(3, 3, 502, 20000.00);