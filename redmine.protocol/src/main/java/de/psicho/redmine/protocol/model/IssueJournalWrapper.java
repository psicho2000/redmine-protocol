package de.psicho.redmine.protocol.model;

import com.taskadapter.redmineapi.bean.Journal;

import lombok.Data;

@Data
public class IssueJournalWrapper {

    Integer issueId;
    String issueSubject;
    Journal journal;
    Integer assignee;
}
