CREATE TABLE IF NOT EXISTS roles (
  id BIGSERIAL PRIMARY KEY UNIQUE,
  role_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS template (
  id       BIGSERIAL PRIMARY KEY UNIQUE,
  name VARCHAR(255),
  user_id BIGINT NULL
);

CREATE TABLE IF NOT EXISTS team (
  id       BIGSERIAL PRIMARY KEY UNIQUE,
  name VARCHAR(255),
  is_private BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY UNIQUE,
  password VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL,
  default_template BIGINT,
  default_team BIGINT
);

ALTER TABLE users
  ADD FOREIGN KEY (default_template) REFERENCES template (id);
ALTER TABLE users
  ADD FOREIGN KEY (default_team) REFERENCES team (id);

ALTER TABLE template
  ADD FOREIGN KEY (user_id) REFERENCES users (id);

CREATE TABLE IF NOT EXISTS day (
  id       BIGSERIAL PRIMARY KEY UNIQUE,
  date DATE,
  user_id BIGINT
);
ALTER TABLE day
  ADD FOREIGN KEY (user_id) REFERENCES users (id);

DROP TYPE IF EXISTS day_of_week;
CREATE TYPE day_of_week AS ENUM ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY',
'FRIDAY', 'SATURDAY','SUNDAY');
CREATE TABLE IF NOT EXISTS day_week (
  id       BIGSERIAL PRIMARY KEY UNIQUE,
  day day_of_week,
  template_id BIGINT
);
ALTER TABLE day_week
  ADD FOREIGN KEY (template_id) REFERENCES template (id);

CREATE TABLE IF NOT EXISTS time_interval (
  id       BIGSERIAL PRIMARY KEY UNIQUE,
  start_time TIME,
  end_time TIME,
  day_id BIGINT,
  day_week_id BIGINT
);
ALTER TABLE time_interval
  ADD FOREIGN KEY (day_id) REFERENCES day (id);
ALTER TABLE time_interval
  ADD FOREIGN KEY (day_week_id) REFERENCES day_week (id);

CREATE TABLE IF NOT EXISTS followed_team (
  user_id       BIGINT NOT NULL,
  team_id       BIGINT NOT NULL,
  CONSTRAINT followed_team_pkey PRIMARY KEY (user_id, team_id)
);
ALTER TABLE followed_team
  ADD FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE followed_team
  ADD FOREIGN KEY (team_id) REFERENCES team (id);

CREATE TABLE IF NOT EXISTS user_team (
  user_id       BIGINT NOT NULL,
  team_id       BIGINT NOT NULL,
  CONSTRAINT user_team_pkey PRIMARY KEY (user_id, team_id)
);
ALTER TABLE user_team
  ADD FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE user_team
  ADD FOREIGN KEY (team_id) REFERENCES team (id);

CREATE TABLE IF NOT EXISTS users_roles (
  user_id       BIGINT NOT NULL,
  role_id       BIGINT NOT NULL,
  CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id)
);
ALTER TABLE users_roles
  ADD FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE users_roles
  ADD FOREIGN KEY (role_id) REFERENCES roles (id);

CREATE TABLE IF NOT EXISTS user_template (
  user_id       BIGINT NOT NULL,
  template_id       BIGINT NOT NULL,
  CONSTRAINT user_template_pkey PRIMARY KEY (user_id, template_id)
);
ALTER TABLE user_template
  ADD FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE user_template
  ADD FOREIGN KEY (template_id) REFERENCES template (id);

INSERT INTO users (username, password) VALUES ('admin', 'admin');
INSERT INTO users (username, password) VALUES ('user', 'user');

INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (role_name) VALUES ('ROLE_USER');

INSERT INTO users_roles (role_id, user_id) VALUES ('1', '1');
INSERT INTO users_roles (role_id, user_id) VALUES ('2', '1');
INSERT INTO users_roles (role_id, user_id) VALUES ('2', '2');