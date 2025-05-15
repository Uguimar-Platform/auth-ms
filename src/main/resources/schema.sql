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

-- Roles table (ADMIN, STUDENT, INSTRUCTOR, SUPERADMIN)
CREATE TABLE IF NOT EXISTS roles
(
    id          VARCHAR(36) PRIMARY KEY,
    name        VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

-- User-Role relationship (many-to-many)
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- System modules/sections
CREATE TABLE IF NOT EXISTS modules
(
    id                 VARCHAR(36) PRIMARY KEY,
    name               VARCHAR(50) UNIQUE                    NOT NULL,
    description        VARCHAR(200),
    created_by         VARCHAR(100)                          NOT NULL,
    created_date       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_by   VARCHAR(100)                          NOT NULL,
    last_modified_date TIMESTAMPTZ                           NOT NULL
);

-- Specific permissions/actions
CREATE TABLE IF NOT EXISTS permissions
(
    id                 VARCHAR(36) PRIMARY KEY,
    name               VARCHAR(50) UNIQUE                    NOT NULL,
    description        VARCHAR(200),
    created_by         VARCHAR(100)                          NOT NULL,
    created_date       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_by   VARCHAR(100)                          NOT NULL,
    last_modified_date TIMESTAMPTZ                           NOT NULL
);

-- Role-Permission relationship (many-to-many)
CREATE TABLE IF NOT EXISTS role_permissions
(
    role_id       VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);

-- Module-Permission relationship (many-to-many)
CREATE TABLE IF NOT EXISTS module_permissions
(
    module_id     VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (module_id, permission_id),
    FOREIGN KEY (module_id) REFERENCES modules (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
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
    used               BOOLEAN     DEFAULT FALSE             NOT NULL,
    created_by         VARCHAR(100)                          NOT NULL,
    created_date       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_by   VARCHAR(100)                          NOT NULL,
    last_modified_date TIMESTAMPTZ                           NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_reset_tokens
(
    id                 VARCHAR(36) PRIMARY KEY,
    user_id            VARCHAR(36)                           NOT NULL,
    token              VARCHAR(10)                           NOT NULL UNIQUE,
    expiry_date        TIMESTAMPTZ                           NOT NULL,
    used               BOOLEAN     DEFAULT FALSE             NOT NULL,
    created_by         VARCHAR(100)                          NOT NULL,
    created_date       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_modified_by   VARCHAR(100)                          NOT NULL,
    last_modified_date TIMESTAMPTZ                           NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Insert default roles
INSERT INTO roles (id, name, description)
VALUES ('1', 'SUPERADMIN', 'Super administrator with all privileges'),
       ('2', 'ADMIN', 'Administrator with management privileges'),
       ('3', 'INSTRUCTOR', 'Course instructor with content management privileges'),
       ('4', 'STUDENT', 'Regular student user')
ON CONFLICT (name) DO NOTHING;

-- Insert default modules
INSERT INTO modules (id, name, description, created_by, created_date, last_modified_by, last_modified_date)
VALUES ('1', 'USER_MANAGEMENT', 'User account management module', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM',
        CURRENT_TIMESTAMP),
       ('2', 'COURSE_MANAGEMENT', 'Course creation and management module', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM',
        CURRENT_TIMESTAMP),
       ('3', 'CONTENT_MANAGEMENT', 'Course content creation and management', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM',
        CURRENT_TIMESTAMP),
       ('4', 'ENROLLMENT', 'Course enrollment and progress tracking', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM',
        CURRENT_TIMESTAMP),
       ('5', 'REPORTING', 'Analytics and reporting module', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- Insert default permissions
INSERT INTO permissions (id, name, description, created_by, created_date, last_modified_by, last_modified_date)
VALUES ('1', 'CREATE_USER', 'Create new user accounts', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('2', 'READ_USER', 'View user account details', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('3', 'UPDATE_USER', 'Modify user account details', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('4', 'DELETE_USER', 'Delete user accounts', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),

       ('5', 'CREATE_COURSE', 'Create new courses', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('6', 'READ_COURSE', 'View course details', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('7', 'UPDATE_COURSE', 'Modify course details', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('8', 'DELETE_COURSE', 'Delete courses', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),

       ('9', 'CREATE_CONTENT', 'Create course content', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('10', 'READ_CONTENT', 'View course content', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('11', 'UPDATE_CONTENT', 'Modify course content', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('12', 'DELETE_CONTENT', 'Delete course content', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),

       ('13', 'ENROLL_COURSE', 'Enroll in courses', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('14', 'VIEW_REPORTS', 'View system reports', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP),
       ('15', 'MANAGE_SYSTEM', 'Manage system settings', 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- Associate permissions with modules
-- User management module permissions
INSERT INTO module_permissions (module_id, permission_id)
VALUES ('1', '1'),
       ('1', '2'),
       ('1', '3'),
       ('1', '4')
ON CONFLICT (module_id, permission_id) DO NOTHING;

-- Course management module permissions
INSERT INTO module_permissions (module_id, permission_id)
VALUES ('2', '5'),
       ('2', '6'),
       ('2', '7'),
       ('2', '8')
ON CONFLICT (module_id, permission_id) DO NOTHING;

-- Content management module permissions
INSERT INTO module_permissions (module_id, permission_id)
VALUES ('3', '9'),
       ('3', '10'),
       ('3', '11'),
       ('3', '12')
ON CONFLICT (module_id, permission_id) DO NOTHING;

-- Enrollment module permissions
INSERT INTO module_permissions (module_id, permission_id)
VALUES ('4', '13'),
       ('4', '10')
ON CONFLICT (module_id, permission_id) DO NOTHING;

-- Reporting module permissions
INSERT INTO module_permissions (module_id, permission_id)
VALUES ('5', '14')
ON CONFLICT (module_id, permission_id) DO NOTHING;

-- Associate permissions with roles
-- SUPERADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT '1', id
FROM permissions
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- ADMIN gets most permissions except some system-level ones
INSERT INTO role_permissions (role_id, permission_id)
SELECT '2', id
FROM permissions
WHERE id NOT IN ('15')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- INSTRUCTOR gets content and course related permissions
INSERT INTO role_permissions (role_id, permission_id)
VALUES ('3', '5'),
       ('3', '6'),
       ('3', '7'),  -- Course management (except delete)
       ('3', '9'),
       ('3', '10'),
       ('3', '11'),
       ('3', '12'), -- Content management
       ('3', '2'),
       ('3', '14')  -- View users and reports
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- STUDENT gets basic permissions
INSERT INTO role_permissions (role_id, permission_id)
VALUES ('4', '6'),
       ('4', '10'),
       ('4', '13') -- View courses, content, and enroll
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Create admin user (password: admin123)
INSERT INTO users (id, username, email, password, first_name, last_name, birth_date, enabled, verified, created_by,
                   last_modified_by, last_modified_date)
VALUES ('1', 'admin', 'admin@example.com', '$2a$10$qPjBQiKR0eB0X2e7G.ygjO7HhiIRGLGzZOq7o6QQ7lJH.Qn9QnNRO',
        'Admin', 'System', '2000-01-01', true, true, 'SYSTEM', 'SYSTEM', CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Assign SUPERADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
VALUES ('1', '1')
ON CONFLICT (user_id, role_id) DO NOTHING;