package de.psicho.redmine.protocol.service;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

import de.psicho.redmine.protocol.api.UserHandler;
import de.psicho.redmine.protocol.utils.DateUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProtocolService {

    private final static String PROTOCOL_PATH = "results";
    private final static String PROTOCOL_FILE_PREFIX = "Gemeinderat ";
    private final static String PDF_SUFFIX = ".pdf";

    @NonNull
    private UserHandler userHandler;

    public static String getProtocolFileName(Date protocolStartDate) {
        return PROTOCOL_FILE_PREFIX + DateUtils.dateToIso(protocolStartDate) + PDF_SUFFIX;
    }

    public static String getProtocolPath(Date protocolStartDate) {
        return PROTOCOL_PATH + "/" + getProtocolFileName(protocolStartDate);
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
        return user.getFirstName() + " " + user.getLastName().substring(0, 1) + ".";
    }
}
