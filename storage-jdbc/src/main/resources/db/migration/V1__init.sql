CREATE TABLE projects(
    project_name VARCHAR(32) PRIMARY KEY NOT NULL,
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP NOT NULL,
    environments TEXT NOT NULL
);

CREATE TABLE toggles(
    project_name VARCHAR(32) NOT NULL REFERENCES projects(project_name) ON DELETE CASCADE,
    toggle_name VARCHAR(32) NOT NULL,
    unique_id CHAR(8) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP NOT NULL,
    variations TEXT NOT NULL,
    default_variation VARCHAR(32) NOT NULL,

    PRIMARY KEY (project_name, toggle_name)
);

CREATE TABLE toggle_environments(
    project_name VARCHAR(32) NOT NULL,
    toggle_name VARCHAR(32) NOT NULL,
    environment VARCHAR(32) NOT NULL,
    weights TEXT NOT NULL,
    overrides TEXT NOT NULL,

    PRIMARY KEY (project_name, toggle_name, environment),
    FOREIGN KEY (project_name, toggle_name) REFERENCES toggles(project_name, toggle_name) ON DELETE CASCADE
);

CREATE TABLE api_keys(
    project_name VARCHAR(32) NOT NULL REFERENCES projects(project_name) ON DELETE CASCADE,
    environment_name VARCHAR(32) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    token_md5_hex CHAR(32) NOT NULL,

    PRIMARY KEY (project_name, environment_name)
);

CREATE INDEX lookup ON api_keys(token_md5_hex);