package it.pagopa.pn.client.b2b.pa.config.springconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@ConfigurationProperties(prefix = "b2b.mail", ignoreUnknownFields = false)
@Data
public class MailSenderConfig {

    private String username;

    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        System.out.println("MAIL PASSWORD: " + password + " MAIL Username: " + username);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
