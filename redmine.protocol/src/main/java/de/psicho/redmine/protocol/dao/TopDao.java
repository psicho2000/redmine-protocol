package de.psicho.redmine.protocol.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import de.psicho.redmine.protocol.api.JournalHandler;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;

@Repository
public class TopDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JournalHandler journalHandler;

    public List<IssueJournalWrapper> findJournals(String changeDate) {

        String sql = "select issues.id, issues.subject, journals.id from journals "
                + "inner join issues on journals.journalized_id=issues.id "
                + "inner join trackers on issues.tracker_id=trackers.id "
                + "where journalized_type='Issue' and trackers.name='TOP' "
                + "and substring(journals.created_on,1,10)= ? and journals.notes is not null "
                + "UNION select distinct issues.id, issues.subject, 0 from issues "
                + "inner join trackers on issues.tracker_id=trackers.id "
                + "where trackers.name='TOP' and substring(issues.created_on,1,10)= ?";
        Object[] args = new Object[] { changeDate, changeDate };

        return jdbcTemplate.query(sql, args, journalHandler::retrieveJournal);
    }

}
