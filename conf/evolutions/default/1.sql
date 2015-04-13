# --- !Ups

create table users (
  "id" BIGINT NOT NULL PRIMARY KEY,
  "login" VARCHAR NOT NULL,
  "password_hash" VARCHAR NOT NULL
)

# --- !Downs

drop table users

