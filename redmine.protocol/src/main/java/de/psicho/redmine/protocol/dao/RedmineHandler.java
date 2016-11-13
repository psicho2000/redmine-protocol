package de.psicho.redmine.protocol.dao;

import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;

import de.psicho.redmine.protocol.config.AppConfig;
import lombok.Getter;

@Getter
@Component
public class RedmineHandler {
    RedmineManager redmineManager;

    public RedmineHandler(AppConfig appConfig) {
        redmineManager = RedmineManagerFactory.createWithApiKey(appConfig.getRedmineApiUrl(),
                appConfig.getRedmineApiAccesskey());
    }
}
