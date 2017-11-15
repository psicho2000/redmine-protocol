package de.psicho.redmine.protocol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import de.psicho.redmine.protocol.api.AttachmentHandler;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.api.RedmineHandler;
import de.psicho.redmine.protocol.api.UserHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Configuration
@Getter
@EnableJpaRepositories(basePackages = "de.psicho.redmine.protocol.repository")
@RequiredArgsConstructor
public class AppConfig {

    @NonNull
    private RedmineConfigurer redmine;

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
