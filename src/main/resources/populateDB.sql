DELETE FROM users;
INSERT INTO users(id, name, email, password, start_period_date)
VALUES (1, 'Кот', 'l2@og.in', 'password2', '2022-04-03 19:53:02.946299'),
       (2, 'Александр', 'l@og.in', 'password', '2022-04-10 12:00:00.000000');

DELETE FROM costs;
INSERT INTO costs(type, price, description, date, user_id)
VALUES ('DINNER', 250, '', '2022-02-01', '1'),
       ('CAR', 1800, 'Антикор', '2022-01-25', '1'),
       ('GAS', 3000, '', '2022-01-30', '1'),
       ('DINNER', 280, '', '2022-02-01', '2'),
       ('FOOD', 4000, '', '2022-02-12', '1'),
       ('TRANSIT', 500, '', '2022-02-11', '2'),
       ('ENTERTAINMENT', 3600, '', '2022-02-18', '2'),
       ('OTHER', 4200, 'Озон', '2022-02-03', '1');
