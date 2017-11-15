package de.psicho.redmine.protocol.controller;

import static de.psicho.redmine.protocol.service.ProtocolService.getProtocolFileName;
import static de.psicho.redmine.protocol.service.ProtocolService.getProtocolPath;
import static java.lang.String.format;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;

import de.psicho.redmine.iTextile.utils.ResourceUtils;
import de.psicho.redmine.protocol.api.AttachmentHandler;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.config.Mail;
import de.psicho.redmine.protocol.config.Protocol;
import de.psicho.redmine.protocol.dao.IssueDao;
import de.psicho.redmine.protocol.model.AttachedFile;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import de.psicho.redmine.protocol.service.ITextService;
import de.psicho.redmine.protocol.service.ProtocolService;
import de.psicho.redmine.protocol.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProtocolController {

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AttachmentHandler attachmentHandler;

    @Autowired
    private IssueHandler issueHandler;

    @Autowired
    private IssueDao issueDao;

    @Autowired
    private AppConfig appConfig;

    private Protocol redmineProtocol;

    @Autowired
    private Validator validator;

    @Autowired
    private ITextService iTextService;

    @PostConstruct
    private void init() {
        redmineProtocol = appConfig.getRedmine().getProtocol();
    }

    @RequestMapping("/")
    public String info() {
        String style = ResourceUtils.readResource("style.css");
        String body = "Create protocol for a given Redmine protocol issue.<br /><br />" + "Usage: /protocol/{protocolId}<br />"
            + "Given:<br /><ul><li>Open issue of Tracker \"Protokoll\"<li>Any topic"
            + "<li>Start date = day of the meeting (must be same when tickets notes have been updated)"
            + "<li>Non-empty fields Zugewiesen an, Andacht, Anwesend, Essen, Ort, Nummer, Moderation</ul>";
        String footer = "This software can be found at <a href=\"https://github.com/psicho2000/redmine-protocol\">GitHub</a> "
            + "and is released under <a href=\"https://www.gnu.org/licenses/agpl-3.0.en.html\">AGPL 3.0</a>. "
            + "It uses <a href=\"https://itextpdf.com/\">iText</a>.";
        return format("<!DOCTYPE html><html><head><style>%s</style></head><body>%s</body><footer>%s</footer></html>", style, body,
            footer);
    }

    @RequestMapping("/protocol/{issueId}")
    public String createProtocol(@PathVariable String issueId,
        @RequestParam(name = "autoclose", defaultValue = "false") boolean autoclose) {

        ResponseInfo responseInfo = null;
        Exception exception = null;

        try {
            Issue protocol = validator.validate(issueId);
            Date protocolStartDate = protocol.getStartDate();
            String isoDate = DateUtils.dateToIso(protocolStartDate);

            iTextService.startITextile(getProtocolPath(protocolStartDate));
            iTextService.writeDocumentHeader(protocol);
            iTextService.startTable(protocol);

            List<IssueJournalWrapper> statusJournals = issueDao.findJournals("Aufgabe", isoDate);
            iTextService.processStatus(protocol, statusJournals);

            List<IssueJournalWrapper> topJournals = issueDao.findJournals("TOP", isoDate);
            addDescription(topJournals);
            Set<AttachedFile> attachedFiles = iTextService.processTop(topJournals);

            iTextService.endTable();
            iTextService.finalizeITextile();

            if (autoclose) {
                closeProtocol(protocol);
            }
            sendProtocol(protocol, attachedFiles);

            responseInfo = ResponseInfo.builder().issueId(issueId).isoDate(isoDate).statusJournals(statusJournals)
                .topJournals(topJournals).build();
        } catch (Exception ex) {
            exception = ex;
            log.error(ExceptionUtils.getStackTrace(ex));
        }

        return createResponse(responseInfo, exception, autoclose);
    }

    private void addDescription(List<IssueJournalWrapper> topJournals) {
        for (IssueJournalWrapper topJournal : topJournals) {
            Integer firstJournalId = issueDao.getFirstNonEmptyJournalByIssueId(topJournal.getIssueId());
            Journal journal = topJournal.getJournal();
            if (journal.getId().equals(firstJournalId)) {
                Issue issue = issueHandler.getIssue(topJournal.getIssueId());
                String description = issue.getDescription();
                if (StringUtils.isNotBlank(description)) {
                    journal.setNotes(description + "\r\n\r\n<hr/>\r\n\r\n" + journal.getNotes());
                }
            }
        }
    }

    private String createResponse(ResponseInfo responseInfo, Exception thrownException, boolean autoclose) {
        StringBuffer result = new StringBuffer();

        if (thrownException != null) {
            result.append("<pre>");
            result.append(ExceptionUtils.getStackTrace(thrownException));
            result.append("</pre>");
        } else {
            Consumer<IssueJournalWrapper> issueInfoAppender =
                issue -> result.append("<br>").append(issue.getIssueId()).append(": ").append(issue.getIssueSubject());

            result.append("Creating protocol for id: ");
            result.append(responseInfo.getIssueId());
            result.append("<br/>Querying for date ");
            result.append(responseInfo.getIsoDate());
            result.append("<br/><h3>StatusItems: ");
            result.append(responseInfo.getStatusJournals().size());
            result.append("</h3> ");
            responseInfo.getStatusJournals().forEach(issueInfoAppender);
            result.append("<br/><h3>TopItems: ");
            result.append(responseInfo.getTopJournals().size());
            result.append("</h3> ");
            responseInfo.getTopJournals().forEach(issueInfoAppender);
            result.append("<p><br/>Protocol has ");
            result.append(autoclose ? "been <strong>closed</strong>." : "<strong>not</strong> been closed.");
            if (!autoclose) {
                result.append("<br/>Close by appending: ?autoclose=1");
            }
        }

        return result.toString();
    }

    private void closeProtocol(Issue protocol) {
        Date protocolStartDate = protocol.getStartDate();
        String subject = "Gemeinderat " + protocolService.getProtocolValue(protocol, redmineProtocol.getFields().getNumber())
            + " am " + DateUtils.dateToGer(protocolStartDate);
        protocol.setSubject(subject);
        protocol.setStatusId(issueHandler.getStatusByName(redmineProtocol.getClosed()));
        File attachment = new File(getProtocolPath(protocolStartDate));
        attachmentHandler.addAttachment(protocol.getId(), attachment);
        issueHandler.updateIssue(protocol);
    }

    private void sendProtocol(Issue protocol, Set<AttachedFile> attachedFiles) {
        Date protocolStartDate = protocol.getStartDate();
        Mail mailConfig = appConfig.getRedmine().getMail();
        MimeMessage message = mailSender.createMimeMessage();
        FileSystemResource file = new FileSystemResource(new File(getProtocolPath(protocolStartDate)));

        MimeMessageHelper helper;
        try {
            String dateGer = DateUtils.dateToGer(protocolStartDate);
            String linkToProtocol = appConfig.getRedmine().getIssues().getLink() + protocol.getId();
            linkToProtocol = format("<a href=\"%s\">%s</a>", linkToProtocol, linkToProtocol);
            helper = new MimeMessageHelper(message, true);
            helper.setTo(mailConfig.getRecipient());
            helper.setSubject(format(mailConfig.getSubject(), dateGer));
            helper.setText(format("<html><body>" + mailConfig.getBody() + "</body></html>", dateGer, linkToProtocol), true);
            helper.addAttachment(getProtocolFileName(protocolStartDate), file);

            for (AttachedFile attachedFile : attachedFiles) {
                byte[] binaryAttachment = attachmentHandler.getAttachment(attachedFile.getIssueId(), attachedFile.getFileName());
                ByteArrayResource byteArrayResource = new ByteArrayResource(binaryAttachment);
                helper.addAttachment(attachedFile.getFileName(), byteArrayResource);
            }
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

        mailSender.send(message);
    }
}
