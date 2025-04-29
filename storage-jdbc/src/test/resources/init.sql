CREATE TABLE projects(
    project_name VARCHAR(32) PRIMARY KEY NOT NULL,
    created_on TIMESTAMP NOT NULL
);

CREATE TABLE toggles(
    project_name VARCHAR(32) NOT NULL,
    toggle_name VARCHAR(32) NOT NULL,
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP NOT NULL,
    variations TEXT NOT NULL,
    overrides TEXT NOT NULL,
    default_variation VARCHAR(32) NOT NULL,
    PRIMARY KEY (project_name, toggle_name)
);