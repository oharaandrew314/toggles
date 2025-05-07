CREATE TABLE tenants(
    tenant_id CHAR(8) PRIMARY KEY NOT NULL,
    created_on TIMESTAMP NOT NULL
);

CREATE TABLE users(
    tenant_id CHAR(8) NOT NULL REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    unique_id CHAR(32) NOT NULL UNIQUE,
    email_address TEXT NOT NULL,
    created_on TIMESTAMP NOT NULL,
    role VARCHAR(16) NOT NULL,

    PRIMARY KEY (tenant_id, unique_id)
);

CREATE INDEX user_emails ON users(email_address);

CREATE TABLE projects(
    tenant_id CHAR(8) NOT NULL REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    project_name VARCHAR(32) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP NOT NULL,
    environments TEXT NOT NULL,

    PRIMARY KEY (tenant_id, project_name)
);

CREATE TABLE toggles(
    tenant_id CHAR(8) NOT NULL,
    project_name VARCHAR(32) NOT NULL,
    toggle_name VARCHAR(32) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP NOT NULL,
    variations TEXT NOT NULL,
    default_variation VARCHAR(32) NOT NULL,

    PRIMARY KEY (tenant_id, project_name, toggle_name),
    FOREIGN KEY (tenant_id, project_name) REFERENCES projects(tenant_id, project_name) ON DELETE CASCADE
);

CREATE TABLE toggle_environments(
    tenant_id CHAR(8) NOT NULL,
    project_name VARCHAR(32) NOT NULL,
    toggle_name VARCHAR(32) NOT NULL,
    environment VARCHAR(32) NOT NULL,
    weights TEXT NOT NULL,
    overrides TEXT NOT NULL,

    PRIMARY KEY (tenant_id, project_name, toggle_name, environment),
    FOREIGN KEY (tenant_id, project_name, toggle_name) REFERENCES toggles(tenant_id, project_name, toggle_name) ON DELETE CASCADE
);

CREATE TABLE api_keys(
    tenant_id CHAR(8),
    project_name VARCHAR(32) NOT NULL,
    environment_name VARCHAR(32) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    token_sha256_hex CHAR(64) NOT NULL,

    PRIMARY KEY (tenant_id, project_name, environment_name),
    FOREIGN KEY (tenant_id, project_name) REFERENCES projects(tenant_id, project_name) ON DELETE CASCADE
);

CREATE INDEX api_key_lookup ON api_keys(token_sha256_hex);