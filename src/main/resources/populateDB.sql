DELETE FROM users;
INSERT INTO users(id, name, email, password, start_period_date)
VALUES (100000, 'User', 'l2@og.in', 'password2', '2022-04-03 19:53:02.946299'),
       (100001, 'Admin', 'l@og.in', 'password', '2022-04-10 12:00:00.000000'),
       (100002, 'SuperUser', 'l3@og.in', 'password3', '2022-04-10 12:00:00.000000');

DELETE FROM costs;
INSERT INTO costs(id, type, price, description, date, user_id)
VALUES (100003, 'DINNER', 250, '', '2022-02-01', 100000),
       (100004, 'CAR', 1800, 'Антикор', '2022-01-25', 100000),
       (100005, 'GAS', 3000, '', '2022-01-30', 100000),
       (100006, 'OTHER', 4200, 'Озон', '2022-02-03', 100000),
       (100007, 'FOOD', 4000, '', '2022-02-12', 100000),
       (100008, 'DINNER', 280, '', '2022-02-01', 100001),
       (100009, 'TRANSIT', 500, '', '2022-02-11', 100001),
       (100010, 'ENTERTAINMENT', 3600, '', '2022-02-18', 100001),
       (100011, 'OTHER', 4100, 'Озон', '2022-02-04', 100002),
       (100012, 'FOOD', 1564, '', '2022-02-11', 100002),
       (100013, 'DINNER', 320, '', '2022-02-01', 100002),
       (100014, 'TRANSIT', 1500, 'Метро', '2022-02-11', 100002);

DELETE FROM user_roles;
INSERT INTO user_roles(user_id, role)
VALUES (100000, 'USER'),
       (100001, 'USER'),
       (100002, 'USER'),
       (100001, 'SUPERUSER'),
       (100002, 'ADMIN')