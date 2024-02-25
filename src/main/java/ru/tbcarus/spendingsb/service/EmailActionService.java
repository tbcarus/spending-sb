package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.config.MailConfig;
import ru.tbcarus.spendingsb.exception.BadRegistrationRequest;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailRequestType;
import ru.tbcarus.spendingsb.model.ErrorType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.JpaEmailActionRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.ConfigUtil;
import ru.tbcarus.spendingsb.util.UtilsClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EmailActionService {
    @Autowired
    JpaEmailActionRepository repository;

    @Autowired
    JpaUserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;

    public EmailAction get(String code) {
        return repository.getByCode(code);
    }

    public EmailAction registerConfirm(String email, String code, EmailRequestType type) {
        EmailAction emailAction = emailActionCheck(email, code, type);
        User user = emailAction.getUser();
        user.setEnabled();
        userRepository.save(user);
//        emailAction.setUsed(true);
//        repository.save(emailAction);
        return emailAction;
    }

    public EmailAction passwordResetGet(String email, String code, EmailRequestType type) {
        EmailAction emailAction = emailActionCheck(email, code, type);
//        emailAction.setUsed(true);
//        repository.save(emailAction);
        return emailAction;
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

    public EmailAction resendRequest(String email, String code) {
        EmailAction old = repository.getByCode(code);
        if (old.isExpired()) {
            if (checkRepeatRequest(old)) {
                throw new BadRegistrationRequest(ErrorType.TOO_MUCH_REPEAT_REQUESTS);
            }
            EmailAction emailAction = create(old.getUser(), old.getType());
            sendEmail(emailAction);
            return emailAction;
        }
        return old;
    }

    private boolean checkRepeatRequest(EmailAction emailAction) {
        // Проверка на многократные запросы. Исключение вызывается при:
        // - запросы на смену пароля. Есть неиспользованный запрос на восстановление пароля.
        // - переход по старой ссылке восстановления пароля, но есть новая
        // - запросы на активацию. Новый запрос при переходе по старой ссылке с истекшим сроком (или по любой ссылке), но есть новая неиспользованная ссылка. Если пользователь уже активирован
        List<EmailAction> list = repository.findAllByUserIdAndDateTimeBetweenAndTypeOrderByDateTimeDesc(emailAction.getUser().getId(),
                LocalDateTime.now().minusDays(ConfigUtil.DEFAULT_EXPIRED_DAYS),
                LocalDateTime.now(),
                emailAction.getType());

        return !list.isEmpty();
    }

    public EmailAction activationRequest(User user) {
        log.info("User {} request for activate profile", user.getEmail());
        EmailAction emailAction = create(user, EmailRequestType.ACTIVATE);
        sendEmail(emailAction);
        return emailAction;
    }

    public EmailAction passwordResetRequest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();

        EmailAction emailAction = create(user, EmailRequestType.RESET_PASSWORD);
        sendEmail(emailAction);
        return emailAction;
    }

    private void checkRepeatPasswordRequestRequest(User user) {
        // Проверка на многократные запросы. Исключение вызывается при:
        // - запросы на смену пароля. Есть неиспользованный запрос на восстановление пароля.
        // - переход по старой ссылке восстановления пароля, но есть новая
        // - запросы на активацию. Новый запрос при переходе по старой ссылке с истекшим сроком (или по любой ссылке), но есть новая неиспользованная ссылка. Если пользователь уже активирован

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

    public void sendEmail(EmailAction emailAction) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(MailConfig.MAIL_FROM);
        smm.setTo(emailAction.getUser().getEmail());
        smm.setSubject(emailAction.getType().getTitle());
        smm.setText("http://localhost:8080/spending/payments/profile/register/"
                + emailAction.getType().name()
                + "?email="
                + emailAction.getUser().getEmail()
                + "&code="
                + emailAction.getCode());
        javaMailSender.send(smm);
    }
}
