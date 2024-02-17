package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.config.MailConfig;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailRequestType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.JpaEmailActionRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.UtilsClass;

import java.util.Random;

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
        EmailAction emailAction = get(code);
        if (emailAction == null || !email.equals(emailAction.getUser().getEmail()) || !type.equals(emailAction.getType())) {
            throw new NotFoundException("Запрос на регистрацию не найден");
        }
        User user = emailAction.getUser();
        user.setEnabled();
        userRepository.save(user);
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
