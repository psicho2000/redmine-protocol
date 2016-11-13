package de.psicho.redmine.protocol.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "redmine.protocol")
@Data
public class MandatoryConfigurer {
    private List<String> mandatory = new ArrayList<String>();
}
