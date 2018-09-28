package de.psicho.redmine.protocol.controller;

import static de.psicho.redmine.protocol.service.ProtocolService.getProtocolFileName;
import static de.psicho.redmine.protocol.service.ProtocolService.getProtocolPath;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
import de.psicho.redmine.protocol.utils.LinkUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProtocolController {

    private static final String AUTOCLOSE = "autoclose";
    private static final String AUTOCLOSE_TRUE = AUTOCLOSE + "=1";

    @NonNull
    private ProtocolService protocolService;

    @NonNull
    private JavaMailSender mailSender;

    @NonNull
    private AttachmentHandler attachmentHandler;

    @NonNull
    private IssueHandler issueHandler;

    @NonNull
    private IssueDao issueDao;

    @NonNull
    private AppConfig appConfig;

    @NonNull
    private Validator validator;

    @NonNull
    private ITextService iTextService;

    @NonNull
    private LinkUtils linkUtils;

    private Protocol redmineProtocol;

    private static final String FOOTER =
        "Generiert mit Code von https://github.com/psicho2000/redmine-protocol. Nutzt iText, lizensiert unter AGPL 3.0.";

    @PostConstruct
    private void init() {
        redmineProtocol = appConfig.getRedmine().getProtocol();
    }

    @RequestMapping("/")
    public String info() {
        // FIXME read tracker type and mandatory fields from configuration
        String body =
            "Erzeugt ein Protokoll für ein Redmine Protokoll-Ticket.<br /><br />" + "Verwendung: /protocol/{protocolId}<br />"
                + "Gegeben:<br /><ul><li>Offenes Ticket vom Tracker \"Protokoll\"<li>Titel beliebig"
                + "<li>Beginn Datum = Tag des Treffens (muss übereinstimmen mit dem Zeitstempel der Ticket-Notizen)"
                + "<li>Pflichtfelder: Zugewiesen an, Andacht, Anwesend, Essen, Ort, Nummer, Moderation</ul>";
        return wrapHtml(body);
    }

    @RequestMapping("/protocol/{issueId}")
    public String createProtocol(@PathVariable String issueId,
        @RequestParam(name = AUTOCLOSE, defaultValue = "false") boolean autoclose) {

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
            iTextService.finalizeITextile(FOOTER);

            if (autoclose) {
                closeProtocol(protocol);
                sendProtocol(protocol, attachedFiles);
            }

            responseInfo = ResponseInfo.builder().issueId(issueId).isoDate(isoDate).statusJournals(statusJournals)
                .topJournals(topJournals).attachedFiles(attachedFiles).build();
        } catch (ValidationException | IssueProcessingException ex) {
            exception = ex;
            log.error(ex.getMessage());
        } catch (Exception ex) {
            exception = ex;
            log.error(ExceptionUtils.getStackTrace(ex));
        }

        return createResponse(responseInfo, exception, autoclose);
    }

    @RequestMapping(value = "/protocol/{issueId}/preview", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> previewProtocol(@PathVariable String issueId) throws IOException {
        int ticketId = Integer.parseInt(issueId);
        Issue protocol = issueHandler.getIssue(ticketId);
        Date protocolStartDate = protocol.getStartDate();

        File pdfFile = new File(getProtocolPath(protocolStartDate));
        FileSystemResource fileSystemResource = new FileSystemResource(pdfFile);
        InputStream inputStream = fileSystemResource.getInputStream();
        byte[] pdf = IOUtils.toByteArray(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + getProtocolFileName(protocolStartDate));

        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    // application/pdf works for docx, aswell, but application/octet-stream is never working - strange
    @RequestMapping(value = "/protocol/{protocolId}/download/{issueId}/{fileName}.{extension}", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable String protocolId, @PathVariable Integer issueId,
        @PathVariable String fileName, @PathVariable String extension) throws IOException {

        String fullFilename = fileName + "." + extension;
        byte[] binaryAttachment = attachmentHandler.getAttachment(issueId, fullFilename);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fullFilename);

        return ResponseEntity.ok().headers(headers).body(binaryAttachment);
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
        StringBuilder result = new StringBuilder();

        if (thrownException instanceof ValidationException || thrownException instanceof IssueProcessingException) {
            result.append(thrownException.getMessage());
        } else if (thrownException != null) {
            result.append("<pre>");
            result.append(ExceptionUtils.getStackTrace(thrownException));
            result.append("</pre>");
        } else {
            result.append("Erzeuge Protokoll für Ticket: ");
            result.append(linkUtils.getShortLink(Integer.parseInt(responseInfo.getIssueId())));
            result.append("<br/>am ");
            result.append(responseInfo.getIsoDate());
            if (responseInfo.getStatusJournals().size() > 0) {
                result.append("<br/><h3>Status: ");
                result.append(responseInfo.getStatusJournals().size());
                result.append("</h3> ");
                result.append(augmentIssueInfo(responseInfo.getStatusJournals(), responseInfo.getAttachedFiles()));
            }
            result.append("<br/><h3>TOP: ");
            result.append(responseInfo.getTopJournals().size());
            result.append("</h3> ");
            result.append(augmentIssueInfo(responseInfo.getTopJournals(), responseInfo.getAttachedFiles()));
            result.append("<p><a href=\"");
            result.append(getCurrentPath());
            result.append("/preview\">PDF Vorschau</a></p>");
            result.append("<p><br/>Protokoll wurde ");
            result.append(autoclose ? "<strong>geschlossen</strong>." : "<strong>nicht</strong> geschlossen.");
            if (!autoclose) {
                result.append(format("<br/>Protokoll schließen mit Parameter: <a href=\"%1$s?%2$s\">%2$s</a>", getCurrentPath(),
                    AUTOCLOSE_TRUE));
            }
        }

        return wrapHtml(result.toString());
    }

    private String getCurrentPath() {
        return ServletUriComponentsBuilder.fromCurrentRequest().build().toString();
    }

    private StringBuilder augmentIssueInfo(List<IssueJournalWrapper> issueJournals, Set<AttachedFile> attachedFiles) {
        StringBuilder result = new StringBuilder();
        for (IssueJournalWrapper issueJournal : issueJournals) {
            Integer issueId = issueJournal.getIssueId();
            result.append("<br/>").append(linkUtils.getShortLink(issueId)).append(": ").append(issueJournal.getIssueSubject());

            Set<AttachedFile> filteredFiles =
                attachedFiles.stream().filter(file -> file.getIssueId().equals(issueId)).collect(Collectors.toSet());
            if (!filteredFiles.isEmpty()) {
                result.append("<ul>");
                for (AttachedFile file : filteredFiles) {
                    String fileName = file.getFileName();
                    result.append("<li><a href=\"").append(getCurrentPath()).append("/download/").append(issueId).append("/")
                        .append(fileName).append("\">").append(fileName).append("</a>");
                }
                result.append("</ul>");
            }
        }
        return result;
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
            String linkToProtocol = linkUtils.getLongLink(protocol.getId());
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

    private String wrapHtml(String body) {
        String style = ResourceUtils.readResource("style.css");
        return format("<!DOCTYPE html><html><head><style>%s</style></head><body>%s</body><footer>%s</footer></html>", style, body,
            FOOTER);
    }

}
