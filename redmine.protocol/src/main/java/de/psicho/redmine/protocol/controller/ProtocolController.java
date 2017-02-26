package de.psicho.redmine.protocol.controller;

import static de.psicho.redmine.protocol.service.ProtocolService.getProtocolFileName;
import static de.psicho.redmine.protocol.service.ProtocolService.getProtocolPath;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskadapter.redmineapi.bean.Issue;

import de.psicho.redmine.protocol.api.AttachmentHandler;
import de.psicho.redmine.protocol.api.IssueHandler;
import de.psicho.redmine.protocol.config.AppConfig;
import de.psicho.redmine.protocol.config.Mail;
import de.psicho.redmine.protocol.config.Protocol;
import de.psicho.redmine.protocol.dao.StatusDao;
import de.psicho.redmine.protocol.dao.TopDao;
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
    private StatusDao statusDao;

    @Autowired
    private TopDao topDao;

    @Autowired
    private AppConfig appConfig;

    private Protocol redmineProtocol;

    @Autowired
    Validator validator;

    @Autowired
    ITextService iTextService;

    @PostConstruct
    private void init() {
        redmineProtocol = appConfig.getRedmine().getProtocol();
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

            List<IssueJournalWrapper> statusJournals = statusDao.findJournals(isoDate);
            iTextService.processStatus(protocol, statusJournals);

            List<IssueJournalWrapper> topJournals = topDao.findJournals(isoDate);
            topJournals = protocolService.filterTopJournals(topJournals);
            iTextService.processTop(topJournals);

            iTextService.endTable();
            iTextService.finalizeITextile();

            if (autoclose) {
                closeProtocol(protocol);
            }
            sendProtocol(protocol);

            responseInfo = ResponseInfo.builder().issueId(issueId).isoDate(isoDate).statusJournals(statusJournals)
                .topJournals(topJournals).build();
        } catch (Exception ex) {
            exception = ex;
            log.error(ExceptionUtils.getStackTrace(ex));
        }

        return createResponse(responseInfo, exception, autoclose);
    }

    private String createResponse(ResponseInfo responseInfo, Exception thrownException, boolean autoclose) {

        StringBuffer result = new StringBuffer();

        if (thrownException != null) {
            result.append("<pre>");
            result.append(ExceptionUtils.getStackTrace(thrownException));
            result.append("</pre>");
        } else {
            Consumer<IssueJournalWrapper> issueInfoAppender =
                issue -> result.append("<br>" + issue.getIssueId() + ": " + issue.getIssueSubject());

            result.append("Creating protocol for id: ");
            result.append(responseInfo.getIssueId());
            result.append("<br>Querying for date ");
            result.append(responseInfo.getIsoDate());
            result.append("<br><h3>StatusItems: ");
            result.append(responseInfo.getStatusJournals().size());
            result.append("</h3> ");
            responseInfo.getStatusJournals().forEach(issueInfoAppender);
            result.append("<br><h3>TopItems: ");
            result.append(responseInfo.getTopJournals().size());
            result.append("</h3> ");
            responseInfo.getTopJournals().forEach(issueInfoAppender);
            result.append("<p><br>Protocol has ");
            result.append(autoclose ? "been <strong>closed</strong>." : "<strong>not</strong> been closed.");
        }

        return result.toString();
    }

    private void closeProtocol(Issue protocol) {
        Date protocolStartDate = protocol.getStartDate();
        StringBuilder subject = new StringBuilder();
        subject.append("Gemeinderat ");
        subject.append(protocolService.getProtocolValue(protocol, redmineProtocol.getFields().getNumber()));
        subject.append(" am ");
        subject.append(DateUtils.dateToGer(protocolStartDate));
        protocol.setSubject(subject.toString());
        protocol.setStatusId(issueHandler.getStatusByName(redmineProtocol.getClosed()));
        File attachment = new File(getProtocolPath(protocolStartDate));
        attachmentHandler.addAttachment(protocol.getId(), attachment);
        issueHandler.updateIssue(protocol);
    }

    private void sendProtocol(Issue protocol) {
        Date protocolStartDate = protocol.getStartDate();
        Mail mailConfig = appConfig.getRedmine().getMail();
        MimeMessage message = mailSender.createMimeMessage();
        FileSystemResource file = new FileSystemResource(new File(getProtocolPath(protocolStartDate)));

        MimeMessageHelper helper;
        try {
            String dateGer = DateUtils.dateToGer(protocolStartDate);
            String linkToProtocol = appConfig.getRedmine().getIssues().getLink() + protocol.getId();
            linkToProtocol = String.format("<a href=\"%s\">%s</a>", linkToProtocol, linkToProtocol);
            helper = new MimeMessageHelper(message, true);
            helper.setTo(mailConfig.getRecipient());
            helper.setSubject(String.format(mailConfig.getSubject(), dateGer));
            helper.setText(String.format("<html><body>" + mailConfig.getBody() + "</body></html>", dateGer, linkToProtocol),
                true);
            helper.addAttachment(getProtocolFileName(protocolStartDate), file);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

        mailSender.send(message);
    }
}
