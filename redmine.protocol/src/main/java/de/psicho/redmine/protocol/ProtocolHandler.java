package de.psicho.redmine.protocol;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;

import de.psicho.redmine.iTextile.ITextExample;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.dao.IssueDao;
import de.psicho.redmine.protocol.dao.StatusDao;
import de.psicho.redmine.protocol.dao.TopDao;
import de.psicho.redmine.protocol.model.Validation;

@RestController
public class ProtocolHandler {
    @Autowired
    IssueDao issueDao;

    @Autowired
    StatusDao statusDao;

    @Autowired
    TopDao topDao;

    @Autowired
    AppConfig appConfig;

    Date protocolStartDate = null;
    Issue protocol = null;

    @RequestMapping("/")
    private String createProtocol(@RequestParam(value = "issueId", defaultValue = "") String issueId) {
        writeHeader();
        if (true)
            return "Datei geschrieben";

        Validation validation = validateProtocol(issueId);
        if (!validation.isEmpty()) {
            return validation.render();
        }

        String isoDate = dateToIso(protocolStartDate);

        List<Journal> statusJournals = statusDao.findJournals(isoDate);
        for (Journal curJournal : statusJournals) {
            processStatus(curJournal);
        }

        List<Journal> topJournals = topDao.findJournals(isoDate);
        for (Journal curJournal : topJournals) {
            processTop(curJournal);
        }

        closeProtocol();

        String result = "Creating protocol for id: " + issueId;
        result += "<br>Querying for date " + isoDate;
        result += "<br># StatusItems: " + statusJournals.size();
        result += "<br># TopItems: " + topJournals.size();
        return result;
    }

    private Validation validateProtocol(String issueId) {
        Validation validation = new Validation();

        if (StringUtils.isBlank(issueId)) {
            validation.add("Der Parameter issueId muss angegeben sein.");
            return validation;
        }
        if (!StringUtils.isNumeric(issueId)) {
            validation.add("issueId muss eine Zahl sein.");
            return validation;
        }

        int ticketId = Integer.parseInt(issueId);
        protocol = issueDao.getIssue(ticketId);
        if (protocol == null) {
            validation.add("Ticket " + issueId + " konnte nicht gefunden werden.");
            return validation;
        }

        if (!protocol.getTracker().getName().equals(appConfig.getRedmineProtocolName())) {
            validation.add("Das Ticket " + protocol.getId() + " ist kein Protokoll (Tracker = "
                    + appConfig.getRedmineProtocolName() + ").");
        }
        if (protocol.getAssignee() == null) {
            validation.add("Das Ticket wurde an niemand zugewiesen.");
        }

        protocolStartDate = protocol.getStartDate();
        if (protocolStartDate == null) {
            validation.add("Beginn muss ein g�ltiges Datum sein.");
        }

        List<String> mandatoryFields = getMandatoryFields();
        for (String field : mandatoryFields) {
            if (protocol.getCustomFieldByName(field) == null
                    || StringUtils.isBlank(protocol.getCustomFieldByName(field).getValue())) {
                validation.add("Feld " + field + " muss angegeben werden.");
            }
        }

        return validation;
    }

    private void writeHeader() {
        // TODO Auto-generated method stub
        String DEST = "results/paragraph_spacebefore.pdf";

        File file = new File(DEST);
        file.getParentFile().mkdirs();
        try {
            new ITextExample().createPdf(DEST);
        } catch (IOException | DocumentException ex) {
            ex.printStackTrace();
        }
    }

    private void processStatus(Journal journal) {
        // TODO Auto-generated method stub

    }

    private void processTop(Journal journal) {
        // TODO Auto-generated method stub
    }

    private void closeProtocol() {
        if (protocol != null) {
            CustomField number = protocol.getCustomFieldByName(appConfig.getRedmineProtocolNumber());
            protocol.setSubject("Gemeinderat " + number.getValue() + " am " + dateToGer(protocolStartDate));
            protocol.setStatusId(issueDao.getStatusByName(appConfig.getRedmineProtocolClosed()));
            issueDao.updateIssue(protocol);
        }
    }

    private List<String> getMandatoryFields() {
        String mandatoryString = appConfig.getRedmineProtocolMandatory();
        List<String> mandatoryFields = Arrays.asList(mandatoryString.split("\\s*,\\s*"));
        return mandatoryFields;
    }

    private String dateToIso(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String isoDate = cal.get(Calendar.YEAR) + "-" + zeroPad(cal.get(Calendar.MONTH) + 1) + "-"
                + zeroPad(cal.get(Calendar.DAY_OF_MONTH));

        return isoDate;
    }

    private String dateToGer(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String isoDate = zeroPad(cal.get(Calendar.DAY_OF_MONTH)) + "." + zeroPad(cal.get(Calendar.MONTH) + 1) + "."
                + cal.get(Calendar.YEAR);

        return isoDate;
    }

    private String zeroPad(Integer input) {
        return StringUtils.leftPad(input.toString(), 2, "0");
    }
}
