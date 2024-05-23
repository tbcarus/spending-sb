package ru.tbcarus.spendingsb.controller.emailAction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailRequestType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.EmailActionService;
import ru.tbcarus.spendingsb.service.UserService;

@Slf4j
public class AbstractEmailActionController {

    @Autowired
    EmailActionService emailActionService;
    @Autowired
    UserService userService;

    protected EmailAction registerConfirm(String email, String code) {
        log.info("Registration confirm request for {} with code {}", email, code);
        return emailActionService.registerConfirm(email, code, EmailRequestType.ACTIVATE);
    }

    protected EmailAction passwordResetRequest(String email) {
        log.info("Request reset password for user {}", email);
        return emailActionService.passwordResetRequest(email);
    }

    protected EmailAction passwordResetGet(String email, String code) {
        log.info("Entrance with reset password link for {} with code {}", email, code);
        return emailActionService.passwordResetGet(email, code, EmailRequestType.RESET_PASSWORD);
    }

    protected User resetPassword(String email, String code, String password, String passwordReply) {
        log.info("Reset password for user {}", email);
        return userService.resetPassword(email, code, password, passwordReply);
    }

    protected EmailAction resendRequest(String email) {
        log.info("update link for registration or password recovery for user {}", email);
        return emailActionService.resendActivationRequest(email);
    }
}
