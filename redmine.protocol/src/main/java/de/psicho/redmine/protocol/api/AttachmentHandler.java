package de.psicho.redmine.protocol.api;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.AttachmentManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Attachment;

import de.psicho.redmine.protocol.controller.IssueProcessingException;
import de.psicho.redmine.protocol.db.model.DbAttachment;
import de.psicho.redmine.protocol.repository.AttachmentRepository;
import de.psicho.redmine.protocol.utils.LinkUtils;

@Component
public class AttachmentHandler {

    // we have to use Autowired as we want to use a specific constructor
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private LinkUtils linkUtils;

    private static final String CONTENT_TYPE = "application/pdf";

    private AttachmentManager attachmentManager = null;

    public AttachmentHandler(RedmineHandler redmineHandler) {
        attachmentManager = redmineHandler.getRedmineManager().getAttachmentManager();
    }

    public void addAttachment(Integer issueId, File attachment) {
        try {
            attachmentManager.addAttachmentToIssue(issueId, attachment, CONTENT_TYPE);
        } catch (RedmineException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public byte[] getAttachment(Integer issueId, String attachmentFileName) {
        try {
            DbAttachment dbAttachment = attachmentRepository.findByContainerIdAndFilename(issueId, attachmentFileName);
            if (dbAttachment == null) {
                throw new IssueProcessingException(format("Für das Ticket %s wurde der Anhang '%s' nicht gefunden.",
                    linkUtils.getShortLink(issueId), attachmentFileName));
            }
            Attachment attachment = attachmentManager.getAttachmentById(dbAttachment.getId());
            return attachmentManager.downloadAttachmentContent(attachment);
        } catch (RedmineException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
