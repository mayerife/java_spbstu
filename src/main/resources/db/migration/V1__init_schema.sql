CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE task (
                      task_id SERIAL PRIMARY KEY,
                      task_text VARCHAR(1000) NOT NULL,
                      due_date TIMESTAMP,
                      creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      complete BOOLEAN NOT NULL DEFAULT FALSE,
                      user_id BIGINT NOT NULL REFERENCES users(user_id),
                      deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE notification (
                              notification_id SERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL REFERENCES users(user_id),
                              message TEXT NOT NULL,
                              creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              read BOOLEAN NOT NULL DEFAULT FALSE,
                              deleted BOOLEAN NOT NULL DEFAULT FALSE
);