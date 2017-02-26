package de.psicho.redmine.protocol.service;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

import de.psicho.redmine.protocol.api.UserHandler;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import de.psicho.redmine.protocol.utils.DateUtils;

@Component
public class ProtocolService {

    private final static String PROTOCOL_PATH = "results";
    private final static String PROTOCOL_FILE_PREFIX = "Gemeinderat ";
    private final static String PDF_SUFFIX = ".pdf";

    @Autowired
    private UserHandler userHandler;

    public static String getProtocolFileName(Date protocolStartDate) {
        return new StringBuilder().append(PROTOCOL_FILE_PREFIX).append(DateUtils.dateToIso(protocolStartDate)).append(PDF_SUFFIX)
            .toString();
    }

    public static String getProtocolPath(Date protocolStartDate) {
        return new StringBuilder().append(PROTOCOL_PATH).append("/").append(getProtocolFileName(protocolStartDate)).toString();
    }

    public String getProtocolValue(Issue protocol, String fieldName) {
        CustomField field = protocol.getCustomFieldByName(fieldName);
        return field.getValue();
    }

    public String getProtocolUser(Issue protocol, String fieldName) {
        CustomField field = protocol.getCustomFieldByName(fieldName);
        Integer userId = Integer.valueOf(field.getValue());
        return getProtocolUser(userId);
    }

    public String getProtocolUser(Integer userId) {
        User user = userHandler.getUserById(userId);
        if (user == null) {
            throw new RuntimeException(String.format("Der User mit der Id %d konnte nicht gefunden werden.", userId));
        }
        return new StringBuilder().append(user.getFirstName()).append(" ").append(user.getLastName().substring(0, 1)).append(".")
            .toString();
    }

    public List<IssueJournalWrapper> filterTopJournals(List<IssueJournalWrapper> topJournals) {
        Predicate<IssueJournalWrapper> filterByContent =
            journal -> journal.getJournal() == null || !StringUtils.isBlank(journal.getJournal().getNotes());
        return topJournals.stream().filter(filterByContent).collect(Collectors.toList());
    }
}
