package ru.tbcarus.spendingsb.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.tbcarus.spendingsb.config.MailConfig;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailContext;
import ru.tbcarus.spendingsb.model.EmailRequestType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(servletRequestAttributes).getRequest();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder
                .append(request.getScheme()).append("://")
                .append(request.getServerName())
                .append(request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort())
                .append(request.getContextPath())
                .append("/register/")
                .append(emailAction.getType().name())
                .append("?email=").append(emailAction.getUser().getEmail())
                .append("&code=").append(emailAction.getCode());
        String link = urlBuilder.toString();

        map.put("link", link);
        email.setContext(map);

        return email;
    }
}
