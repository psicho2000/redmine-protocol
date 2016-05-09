package de.psicho.redmine.protocol.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueStatus;

import de.psicho.redmine.protocol.config.AppConfig;

@Component
public class IssueDao {
    @Autowired
    AppConfig appConfig;

    RedmineManager redmineMgr = null;
    IssueManager issueMgr = null;
    List<IssueStatus> statuses = null;

    private void init() {
        if (redmineMgr == null) {
            redmineMgr = RedmineManagerFactory.createWithApiKey(appConfig.getRedmineApiUrl(),
                    appConfig.getRedmineApiAccesskey());
        }
        if (issueMgr == null) {
            issueMgr = redmineMgr.getIssueManager();
        }
    }

    public Issue getIssue(Integer issueId) {
        Issue issue = null;

        init();

        try {
            issue = issueMgr.getIssueById(issueId);
        } catch (RedmineException re) {
            re.printStackTrace();
        }

        return issue;
    }

    public void updateIssue(Issue issue) {
        init();

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
