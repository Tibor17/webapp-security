-- Database: jaaswf8

DROP DATABASE IF EXISTS jaaswf8;

CREATE DATABASE jaaswf8
    WITH
    OWNER = iam
    ENCODING = 'UTF8'
--     LC_COLLATE = 'C.UTF8'
--     LC_CTYPE = 'C.UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = 25;

--- already done by docker-compose.yml
--- CREATE USER iam WITH PASSWORD 'admin123!';

GRANT ALL PRIVILEGES ON DATABASE jaaswf8 TO iam;

\connect jaaswf8;
