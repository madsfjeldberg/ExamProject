DROP DATABASE IF EXISTS exam_db;

CREATE DATABASE exam_db;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS project_users;
DROP TABLE IF EXISTS task_users;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_project_id INT
);

CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    required_hours INT,
    project_id INT
);

CREATE TABLE project_users (
    user_id INT,
    project_id INT,
    is_admin BOOLEAN,
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE task_users (
    user_id INT,
    task_id INT,
    PRIMARY KEY (user_id, task_id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (username, email, password) VALUES
    ('mads', 'mafj0001@stud.kea.dk', 'mads'),
    ('yusef', 'yuay0001@stud.kea.dk', 'yusef'),
    ('mohamed', 'moom0001@stud.kea.dk', 'mohamed'),
    ('sina', 'sija0001@stud.kea.dk', 'sina');

INSERT INTO projects (name, description, parent_project_id) VALUES
    ('Project 1', 'This is project 1', NULL),
    ('Project 2', 'This is project 2, subproject for project 1', 1),
    ('Project 3', 'This is project 3', NULL),
    ('Project 4', 'This is project 4', NULL);

INSERT INTO tasks (name, description, required_hours, project_id) VALUES
    ('Task 1', 'This is task 1', 10, 1),
    ('Task 2', 'This is task 2', 20, 1),
    ('Task 3', 'This is task 3', 30, 2),
    ('Task 4', 'This is task 4', 40, 3),
    ('Task 5', 'This is task 5', 50, 4);

INSERT INTO project_users (user_id, project_id, is_admin) VALUES
    (1, 1, TRUE),
    (2, 1, FALSE),
    (2, 2, TRUE),
    (3, 3, TRUE),
    (4, 4, TRUE);
