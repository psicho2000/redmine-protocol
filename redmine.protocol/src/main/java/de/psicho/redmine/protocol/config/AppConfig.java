package de.psicho.redmine.protocol.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.psicho.redmine.protocol.ServiceMarker;
import de.psicho.redmine.protocol.api.ApiMarker;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.api.RedmineHandler;
import de.psicho.redmine.protocol.dao.DaoMarker;
import lombok.Getter;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = { ServiceMarker.class, DaoMarker.class, ApiMarker.class })
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

    @Bean
    public RedmineHandler redmineHandler() {
        return new RedmineHandler(this);
    }

    @Bean
    public IssueHandler issueHandler() {
        return new IssueHandler(redmineHandler());
    }
}
