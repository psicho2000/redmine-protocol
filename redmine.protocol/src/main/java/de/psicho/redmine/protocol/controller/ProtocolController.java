package de.psicho.redmine.protocol.controller;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.User;

import de.psicho.redmine.iTextile.iTextile;
import de.psicho.redmine.iTextile.command.TextProperty;
import de.psicho.redmine.protocol.api.AttachmentHandler;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.api.UserHandler;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.config.MailConfigurer;
import de.psicho.redmine.protocol.dao.StatusDao;
import de.psicho.redmine.protocol.dao.TopDao;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import de.psicho.redmine.protocol.utils.DateUtils;
import net.java.textilej.parser.markup.textile.TextileDialect;

@RestController
public class ProtocolController {

    private final static String PROTOCOL_PATH = "results";
    private final static String PROTOCOL_FILE_PREFIX = "Gemeinderat ";
    private final static String PDF_SUFFIX = ".pdf";

    @Value("${redmine.issues.link}")
    private String issueLinkPrefix;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    AttachmentHandler attachmentHandler;

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

    // TODO autowire as a (stateful --> prototype?) bean; how to inject filename (known at runtime, not compile time)
    // REMARK: if this is prototype, the whole chain must be!
    iTextile iTextile;

    Date protocolStartDate = null;
    Issue protocol = null;

    @RequestMapping("/protocol/{issueId}")
    public String createProtocol(@PathVariable String issueId) {
        protocol = validator.validate(issueId);
        protocolStartDate = protocol.getStartDate();
        String isoDate = DateUtils.dateToIso(protocolStartDate);

        startITextile(getProtocolPath());
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
        sendProtocol();

        return createResponse(issueId, isoDate, statusJournals, topJournals);
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

    // TODO protocol writer - iTextile references only here!

    private void startITextile(String filename) {
        File file = new File(filename);
        file.getParentFile().mkdirs();
        iTextile = new iTextile(filename);
    }

    private void finalizeITextile() {
        iTextile.createFile();
    }

    private void startTable() {
        iTextile.startTable(3);
        iTextile.setTableColumnWidth(0, 35f);
        iTextile.setTableColumnWidth(2, 70f);
        iTextile.setTableColumnParser(0, new TextileDialect());
        iTextile.setTableColumnParser(1, new TextileDialect());

        TextProperty format = TextProperty.builder().style(Font.BOLD).build();
        iTextile.setTableHeader(format, BaseColor.GRAY, "Nr.", "TOP / Beschluss", "Verantw.");

        String moderation = getProtocolUser(appConfig.getRedmineProtocolModeration());
        String devotion = getProtocolUser(appConfig.getRedmineProtocolDevotion());
        iTextile.addTableRow("---", "Moderation / Andacht", moderation + "/ " + devotion);
    }

    private void endTable() {
        iTextile.endTable();
    }

    private void writeDocumentHeader() {
        heading();

        String title = new StringBuilder().append("Gemeinderat am ").append(DateUtils.dateToGer(protocolStartDate))
            .append(" bei ").append(getProtocolValue(appConfig.getRedmineProtocolLocation())).toString();
        iTextile.startTable(2, Rectangle.NO_BORDER);
        iTextile.setTableColumnFormat(0, TextProperty.builder().alignment(Element.ALIGN_LEFT).build());
        iTextile.setTableColumnFormat(1, TextProperty.builder().alignment(Element.ALIGN_RIGHT).build());
        iTextile.addTableRow(title, getProtocolValue(appConfig.getRedmineProtocolNumber()));
        iTextile.endTable();

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
        BaseColor opalBlue = new BaseColor(25, 73, 150);
        iTextile.addParagraph(insertSpaces("PROTOKOLL", 8),
            TextProperty.builder().size(18.0f).style(Font.BOLD).color(opalBlue).alignment(Element.ALIGN_CENTER).build());
    }

    private String insertSpaces(String content, int numberOfSpaces) {
        String spacing = StringUtils.repeat(" ", numberOfSpaces);
        String[] stringArray = content.split("");
        return StringUtils.join(stringArray, spacing);
    }

    private void processStatus(List<IssueJournalWrapper> statusJournals) {
        String statusContent = "*Status vom letzten Protokoll*\r\n"
            + statusJournals.stream().map(this::appendJournal).collect(Collectors.joining("\r\n"));
        statusContent = setIssueLinks(statusContent);
        iTextile.addTableRow("---", statusContent, getProtocolUser(appConfig.getRedmineProtocolModeration()));
    }

    private String setIssueLinks(String statusContent) {
        Matcher matcher = Pattern.compile("#(\\d*)").matcher(statusContent);
        return matcher.replaceAll("\"#$1\":" + issueLinkPrefix + "$1");
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
            String number = setIssueLinks("#" + top.getIssueId().toString());
            String content;
            String title = "*" + top.getIssueSubject() + "*\r\n";
            if (top.getJournal() != null) {
                content = setIssueLinks(top.getJournal().getNotes());
            } else {
                Issue issue = issueHandler.getIssue(top.getIssueId());
                content = issue.getDescription();
            }
            Integer assignee = top.getAssignee();
            if (assignee == null || assignee == 0) {
                throw new RuntimeException(String.format("Das Ticket #%d wurde niemandem zugewiesen!", top.getIssueId()));
            }
            String responsible = getProtocolUser(assignee);
            iTextile.addTableRow(number, title + content, responsible);
        }
    }

