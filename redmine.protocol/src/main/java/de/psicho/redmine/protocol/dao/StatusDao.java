package de.psicho.redmine.protocol.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.psicho.redmine.protocol.api.JournalHandler;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;

@Repository
public class StatusDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JournalHandler journalHandler;

    public List<IssueJournalWrapper> findJournals(String changeDate) {

        String sql = "select issues.id as issueId, issues.subject, journals.id as journalId from journals "
            + "inner join issues on journals.journalized_id=issues.id " + "inner join trackers on issues.tracker_id=trackers.id "
            + "where journalized_type='Issue' and trackers.name='Aufgabe' "
            + "and substring(journals.created_on,1,10)= ? and journals.notes is not null group by issues.id";

        Object[] args = new Object[] { changeDate };

        return jdbcTemplate.query(sql, args, journalHandler::retrieveJournal);
    }
}
