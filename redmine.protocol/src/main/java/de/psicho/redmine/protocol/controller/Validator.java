package de.psicho.redmine.protocol.controller;

import static java.lang.String.format;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.bean.Issue;

import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.config.Protocol;
import de.psicho.redmine.protocol.model.Validation;
import de.psicho.redmine.protocol.utils.LinkUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Validator {

    @NonNull
    private AppConfig appConfig;

    @NonNull
    private IssueHandler issueHandler;

    @NonNull
    private LinkUtils linkUtils;

    private Validation validateIssueId(String issueId) {
        Validation validation = new Validation();

        if (StringUtils.isBlank(issueId)) {
            validation.add("Der Parameter issueId muss angegeben sein.");
        }
        if (!StringUtils.isNumeric(issueId)) {
            validation.add("issueId muss eine Zahl sein.");
        }

        return validation;
    }

    private Validation validateProtocol(Issue protocol) {
        Validation validation = new Validation(linkUtils.getShortLink(protocol.getId()));
        Protocol redmineProtocol = appConfig.getRedmine().getProtocol();

        if (!protocol.getTracker().getName().equals(redmineProtocol.getName())) {
            validation.add(format("Das Ticket ist kein Protokoll (Tracker muss '%s' sein).", redmineProtocol.getName()));
            return validation;
        }

        if (protocol.getStatusName().equals(redmineProtocol.getClosed())) {
            validation.add("Das Protokoll wurde bereits geschlossen.");
        }

        if (protocol.getAssigneeId() == null) {
            validation.add("Das Protokoll wurde niemandem zugewiesen. Es muss dem Protokollschreiber entsprechen.");
        }

        if (protocol.getStartDate() == null) {
            validation.add("'Beginn' muss ein gültiges Datum sein und dem Tag des Meetings entsprechen.");
        }

        List<String> mandatoryFields = redmineProtocol.getMandatory();
        for (String field : mandatoryFields) {
            if (protocol.getCustomFieldByName(field) == null
                || StringUtils.isBlank(protocol.getCustomFieldByName(field).getValue())) {
                validation.add(format("Feld '%s' muss angegeben werden.", field));
            }
        }

        return validation;
    }

    Issue validate(String issueId) {
        Validation validation = validateIssueId(issueId);
        if (validation.isNotEmpty()) {
            throw new ValidationException(validation.render());
        }

        Issue protocol = openTicket(issueId);
        if (protocol == null) {
            throw new ValidationException(format("Ticket '%s' konnte nicht gefunden werden.", issueId));
        }

        validation = validateProtocol(protocol);
        if (validation.isNotEmpty()) {
            throw new ValidationException(validation.render());
        }
        return protocol;
    }

    private Issue openTicket(String issueId) {
        int ticketId = Integer.parseInt(issueId);
        return issueHandler.getIssue(ticketId);
    }
}