    // TODO protocol finalizer

    private void closeProtocol() {
        if (protocol != null) {
            StringBuilder subject = new StringBuilder();
            subject.append("Gemeinderat ");
            subject.append(getProtocolValue(appConfig.getRedmineProtocolNumber()));
            subject.append(" am ");
            subject.append(DateUtils.dateToGer(protocolStartDate));
            protocol.setSubject(subject.toString());
            protocol.setStatusId(issueHandler.getStatusByName(appConfig.getRedmineProtocolClosed()));
            File attachment = new File(getProtocolPath());
            attachmentHandler.addAttachment(protocol.getId(), attachment);
            issueHandler.updateIssue(protocol);
        }
    }

    private void sendProtocol() {
        MailConfigurer mailConfig = appConfig.getMailConfigurer();
        MimeMessage message = mailSender.createMimeMessage();
        FileSystemResource file = new FileSystemResource(new File(getProtocolPath()));

        MimeMessageHelper helper;
        try {
            String dateGer = DateUtils.dateToGer(protocolStartDate);
            helper = new MimeMessageHelper(message, true);
            helper.setTo(mailConfig.getRecipient());
            helper.setSubject(String.format(mailConfig.getSubject(), dateGer));
            helper.setText(String.format("<html><body>" + mailConfig.getBody() + "</body></html>", dateGer), true);
            helper.addAttachment(getProtocolFileName(), file);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

        mailSender.send(message);
    }

    // TODO create a protocol wrapper

    private String getProtocolFileName() {
        return new StringBuilder().append(PROTOCOL_FILE_PREFIX).append(DateUtils.dateToIso(protocolStartDate)).append(PDF_SUFFIX)
            .toString();
    }

    private String getProtocolPath() {
        return new StringBuilder().append(PROTOCOL_PATH).append("/").append(getProtocolFileName()).toString();
    }

    private String getProtocolValue(String fieldName) {
        CustomField field = protocol.getCustomFieldByName(fieldName);
        return field.getValue();
    }

    private String getProtocolUser(String fieldName) {
        CustomField field = protocol.getCustomFieldByName(fieldName);
        Integer userId = Integer.valueOf(field.getValue());
        return getProtocolUser(userId);
    }

    private String getProtocolUser(Integer userId) {
        User user = userHandler.getUserById(userId);
        if (user == null) {
            throw new RuntimeException(String.format("Der User mit der Id %d konnte nicht gefunden werden.", userId));
        }
        return new StringBuilder().append(user.getFirstName()).append(" ").append(user.getLastName().substring(0, 1)).append(".")
            .toString();
    }
}
