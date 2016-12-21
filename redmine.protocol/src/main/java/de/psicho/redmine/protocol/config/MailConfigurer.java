package de.psicho.redmine.protocol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "redmine.mail")
@Data
public class MailConfigurer {

    private String recipient;
    private String subject;
    private String body;
}
