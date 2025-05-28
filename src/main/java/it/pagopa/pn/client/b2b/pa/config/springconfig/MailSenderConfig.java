package it.pagopa.pn.client.b2b.pa.config.springconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailSenderConfig {

    private static final Logger logger = LoggerFactory.getLogger(MailSenderConfig.class.getName());

    @Value("${b2b.mail.username}")
    private String mailUsername;

    @Value("${b2b.mail.password}")
    private String mailPassowrd;

    @Bean
    public JavaMailSender javaMailSender(){
        logger.info("MAIL PASSWORD: {} MAIL Username: {}", mailPassowrd, mailUsername);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassowrd);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
