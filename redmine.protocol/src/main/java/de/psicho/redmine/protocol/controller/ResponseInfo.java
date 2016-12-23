package de.psicho.redmine.protocol.controller;

import java.util.List;

import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseInfo {

    private String issueId;
    private String isoDate;
    private List<IssueJournalWrapper> statusJournals;
    private List<IssueJournalWrapper> topJournals;
}
