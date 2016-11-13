package de.psicho.redmine.protocol.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.taskadapter.redmineapi.bean.Journal;
import com.taskadapter.redmineapi.bean.JournalFactory;

import de.psicho.redmine.protocol.model.IssueJournalWrapper;

@Repository
public class TopDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<IssueJournalWrapper> findJournals(String changeDate) {

        String sql = "select distinct issues.id from journals "
                + "inner join issues on journals.journalized_id=issues.id "
                + "inner join trackers on issues.tracker_id=trackers.id "
                + "where journalized_type='Issue' and trackers.name='TOP' and "
                + "substring(journals.created_on,1,10)= ? " + "UNION " + "select distinct issues.id from issues "
                + "inner join trackers on issues.tracker_id=trackers.id " + "where trackers.name='TOP' and "
                + "substring(issues.created_on,1,10)= ?";

        List<IssueJournalWrapper> issueJournals = jdbcTemplate.query(sql, new Object[] { changeDate, changeDate },
                (rs, rownum) -> {
                    IssueJournalWrapper issueJournal = new IssueJournalWrapper();
                    // FIXME continue here
                    return issueJournal;
                });

        // TODO set this block into the above lambda
        IssueJournalWrapper issueJournal = new IssueJournalWrapper();
        Journal journal = JournalFactory.create(1);
        journal.setCreatedOn(null); // FIXME
        journal.setNotes(null); // FIXME
        issueJournal.setIssueId(1); // FIXME
        issueJournal.setJournal(journal);

        return issueJournals;
    }
}
