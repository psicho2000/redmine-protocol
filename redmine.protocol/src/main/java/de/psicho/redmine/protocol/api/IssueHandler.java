package de.psicho.redmine.protocol.api;

import java.util.List;

import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;

@Component
public class IssueHandler {
    IssueManager issueMgr = null;
    List<IssueStatus> statuses = null;

    public IssueHandler(RedmineHandler redmineHandler) {
        issueMgr = redmineHandler.getRedmineManager().getIssueManager();
    }

    public Issue getIssue(Integer issueId) {
        Issue issue = null;

        try {
            issue = issueMgr.getIssueById(issueId);
        } catch (RedmineException re) {
            re.printStackTrace();
        }

        return issue;
    }

    public void updateIssue(Issue issue) {
        try {
            issueMgr.update(issue);
        } catch (RedmineException re) {
            re.printStackTrace();
        }
    }

    public Integer getStatusByName(String statusName) {
        try {
            if (statuses == null) {
                statuses = issueMgr.getStatuses();
            }
            for (IssueStatus status : statuses) {
                if (status.getName().equals(statusName)) {
                    return status.getId();
                }
            }
        } catch (RedmineException re) {
            re.printStackTrace();
        }
        return null;
    }
}
