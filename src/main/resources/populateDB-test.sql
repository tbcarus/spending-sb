DELETE FROM users;
DELETE FROM costs;
DELETE FROM user_roles;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users(name, email, password, start_period_date)
VALUES ('User', 'l2@og.in', 'password2', '2022-04-03'), --100000
       ('Admin', 'l@og.in', 'password', '2022-04-10'),  --100001
       ('SuperUser', 'l1@og.in', 'password3', '2023-04-10'); --100002


INSERT INTO costs(type, price, description, date, user_id)
VALUES ('DINNER', 250, '', '2023-02-01', 100000),                               --100003
       ('CAR', 1800, 'Антикор', '2023-01-25', 100000),                          --100004
       ('GAS', 3000, '', '2023-01-30', 100000),                                 --100005
       ('OTHER', 4200, 'Озон', '2023-02-03', 100000),                           --100006
       ('FOOD', 4000, '', '2023-02-12', 100000),                                --100007
       ('DINNER', 280, '', '2023-02-01', 100001),                              --100008
       ('TRANSIT', 500, '', '2023-02-11', 100001),                              --100009
       ('ENTERTAINMENT', 3600, '', '2023-02-18', 100001),                       --100010
       ('OTHER', 4100, 'Озон', '2023-02-04', 100002),                           --100011
       ('FOOD', 1564, '', '2023-02-11', 100002),                                --100012
       ('DINNER', 320, '', '2023-02-01', 100002),                               --100013
       ('TRANSIT', 1500, 'Метро', '2023-02-11', 100002);                       --100014


INSERT INTO user_roles(user_id, role)
VALUES (100000, 'USER'),
       (100001, 'USER'),
       (100002, 'USER'),
       (100001, 'ADMIN'),
       (100002, 'SUPERUSER')