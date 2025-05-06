CREATE TABLE IF NOT EXISTS users
(
    id                 VARCHAR(36) PRIMARY KEY,
    username           VARCHAR(50) UNIQUE  NOT NULL,
    email              VARCHAR(100) UNIQUE NOT NULL,
    password           VARCHAR(100)        NOT NULL,
    first_name         VARCHAR(100)        NOT NULL,
    last_name          VARCHAR(100)        NOT NULL,
    birth_date         DATE                NOT NULL,
    verified           BOOLEAN             NOT NULL DEFAULT FALSE,
    enabled            BOOLEAN             NOT NULL DEFAULT TRUE,
    created_by         VARCHAR(100)        NOT NULL,
    created_date       TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by   VARCHAR(100)        NOT NULL,
    last_modified_date TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles
(
    id   VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tokens
(
    id          VARCHAR(36) PRIMARY KEY,
    user_id     VARCHAR(36) NOT NULL,
    value       TEXT UNIQUE NOT NULL,
    expiry_date TIMESTAMP   NOT NULL,
    token_type  VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS verification_tokens
(
    id                 VARCHAR(36) PRIMARY KEY,
    user_id            VARCHAR(36)                           NOT NULL,
    token              VARCHAR(10)                           NOT NULL UNIQUE,
    expiry_date        TIMESTAMPTZ                           NOT NULL,
    used               BOOLEAN     DEFAULT FALSE,
    created_by         VARCHAR(100)                          NOT NULL,
    created_date       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_by   VARCHAR(100)                          NOT NULL,
    last_modified_date TIMESTAMPTZ                           NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Crear roles por defecto si no existen
INSERT INTO roles (id, name)
VALUES ('1', 'ADMIN'),
       ('2', 'USER')
ON CONFLICT (name) DO NOTHING;

-- Crear usuario admin por defecto (contraseña: admin123)
INSERT INTO users (id, username, email, password, first_name, last_name, birth_date, enabled, created_by,
                   last_modified_by, last_modified_date)
VALUES ('1', 'admin', 'admin@example.com', '$2a$10$qPjBQiKR0eB0X2e7G.ygjO7HhiIRGLGzZOq7o6QQ7lJH.Qn9QnNRO',
        'Admin', 'System', '2000-01-01', true, 'SELF-REGISTRATION', 'SELF-REGISTRATION', CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Asignar rol de admin al usuario admin
INSERT INTO user_roles (user_id, role_id)
VALUES ('1', '1')
ON CONFLICT (user_id, role_id) DO NOTHING;