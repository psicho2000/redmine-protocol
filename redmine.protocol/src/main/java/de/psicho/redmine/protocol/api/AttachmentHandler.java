package de.psicho.redmine.protocol.api;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.AttachmentManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Attachment;

import de.psicho.redmine.protocol.db.model.DbAttachment;
import de.psicho.redmine.protocol.repository.AttachmentRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AttachmentHandler {

    private static final String CONTENT_TYPE = "application/pdf";

    private AttachmentManager attachmentManager = null;

    @NonNull
    private AttachmentRepository attachmentRepository;

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
            Attachment attachment = attachmentManager.getAttachmentById(dbAttachment.getId());
            return attachmentManager.downloadAttachmentContent(attachment);
        } catch (RedmineException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
