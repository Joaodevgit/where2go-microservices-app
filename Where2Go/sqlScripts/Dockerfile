FROM postgres:latest

ENV POSTGRES_USER where2go
ENV POSTGRES_PASSWORD where2go@123
ENV PGDATA /data/postgres
ENV POSTGRES_PORT=5433

EXPOSE $POSTGRES_PORT

COPY init_database.sql /docker-entrypoint-initdb.d/
