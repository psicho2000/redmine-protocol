package de.psicho.redmine.protocol.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
}
