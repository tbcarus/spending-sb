package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.config.MailConfig;
import ru.tbcarus.spendingsb.exception.BadRegistrationRequest;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailRequestType;
import ru.tbcarus.spendingsb.model.ErrorType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.JpaEmailActionRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.UtilsClass;

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
        User user = emailAction.getUser();
        user.setEnabled();
        userRepository.save(user);
//        emailAction.setUsed(true);
//        repository.save(emailAction);
        return emailAction;
    }

    public EmailAction resendRequestServ(String email, String code) {
        EmailAction old = repository.getByCode(code);
        if (old.isExpired()) {
            return create(old.getUser(), old.getType());
        }
        return old;
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
        EmailAction ea = repository.save(emailAction);
        sendEmail(ea);
        return ea;
    }

    public void sendEmail(EmailAction emailAction) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(MailConfig.MAIL_FROM);
        smm.setTo(emailAction.getUser().getEmail());
        smm.setSubject(emailAction.getType().getTitle());
        smm.setText("http://localhost:8080/spending/payments/profile/register/confirm-email?email=" + emailAction.getUser().getEmail() + "&code=" + emailAction.getCode());
        javaMailSender.send(smm);
    }
}
