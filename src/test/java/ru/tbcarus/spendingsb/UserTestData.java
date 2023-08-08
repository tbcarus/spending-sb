package ru.tbcarus.spendingsb;

import ru.tbcarus.spendingsb.model.Role;
import ru.tbcarus.spendingsb.model.User;

import java.time.LocalDate;
import java.util.List;

import static ru.tbcarus.spendingsb.model.AbstractBaseEntity.START_SEQ;

public class UserTestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(User.class,"");

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int SUPER_USER = START_SEQ + 2;
    public static final int NOT_FOUND = 10;

    public static User user = new User(USER_ID, "User", "l2@og.in", "password2", true, LocalDate.parse("2022-04-03"), Role.USER);
    public static User admin = new User(ADMIN_ID, "Admin", "l@og.in", "password", true, LocalDate.parse("2022-04-10"), Role.USER, Role.ADMIN);
    public static User superUser = new User(SUPER_USER, "SuperUser", "l3@og.in", "password3", true, LocalDate.parse("2022-04-10"), Role.USER, Role.SUPERUSER);
    public List<User> userList = List.of(admin, superUser, user);
}
