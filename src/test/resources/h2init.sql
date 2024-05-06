DROP TABLE IF EXISTS project_users;
DROP TABLE IF EXISTS task_users;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS tasks;

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
    ('test', 'test@mail.dk', 'test'),
    ('test2', 'test2@mail.dk', 'test2'),
    ('mads', 'mafj0001@stud.kea.dk', 'mads'),
    ('yusef', 'yuay0001@stud.kea.dk', 'yusef'),
    ('mohamed', 'moom0001@stud.kea.dk', 'mohamed'),
    ('sina', 'sija0001@stud.kea.dk', 'sina');

INSERT INTO projects (name, description) VALUES
    ('test 1', 'test 1'),
    ('test 2', 'test 2'),
    ('test 3', 'test 3');

INSERT INTO project_users (user_id, project_id, is_admin) VALUES
    (1, 1, 1),
    (2, 2, 0),
    (1, 3, 1);