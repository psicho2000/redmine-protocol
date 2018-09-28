package de.psicho.redmine.protocol.controller;

import static java.lang.String.format;

import org.springframework.stereotype.Component;

import de.psicho.redmine.protocol.config.AppConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LinkUtils {

    @NonNull
    private AppConfig appConfig;

    String getShortLink(Integer issueId) {
        String linkToProtocol = appConfig.getRedmine().getIssues().getLink() + issueId;
        return format("<a href=\"%s\">%s</a>", linkToProtocol, issueId);
    }

    String getLongLink(Integer issueId) {
        String linkToProtocol = appConfig.getRedmine().getIssues().getLink() + issueId;
        return format("<a href=\"%s\">%s</a>", linkToProtocol, linkToProtocol);
    }

}
