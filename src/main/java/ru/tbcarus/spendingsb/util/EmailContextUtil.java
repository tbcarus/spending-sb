package ru.tbcarus.spendingsb.util;

import ru.tbcarus.spendingsb.config.MailConfig;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailContext;
import ru.tbcarus.spendingsb.model.EmailRequestType;

import java.util.HashMap;
import java.util.Map;

public class EmailContextUtil {

    public static final String CONFIRMATION_EMAIL_TEMPLATE = "email/confirmationTemplate";
    public static final String PASSWORD_RESET_EMAIL_TEMPLATE = "email/passwordResetTemplate";

    public static EmailContext getEmailContext(EmailAction emailAction) {
        EmailContext email = new EmailContext();
        email.setFrom(MailConfig.MAIL_FROM);
        email.setTo(emailAction.getUser().getEmail());
        email.setSubject(emailAction.getType().getTitle());
        email.setFromDisplayName("FROM_DISPLAY_NAME");
        email.setDisplayName("DISPLAY_NAME");

        if (EmailRequestType.ACTIVATE.equals(emailAction.getType())) {
            email.setTemplate(CONFIRMATION_EMAIL_TEMPLATE);
        }
        if (EmailRequestType.RESET_PASSWORD.equals(emailAction.getType())) {
            email.setTemplate(PASSWORD_RESET_EMAIL_TEMPLATE);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", emailAction.getUser().getName());
        map.put("emailAction", emailAction);
        map.put("link", "http://localhost:8080/spending/register/"
                + emailAction.getType().name()
                + "?email="
                + emailAction.getUser().getEmail()
                + "&code="
                + emailAction.getCode());
        email.setContext(map);

        return email;
    }
}
