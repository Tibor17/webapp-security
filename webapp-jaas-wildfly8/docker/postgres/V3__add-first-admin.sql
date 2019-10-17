\connect jaaswf8;

BEGIN;

DO $$
    DECLARE
        max int8 := 0;
    BEGIN
        IF (SELECT count(*) FROM users) = max THEN
            INSERT INTO users (username, passwd) VALUES ('myfear', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=');
        END IF;
END $$;

DO $$
    DECLARE
        max int8 := 0;
    BEGIN
        IF (SELECT count(*) FROM userroles) = max THEN
            INSERT INTO userroles (username, role) VALUES ('myfear', 'ADMIN');
        END IF;
END $$;

COMMIT;
