DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS email_requests;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS costs;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id                INTEGER PRIMARY KEY DEFAULT NEXTVAL('global_seq'),
    NAME              TEXT                DEFAULT 'Имя не задано'::TEXT,
    email             TEXT                              NOT NULL,
    PASSWORD          TEXT                              NOT NULL,
    enabled           bool                DEFAULT FALSE NOT NULL,
    banned            bool                DEFAULT FALSE NOT NULL,
    start_period_date TIMESTAMP           DEFAULT NOW() NOT NULL,
    new_notify        bool                DEFAULT FALSE NOT NULL
);

CREATE UNIQUE INDEX users_email_uindex ON users (email);
CREATE UNIQUE INDEX users_id_uindex ON users (id);

CREATE TABLE costs
(
    id               INTEGER PRIMARY KEY DEFAULT NEXTVAL('global_seq'),
    type             TEXT,
    price            INTEGER                           NOT NULL,
    description      TEXT,
    date             DATE                DEFAULT NOW() NOT NULL,
    user_id          INTEGER                           NOT NULL
        CONSTRAINT costs_user_id_fk
            REFERENCES users
            ON DELETE CASCADE,
    date_of_creation TIMESTAMP           DEFAULT NOW()
);

CREATE UNIQUE INDEX costs_id_uindex ON costs (id);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR NOT NULL,
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE notes
(
    id        INTEGER                    NOT NULL
        CONSTRAINT note_pkey
            PRIMARY KEY,
    type      TEXT,
    date_time TIMESTAMP(6) DEFAULT NOW() NOT NULL,
    email     VARCHAR(255)               NOT NULL,
    read      BOOLEAN      DEFAULT FALSE NOT NULL,
    text      VARCHAR(255),
    title     VARCHAR(255),
    user_id   INTEGER
        CONSTRAINT note_user_id_fk
            REFERENCES users on delete cascade
);

CREATE TABLE email_requests
(
    id        INTEGER                    NOT NULL
        CONSTRAINT email_requests_pkey
            PRIMARY KEY,
    code      VARCHAR(255)               NOT NULL,
    date_time TIMESTAMP(6) DEFAULT NOW() NOT NULL,
    type      TEXT                       NOT NULL,
    user_id   INTEGER                    NOT NULL
        CONSTRAINT email_requests_user_id_fk
            REFERENCES users on delete cascade,
    used      BOOLEAN      DEFAULT FALSE NOT NULL

);

create table friends
(
    id           integer not null
        primary key,
    color        varchar(255) default '#ffc107'::character varying,
    friend_email varchar(255),
    friend_id    integer not null,
    user_email   varchar(255),
    user_id      integer
        constraint friend_user_id_fk
            references users
            on delete cascade
);




