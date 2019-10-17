\connect jaaswf8;

begin;

CREATE TABLE IF NOT EXISTS "public"."userroles"
(
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    role character varying(32) COLLATE pg_catalog."default" NOT NULL
) WITH (
    OIDS = FALSE
) TABLESPACE pg_default;

ALTER TABLE "public"."userroles"
    OWNER to iam;

CREATE TABLE IF NOT EXISTS "public"."users"
(
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    passwd character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (username)
) WITH (
    OIDS = FALSE
) TABLESPACE pg_default;

CREATE UNIQUE INDEX uk_username ON "public"."users"(username);

ALTER TABLE "public"."users"
    OWNER to iam;

commit;
