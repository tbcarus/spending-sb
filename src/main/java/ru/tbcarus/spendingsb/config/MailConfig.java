package ru.tbcarus.spendingsb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    public static final String MAIL_FROM = "tbcarus@yandex.ru"; //tbcarus@yandex.ru spending-sb@yandex.ru FidelAC26@yandex.ru

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.yandex.ru");
        mailSender.setPort(465);

        mailSender.setUsername("tbcarus");
        mailSender.setPassword("hpjsguumudneendo"); //hpjsguumudneendo ftialbzddhybalgh olasukznykzrucor

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.ssl.enable", "true");

        return mailSender;
    }
}
