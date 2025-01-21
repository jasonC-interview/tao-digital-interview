--liquibase formatted sql

--changeset application:1
CREATE TABLE app_user (
     id uuid NOT NULL,
     "password" varchar(255) NULL,
     "role" varchar(255) NULL,
     username varchar(255) NULL,
     CONSTRAINT app_user_pkey PRIMARY KEY (id)
);

CREATE TABLE book (
     id uuid NOT NULL,
     author varchar(255) NULL,
     image varchar(255) NULL,
     title varchar(255) NULL,
     CONSTRAINT book_pkey PRIMARY KEY (id)
);

CREATE TABLE inventory (
      id uuid NOT NULL,
      book_id uuid NULL,
      user_id uuid NULL,
      loan_date timestamp without time zone NULL,
      CONSTRAINT borrowed_book_pkey PRIMARY KEY (id)
);
ALTER TABLE inventory ADD CONSTRAINT book_id_key FOREIGN KEY (book_id) REFERENCES book(id);
ALTER TABLE inventory ADD CONSTRAINT user_id_key FOREIGN KEY (user_id) REFERENCES app_user(id);
CREATE INDEX IF NOT EXISTS idx_username on app_user(username);
