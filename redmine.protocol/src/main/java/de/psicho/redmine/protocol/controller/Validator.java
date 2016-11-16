package de.psicho.redmine.protocol.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.bean.Issue;

import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.model.Validation;

@Component
public class Validator {

    @Autowired
    AppConfig appConfig;

    @Autowired
    IssueHandler issueHandler;

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
        Validation validation = new Validation();

        if (!protocol.getTracker().getName().equals(appConfig.getRedmineProtocolName())) {
            validation.add(String.format("Das Ticket '%d' ist kein Protokoll (Tracker = '%s').", protocol.getId(),
                appConfig.getRedmineProtocolName()));
            return validation;
        }

        if (protocol.getStatusName().equals(appConfig.getRedmineProtocolClosed())) {
            validation.add("Das Protokoll wurde bereits geschlossen.");
        }

        if (protocol.getAssigneeId() == null) {
            validation.add("Das Ticket wurde niemandem zugewiesen.");
        }

        if (protocol.getStartDate() == null) {
            validation.add("Beginn muss ein gültiges Datum sein.");
        }

        List<String> mandatoryFields = appConfig.getMandatoryConfigurer().getMandatory();
        for (String field : mandatoryFields) {
            if (protocol.getCustomFieldByName(field) == null
                || StringUtils.isBlank(protocol.getCustomFieldByName(field).getValue())) {
                validation.add(String.format("Feld '%s' muss angegeben werden.", field));
            }
        }
        return validation;
    }

    public Issue validate(String issueId) {
        Validation validation = validateIssueId(issueId);
        if (!validation.isEmpty()) {
            throw new ValidationException(validation.render());
        }

        Issue protocol = openTicket(issueId);
        if (protocol == null) {
            throw new ValidationException(String.format("Ticket '%d' konnte nicht gefunden werden.", issueId));
        }

        validation = validateProtocol(protocol);
        if (!validation.isEmpty()) {
            throw new ValidationException(validation.render());
        }
        return protocol;
    }

    private Issue openTicket(String issueId) {
        int ticketId = Integer.parseInt(issueId);
        return issueHandler.getIssue(ticketId);
    }
}
