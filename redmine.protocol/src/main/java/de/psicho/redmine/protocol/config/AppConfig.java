package de.psicho.redmine.protocol.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.psicho.redmine.protocol.ServiceMarker;
import de.psicho.redmine.protocol.dao.IssueHandler;
import de.psicho.redmine.protocol.dao.RedmineHandler;
import de.psicho.redmine.protocol.dao.RepositoryMarker;
import lombok.Getter;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = { ServiceMarker.class, RepositoryMarker.class })
@Getter
public class AppConfig {

    @Value("${redmine.api.url}")
    String redmineApiUrl;

    @Value("${redmine.api.accesskey}")
    String redmineApiAccesskey;

    @Value("${redmine.protocol.name}")
    String redmineProtocolName;

    @Value("${redmine.protocol.number}")
    String redmineProtocolNumber;

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
