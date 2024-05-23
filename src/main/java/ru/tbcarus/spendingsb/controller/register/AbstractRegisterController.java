package ru.tbcarus.spendingsb.controller.register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;

import java.time.LocalDate;

@Slf4j
public abstract class AbstractRegisterController {

    @Autowired
    private UserService userService;

    public User getRegister() {
        return new User();
    }

    public User createRegister(User user, int startDay) {
        log.info("create new user {}", user);
        user.setStartPeriodDate(LocalDate.now().withDayOfMonth(startDay));
        return userService.create(user);
    }
}