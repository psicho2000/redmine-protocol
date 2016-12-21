package de.psicho.redmine.protocol.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "redmine")
public class RedmineConfigurer {

    private Mail mail;
    private Protocol protocol;
    private Api api;
    private Issues issues;
}
