# Spendings 
Это приложение было написано в учебных целях. Использованы Spring Boot, Spring Security, Spring Data JPA, Hibernate, REST, JavaScript, Bootstrap, PostgreSQL, JUnit, Maven, SLF4J, Bean Validation

Идея написания пришла от таблицы excel, в которую мы заносили свои ежедневные траты с разбиением 
по месяцам. Эта таблица хранилась на домашнем компьютере, что доставляло неудобства, и хотелось, 
чтобы она была всегда под рукой. Оттуда же достались и захаркоженные типы трат 
(пока без возможности создания своего набора типов трат). Так же предыдущая упрощённая версия 
приложения позволяла экспортировать данные в файл excel в том же формате и внешнем виде, 
как это было в оригинальном файле.

**Текущие возможности приложения:**\
1. Все данные хранятся в локальной БД Postgres;
2. Все пользователи разделены по ролям: ADMIN, USER, SUPERUSER. Все новые зарегистрированные пользователи имеют роли USER и SUPERUSER;
3. ADMIN может создавать, изменять и удалять пользователей;
4. Регистрация пользователя с подтверждением регистрации и возможностью восстановить пароль по e-mail. Пароли хранятся в базе в зашифрованном виде;
5. Вывод трат осуществляется помесячно. Стартовый день месяца можно задать. Траты в таблице упорядочены по дате;
6. Траты выводятся в главную таблицу в столбцы по типам. Для некоторых типов отображается описание траты;
7. В верхней строке каждой колонки отображается сумма трат за месяц. Также отображается сумма всех трат за текущий период;
8. Можно перейти в колонку каждого типа трат, где будет отображено больше информации;
9. Траты заносятся либо на отдельной странице с заполнением необходимых полей, либо прямо в таблице в специально предусмотренные для этого две верхние строки. Нельзя занести "0", дробное или строковое значение;
10. Можно удалять и редактировать траты;
11. Пользователи могут объединяться в группы. Для этого пользователи могут отправлять приглашения со следующими ограничениями:
    - Отправлять приглашения может только SUPERUSER;
    - После принятия приглашения роль SUPERUSER слетает и устанавливается стартовый день месяца приглашающего;
    - Нельзя отравить приглашение пользователю, состоящему в другой группе;
    - Нельзя иметь суммарное количество друзей и отправленных приглашений более 5 (на текущий момент);
12. При приёме приглашения вступить в группу все отправленные и полученные приглашения удаляются;
13. Пользователи могут просматривать и удалять свои отправленные и полученные приглашения;
14. В группе можно:
    - Полный доступ к тратам членов группы, но принадлежность траты изменить нельзя;
    - Устанавливать цвет траты для каждого пользователя группы;
    - Удалиться из группы. При этом SUPERUSER восстанавливается;
    - Только для SUPERUSER:
      - Отправлять приглашения для вступления в группу;
      - Удалять пользователей;
      - Менять роли пользователей (убирать или восстанавливать SUPERUSER);
      - Удалить группу – у всех пользователей восстановится SUPERUSER, все пользователи будут видеть только свои траты;
      - Удалиться из группы. При этом, если в группе не остаётся SUPERUSER, то эта роль восстанавливается у первого;
      - Установить в группе единое число начала месяца;