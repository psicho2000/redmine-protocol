package de.psicho.redmine.protocol.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

import de.psicho.redmine.iTextile.iTextile;
import de.psicho.redmine.iTextile.command.TextProperty;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.api.UserHandler;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.dao.StatusDao;
import de.psicho.redmine.protocol.dao.TopDao;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import de.psicho.redmine.protocol.utils.DateUtils;

@RestController
public class ProtocolController {

    private final static String PROTOCOL_FILENAME = "results/Protokoll.pdf";
    private final static String AGENDA_FILENAME = "results/Agenda.pdf";

    @Autowired
    IssueHandler issueHandler;

    @Autowired
    UserHandler userHandler;

    @Autowired
    StatusDao statusDao;

    @Autowired
    TopDao topDao;

    @Autowired
    AppConfig appConfig;

    @Autowired
    Validator validator;

    iTextile iTextile;

    Date protocolStartDate = null;
    Issue protocol = null;

    @RequestMapping("/agenda")
    public String createAgenda() {
        startITextile(AGENDA_FILENAME);
        // TODO create new protocol ticket
        // TODO create agenda.pdf
        return null;
    }

    @RequestMapping("/protocol/{issueId}")
    public String createProtocol(@PathVariable String issueId) {
        protocol = validator.validate(issueId);
        protocolStartDate = protocol.getStartDate();
        String isoDate = DateUtils.dateToIso(protocolStartDate);

        startITextile(PROTOCOL_FILENAME);
        writeDocumentHeader();
        startTable();

        List<IssueJournalWrapper> statusJournals = statusDao.findJournals(isoDate);
        processStatus(statusJournals);

        List<IssueJournalWrapper> topJournals = topDao.findJournals(isoDate);
        processTop(topJournals);

        endTable();
        finalizeITextile();

        // FIXME temporarily don't close the protocol
        // closeProtocol();

        return createResponse(issueId, isoDate, statusJournals, topJournals);
    }

    private void startTable() {
        iTextile.startTable(3);
        iTextile.setTableColumnWidth(0, 30f);
        iTextile.setTableColumnWidth(2, 70f);

        TextProperty format = TextProperty.builder().style(Font.BOLD).build();
        iTextile.setTableHeader(format, BaseColor.GRAY, "Nr.", "TOP / Beschluss", "Verantw.");

        String moderation = getProtocolUser(appConfig.getRedmineProtocolModeration());
        String devotion = getProtocolUser(appConfig.getRedmineProtocolDevotion());
        iTextile.addTableRow("---", "Moderation / Andacht", moderation + "/ " + devotion);
    }

    private void endTable() {
        iTextile.endTable();
    }

    private String createResponse(String issueId, String isoDate, List<IssueJournalWrapper> statusJournals,
        List<IssueJournalWrapper> topJournals) {

        StringBuffer result = new StringBuffer();
        result.append("Creating protocol for id: ");
        result.append(issueId);
        result.append("<br>Querying for date ");
        result.append(isoDate);
        result.append("<br># StatusItems: ");
        result.append(statusJournals.size());
        result.append("<br># TopItems: ");
        result.append(topJournals.size());
        return result.toString();
    }

    private void startITextile(String filename) {
        File file = new File(filename);
        file.getParentFile().mkdirs();
        iTextile = new iTextile(filename);
    }

    private void finalizeITextile() {
        iTextile.createFile();
    }

    private void writeDocumentHeader() {
        heading();

        StringBuilder title = new StringBuilder();
        title.append("Gemeinderat am ");
        title.append(DateUtils.dateToGer(protocolStartDate));
        title.append(" bei ");
        title.append(getProtocolValue(appConfig.getRedmineProtocolLocation()));
        title.append("             "); // FIXME
        title.append(getProtocolValue(appConfig.getRedmineProtocolNumber()));
        paragraph(title.toString());

        StringBuilder meal = new StringBuilder();
        meal.append("Essen: ");
        meal.append(getProtocolValue(appConfig.getRedmineProtocolMeal()));
        paragraph(meal.toString());

        StringBuilder members = new StringBuilder();
        members.append("Anwesend: ");
        members.append(getProtocolValue(appConfig.getRedmineProtocolMembers()));
        paragraph(members.toString());
    }

    private void paragraph(String title) {
        iTextile.addParagraph(title, TextProperty.builder().size(12.0f).style(Font.NORMAL).color(BaseColor.BLACK).build());
    }

    private void heading() {
        iTextile.addParagraph("P    R    O    T    O    K    O    L    L",
            TextProperty.builder().size(18.0f).style(Font.BOLD).color(BaseColor.BLUE).alignment(Element.ALIGN_CENTER).build());
    }

    private void processStatus(List<IssueJournalWrapper> statusJournals) {
        String statusContent = "*Status vom letzten Protokoll*\r\n"
            + statusJournals.stream().map(this::appendJournal).collect(Collectors.joining("\r\n"));
        iTextile.addTableRow("---", statusContent, getProtocolUser(appConfig.getRedmineProtocolModeration()));
    }

    private String appendJournal(IssueJournalWrapper status) {
        StringBuilder statusContent = new StringBuilder();
        statusContent.append("#");
        statusContent.append(status.getIssueId());
        statusContent.append(" (");
        statusContent.append(status.getIssueSubject());
        statusContent.append(")");
        statusContent.append("\r\n");
        statusContent.append(status.getJournal().getNotes());
        return statusContent.toString();
    }

    private void processTop(List<IssueJournalWrapper> topJournals) {
        for (IssueJournalWrapper top : topJournals) {
            // FIXME
        }
    }

    private void closeProtocol() {
        if (protocol != null) {
            StringBuilder subject = new StringBuilder();
            subject.append("Gemeinderat ");
            subject.append(getProtocolValue(appConfig.getRedmineProtocolNumber()));
            subject.append(" am ");
            subject.append(DateUtils.dateToGer(protocolStartDate));
            protocol.setSubject(subject.toString());
            protocol.setStatusId(issueHandler.getStatusByName(appConfig.getRedmineProtocolClosed()));
            issueHandler.updateIssue(protocol);
        }
    }

    private String getProtocolValue(String fieldName) {
        CustomField field = protocol.getCustomFieldByName(fieldName);
        return field.getValue();
    }

    private String getProtocolUser(String fieldName) {
        CustomField field = protocol.getCustomFieldByName(fieldName);
        User user = userHandler.getUserById(field.getValue());
        return new StringBuilder().append(user.getFirstName()).append(" ").append(user.getLastName().substring(0, 1)).append(".")
            .toString();
    }
}
