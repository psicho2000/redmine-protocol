package de.psicho.redmine.protocol.model;

import com.taskadapter.redmineapi.bean.Journal;

public class IssueJournalWrapper {
    Integer issueId;
    Journal journal;

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }
}
