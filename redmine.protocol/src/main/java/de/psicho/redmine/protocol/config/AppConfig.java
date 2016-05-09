package de.psicho.redmine.protocol.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.psicho.redmine.protocol.ServiceMarker;
import de.psicho.redmine.protocol.dao.RepositoryMarker;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = { ServiceMarker.class, RepositoryMarker.class })
public class AppConfig {

    @Value("${redmine.api.url}")
    String redmineApiUrl;

    @Value("${redmine.api.accesskey}")
    String redmineApiAccesskey;

    @Value("${redmine.protocol.name}")
    String redmineProtocolName;

    @Value("${redmine.protocol.mandatory}")
    String redmineProtocolMandatory;

    @Value("${redmine.protocol.number}")
    String redmineProtocolNumber;

    @Value("${redmine.protocol.closed}")
    String redmineProtocolClosed;

    public String getRedmineProtocolMandatory() {
        return redmineProtocolMandatory;
    }

    public String getRedmineProtocolName() {
        return redmineProtocolName;
    }

    public String getRedmineProtocolNumber() {
        return redmineProtocolNumber;
    }

    public String getRedmineProtocolClosed() {
        return redmineProtocolClosed;
    }

    public String getRedmineApiUrl() {
        return redmineApiUrl;
    }

    public String getRedmineApiAccesskey() {
        return redmineApiAccesskey;
    }

}
