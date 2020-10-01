DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

DROP TYPE IF EXISTS user_flag;
DROP TYPE IF EXISTS group_flag;

DROP SEQUENCE IF EXISTS user_seq;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE TYPE group_flag AS ENUM ('registering', 'current', 'finished');

CREATE SEQUENCE user_seq START 100000;

CREATE TABLE cities
(
	id    INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
	value TEXT NOT NULL
);

CREATE TABLE projects
(
	id          INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
	description TEXT NOT NULL,
	name        TEXT NOT NULL,
	FOREIGN KEY (name) REFERENCES groups (name)
);

CREATE TABLE groups
(
	id   INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
	name TEXT       NOT NULL,
	flag group_flag NOT NULL,
	CONSTRAINT flag_name_idx UNIQUE (name, flag)
);

CREATE TABLE users
(
	id         INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
	full_name  TEXT      NOT NULL,
	email      TEXT      NOT NULL,
	flag       user_flag NOT NULL,
	group_name TEXT      NOT NULL,
	city_name  TEXT      NOT NULL,
	FOREIGN KEY (city_name) REFERENCES cities (value),
	FOREIGN KEY (group_name) REFERENCES groups (name),
	CONSTRAINT city_email_idx UNIQUE (email, city_name),
	CONSTRAINT email_idx UNIQUE (email)
);