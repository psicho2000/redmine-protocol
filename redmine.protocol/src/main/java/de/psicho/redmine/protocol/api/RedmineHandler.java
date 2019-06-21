package de.psicho.redmine.protocol.api;

import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;

import de.psicho.redmine.protocol.config.Api;
import de.psicho.redmine.protocol.config.AppConfig;
import lombok.Getter;

@Getter
public class RedmineHandler {

    private RedmineManager redmineManager;

    public RedmineHandler(AppConfig appConfig) {
        Api redmineApi = appConfig.getRedmine().getApi();
        redmineManager = RedmineManagerFactory.createWithApiKey(redmineApi.getUrl(), redmineApi.getAccesskey());
    }
}
