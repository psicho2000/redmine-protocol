package de.psicho.redmine.protocol.api;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalFactory;

import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JournalHandler {

    @NonNull
    private IssueHandler issueHandler;

    public IssueJournalWrapper retrieveJournal(ResultSet rs, int rownum) {
        IssueJournalWrapper result = new IssueJournalWrapper();

        try {
            Integer issueId = rs.getInt(1);
            String subject = rs.getString(2);
            Integer assignee = rs.getInt(3);
            Integer journalId = rs.getInt(4);
            Journal journal = journalId == 0 ? null : getJournal(issueId, journalId);
            result.setIssueId(issueId);
            result.setIssueSubject(subject);
            result.setJournal(journal);
            result.setAssignee(assignee);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    private Journal getJournal(Integer issueId, Integer journalId) {
        Journal result = JournalFactory.create(journalId);

        Issue issue = issueHandler.getIssue(issueId, Include.journals);
        if (issue != null && issue.getJournals() != null) {
            for (Journal journal : issue.getJournals()) {
                Integer currentJournalId = journal.getId();
                if (currentJournalId != null && currentJournalId.equals(journalId)) {
                    result = journal;
                }
            }
        }

        return result;
    }
}
