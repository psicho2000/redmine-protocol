package de.psicho.redmine.protocol.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.taskadapter.redmineapi.bean.Journal;

@Repository
public class StatusDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Journal> findJournals(String changeDate) {

        String sql = "select issues.id as issueId, issues.subject, journals.id as journalId, journals.notes "
                + "from journals " + "inner join issues on journals.journalized_id=issues.id "
                + "inner join trackers on issues.tracker_id=trackers.id "
                + "where journalized_type='Issue' and trackers.name='Aufgabe' "
                + "and substring(journals.created_on,1,10)= ?";

        List<Journal> journals = jdbcTemplate.query(sql, new Object[] { changeDate },
                new BeanPropertyRowMapper<Journal>(Journal.class));

        return journals;
    }
}
