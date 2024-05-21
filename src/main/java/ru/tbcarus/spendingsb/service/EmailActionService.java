package ru.tbcarus.spendingsb.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.tbcarus.spendingsb.exception.BadRegistrationRequest;
import ru.tbcarus.spendingsb.model.*;
import ru.tbcarus.spendingsb.repository.JpaEmailActionRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.ConfigUtil;
import ru.tbcarus.spendingsb.util.EmailContextUtil;
import ru.tbcarus.spendingsb.util.UtilsClass;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmailActionService {
    @Autowired
    JpaEmailActionRepository repository;

    @Autowired
    JpaUserRepository userRepository;

    @Autowired
    JavaMailSender emailSender;

    @Autowired
    SpringTemplateEngine templateEngine;

    public EmailAction get(String code) {
        return repository.getByCode(code);
    }

    public EmailAction registerConfirm(String email, String code, EmailRequestType type) {
        EmailAction emailAction = emailActionCheck(email, code, type);
        User user = emailAction.getUser();
        user.setEnabled();
        userRepository.save(user);
        emailAction.setUsed(true);
        return repository.save(emailAction);
    }

    public EmailAction passwordResetGet(String email, String code, EmailRequestType type) {
        EmailAction emailAction = emailActionCheck(email, code, type);
        emailAction.setUsed(true);
        return repository.save(emailAction);
    }

    private EmailAction emailActionCheck(String email, String code, EmailRequestType type) {
        // Регистрация не подтверждается если:
        // - в базе отсутствует запись по запрошенному коду
        // - не совпадает почта
        // - не совпадает тип запроса
        // - запрос уже был used = true
        // - истёк период действия кода
        EmailAction emailAction = repository.getByCode(code);
        if (emailAction == null
                || !email.equals(emailAction.getUser().getEmail())
                || !type.equals(emailAction.getType())
                || emailAction.isUsed()) {
            throw new BadRegistrationRequest(ErrorType.NOT_FOUND);
        }
        if (!emailAction.isActive()) {
            throw new BadRegistrationRequest(ErrorType.PERIOD_EXPIRED);
        }
        return emailAction;
    }

    public EmailAction resendActivationRequest(String email) {
        // Повторный запрос активации пользователя
        User user = userRepository.getByEmail(email);
        return resentRequest(user, null, EmailRequestType.ACTIVATE);
    }

    public EmailAction passwordResetRequest(String email) {
        // запрос на смену пароля
        User user = userRepository.findByEmail(email).orElseThrow();
        return resentRequest(user, null, EmailRequestType.RESET_PASSWORD);
    }

    private EmailAction resentRequest(User user, EmailAction old, EmailRequestType type) {
        if (!checkRepeatRequest(user, type)) {
            throw new BadRegistrationRequest(ErrorType.TOO_MUCH_REPEAT_REQUESTS);
        }
        EmailAction emailAction = create(user, type);
        try {
            sendEmail(emailAction);
        } catch (MessagingException e) {
            log.error("Nothing was sent {}", e.getCause().getMessage());
        }
        return emailAction;
    }

    private boolean checkRepeatRequest(User user, EmailRequestType type) {
        // Проверка на многократные запросы. Исключение вызывается при:
        // - запросы на смену пароля. Есть неиспользованный запрос на восстановление пароля.
        // - переход по старой ссылке восстановления пароля, но есть новая
        // - запросы на активацию. Новый запрос при переходе по старой ссылке с истекшим сроком (или по любой ссылке), но есть новая неиспользованная ссылка. Если пользователь уже активирован
        List<EmailAction> list = repository.findAllByUserIdAndDateTimeBetweenAndTypeOrderByDateTimeDesc(user.getId(),
                LocalDateTime.now().minusDays(ConfigUtil.DEFAULT_EXPIRED_DAYS),
                LocalDateTime.now(),
                type);

        return list.size() < ConfigUtil.ACTIVE_REQUESTS_MAX;
    }

    public EmailAction activationRequest(User user) {
        // Запрос активации после регистрации пользователя
        log.info("User {} request for activate profile", user.getEmail());
        EmailAction emailAction = create(user, EmailRequestType.ACTIVATE);
        try {
            sendEmail(emailAction);
        } catch (MessagingException e) {
            log.error("Nothing was sent {}", e.getCause().getMessage());
        }
        return emailAction;
    }

    @Transactional
    public EmailAction create(User user, EmailRequestType type) {
        String code = UtilsClass.randomString(35);
        while (repository.findByCode(code).isPresent()) {
            code = UtilsClass.randomString(35);
        }
        EmailAction emailAction = new EmailAction(type);
        emailAction.setUser(user);
        emailAction.setCode(code);
        return repository.save(emailAction);
    }

    public void sendEmail(EmailAction emailAction) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        EmailContext email = EmailContextUtil.getEmailContext(emailAction);

        context.setVariables(email.getContext());
        helper.setFrom(email.getFrom());
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        String html = templateEngine.process(email.getTemplate(), context);
        helper.setText(html, true);

        log.info("Sending email: {} with html body: {}", email, html);
        emailSender.send(message);
    }
}
