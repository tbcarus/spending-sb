package ru.tbcarus.spendingsb.controller.emailAction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.service.EmailActionService;

@Slf4j
public class AbstractEmailActionController {

    @Autowired
    EmailActionService emailActionService;

//    public EmailAction createActivate(String email) {
//        log.info("User {} request for activate profile", email);
//        EmailAction emailAction = new EmailAction(EmailRequestType.ACTIVATE);
//        return emailActionService.create(email, emailAction);
//    }

//    public EmailAction createResetPassword(String email) {
//        log.info("User {} request for reset password", email);
//        EmailAction emailAction = new EmailAction(EmailRequestType.RESET_PASSWORD);
//        return emailActionService.create(email, emailAction);
//    }
}
