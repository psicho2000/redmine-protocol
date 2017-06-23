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

        String sql = "SELECT issues.id, issues.subject, issues.assigned_to_id, journals.id FROM journals "
            + "INNER JOIN issues ON journals.journalized_id=issues.id INNER JOIN trackers ON issues.tracker_id=trackers.id "
            + "WHERE journalized_type='Issue' AND trackers.name='Aufgabe' "
            + "AND journals.notes <> '' AND SUBSTRING(journals.created_on,1,10)= ? GROUP BY issues.id";

        Object[] args = new Object[] { changeDate };

        return jdbcTemplate.query(sql, args, journalHandler::retrieveJournal);
    }
}
