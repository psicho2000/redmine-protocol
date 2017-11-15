package de.psicho.redmine.protocol.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import de.psicho.redmine.protocol.api.JournalHandler;
import de.psicho.redmine.protocol.model.IssueJournalWrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IssueDao {

    @NonNull
    private JdbcTemplate jdbcTemplate;

    @NonNull
    private JournalHandler journalHandler;

    public List<IssueJournalWrapper> findJournals(String tracker, String changeDate) {
        String sql = "SELECT issues.id, issues.subject, issues.assigned_to_id, journals.id FROM journals "
            + "INNER JOIN issues ON journals.journalized_id=issues.id INNER JOIN trackers ON issues.tracker_id=trackers.id "
            + "WHERE journalized_type='Issue' AND trackers.name= ? "
            + "AND journals.notes <> '' AND SUBSTRING(journals.created_on,1,10)= ?";

        Object[] args = new Object[] { tracker, changeDate };
        return jdbcTemplate.query(sql, args, journalHandler::retrieveJournal);
    }

    public Integer getFirstNonEmptyJournalByIssueId(Integer issueId) {
        String sql = "SELECT journals.id FROM journals INNER JOIN issues ON issues.id=journals.journalized_id "
            + "WHERE issues.id = ? AND journals.notes <> '' ORDER BY journals.id ASC LIMIT 0,1";

        Object[] args = new Object[] { issueId };
        return jdbcTemplate.queryForObject(sql, args, new SingleColumnRowMapper<>(Integer.class));
    }
}
