FROM postgres:10-alpine
LABEL maintiner="Tibor Digaňa <tibordigana@apache.org>"
COPY *.sql /docker-entrypoint-initdb.d/
RUN chmod -R 0755 /docker-entrypoint-initdb.d

#         TYPE DATABASE USER ADDRESS      METHOD
RUN echo "host all      all  127.0.0.1/32 md5" >> /var/lib/postgresql/pg_hba.conf && \
    echo "host jaaswf8  iam  all          md5" >> /var/lib/postgresql/pg_hba.conf

RUN echo "listen_addresses = '*'" >> /var/lib/postgresql/postgresql.conf && \
    echo "max_connections = 100" >> /var/lib/postgresql/postgresql.conf
