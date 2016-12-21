package de.psicho.redmine.protocol.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.psicho.redmine.protocol.api.AttachmentHandler;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.api.RedmineHandler;
import de.psicho.redmine.protocol.api.UserHandler;
import lombok.Getter;

@Configuration
@Getter
public class AppConfig {

    @Value("${redmine.api.url}")
    String redmineApiUrl;

    @Value("${redmine.api.accesskey}")
    String redmineApiAccesskey;

    @Value("${redmine.protocol.name}")
    String redmineProtocolName;

    @Value("${redmine.protocol.fields.number}")
    String redmineProtocolNumber;

    @Value("${redmine.protocol.fields.members}")
    String redmineProtocolMembers;

    @Value("${redmine.protocol.fields.meal}")
    String redmineProtocolMeal;

    @Value("${redmine.protocol.fields.location}")
    String redmineProtocolLocation;

    @Value("${redmine.protocol.fields.moderation}")
    String redmineProtocolModeration;

    @Value("${redmine.protocol.fields.devotion}")
    String redmineProtocolDevotion;

    @Value("${redmine.protocol.closed}")
    String redmineProtocolClosed;

    @Autowired
    MandatoryConfigurer mandatoryConfigurer;

    @Autowired
    MailConfigurer mailConfigurer;

    @Bean
    public RedmineHandler redmineHandler() {
        return new RedmineHandler(this);
    }

    @Bean
    public IssueHandler issueHandler() {
        return new IssueHandler(redmineHandler());
    }

    @Bean
    public UserHandler userHandler() {
        return new UserHandler(redmineHandler());
    }

    @Bean
    public AttachmentHandler attachmentHandler() {
        return new AttachmentHandler(redmineHandler());
    }
}
