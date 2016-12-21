package de.psicho.redmine.protocol.api;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.AttachmentManager;
import com.taskadapter.redmineapi.RedmineException;

@Component
public class AttachmentHandler {

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
}
