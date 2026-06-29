-- V1__init_schema.sql
-- Task Tracker initial schema

CREATE TABLE projects (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tasks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(10)  NOT NULL DEFAULT 'TODO'
                    CHECK (status IN ('TODO', 'DOING', 'DONE')),
    priority    VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM'
                    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    due_date    DATE,
    project_id  BIGINT REFERENCES projects(id) ON DELETE SET NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes to support common query patterns
-- Filter by status (most common filter)
CREATE INDEX idx_tasks_status    ON tasks(status);
-- Sort/filter by due date
CREATE INDEX idx_tasks_due_date  ON tasks(due_date);
-- Join / filter by project
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
