
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255),
    role VARCHAR(31) NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE auth_identities (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email TEXT,
    password_hash TEXT
);

CREATE TABLE projects (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP
);

CREATE TABLE arrangements (
    id BIGINT PRIMARY KEY,
    manager_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE
);

CREATE TABLE calls_prop (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255),
    body TEXT,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    deadline DATE,
    total_amount DOUBLE PRECISION,
    status VARCHAR(50),
    created_at TIMESTAMP
);

CREATE TABLE investments (
    id BIGINT PRIMARY KEY,
    funder_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    call_prop_id BIGINT NOT NULL REFERENCES calls_prop(id) ON DELETE CASCADE,
    amount DOUBLE PRECISION
);
