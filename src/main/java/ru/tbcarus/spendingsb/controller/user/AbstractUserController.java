package ru.tbcarus.spendingsb.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.GroupService;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public abstract class AbstractUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    public List<User> getALL() {
        log.info("get all users");
        return userService.getAll();
    }

    public User get(int id) {
        log.info("get user by id {}", id);
        return userService.getById(id);
    }

    public User getByEmail(String email) {
        log.info("get user by email {}", email);
        return userService.getByEmail(email);
    }

    public User create(User user) {
        log.info("create new user {}", user);
        return userService.create(user);
    }

    public User create(User user, int startDay) {
        log.info("create new user {}", user);
        user.setStartPeriodDate(LocalDate.now().withDayOfMonth(startDay));
        return userService.create(user);
    }

    public User update(User user, int id) {
        log.info("update user {}", user);
        ValidationUtil.assureIdConsistent(user, id);
        return userService.update(user, id);
    }

    public void changeStartDate(User user, LocalDate startDate) {
        log.info("change start date");
        userService.changeStartDate(user, startDate);
    }

    public void delete(int id) {
        User user = userService.getByIdWithFriends(id);
        if (user.isInGroup()) {
            groupService.deleteFromGroupSelf(user);
        }
        userService.delete(id);
    }

    protected User enable(int userId, boolean enable) {
        return userService.enable(userId, enable);
    }

    protected User ban(int userId, boolean ban) {
        return userService.ban(userId, ban);
    }
}
