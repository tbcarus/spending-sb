package ru.tbcarus.spendingsb.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailRequestType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.EmailActionService;
import ru.tbcarus.spendingsb.service.NoteService;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public abstract class AbstractUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private EmailActionService emailActionService;

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
        userService.delete(id);
    }

    public void deleteUserFromGroup(User user, int id) {
        log.info("superuser {} delete user {} from group", user.getId(), id);
        userService.deleteUserFromGroup(user, id);
    }

    public void deleteFromGroupSelf(User user) {
        log.info("user {} deleting from the group by self", user.getId());
        userService.deleteGroupGroupSelf(user);
    }

    protected void sendFriendInvite(User user, String email) {
        userService.sendFriendInvite(user, email);
    }

    protected List<User> getFriends(User user) {
        log.info("get friends list by user {}", user.getId());
        return userService.getFriends(user.getFriendsList());
    }

    protected void addSU(User user, int id) {
        log.info("superuser {} add SU role for user {}", user.getId(), id);
        userService.addSU(user, id);
    }

    protected void removeSU(User user, int id) {
        log.info("superuser {} remove SU role for user {}", user.getId(), id);
        userService.removeSU(user, id);
    }

    protected EmailAction registerConfirm(String email, String code) {
        log.info("Registration confirm request for {} with code {}", email, code);
        return emailActionService.registerConfirm(email, code, EmailRequestType.ACTIVATE);
    }

    protected EmailAction resendRequest(String email) {
        log.info("update link for registration or password recovery for user {}", email);
        return emailActionService.resendActivationRequest(email);
    }

    protected EmailAction passwordResetRequest(String email) {
        log.info("Request reset password for user {}", email);
        return emailActionService.passwordResetRequest(email);
    }

    protected User resetPassword(String email, String code, String password, String passwordReply) {
        log.info("Reset password for user {}", email);
        return userService.resetPassword(email, code, password, passwordReply);
    }

    protected EmailAction passwordResetGet(String email, String code) {
        log.info("Entrance with reset password link for {} with code {}", email, code);
        return emailActionService.passwordResetGet(email, code, EmailRequestType.RESET_PASSWORD);
    }

    protected User enable(int userId, boolean enable) {
        return userService.enable(userId, enable);
    }

    protected User ban(int userId, boolean ban) {
        return userService.ban(userId, ban);
    }
}
