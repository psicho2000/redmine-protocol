package de.psicho.redmine.protocol.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.psicho.redmine.protocol.db.model.DbAttachment;

@Repository
public interface AttachmentRepository extends CrudRepository<DbAttachment, Long> {

    DbAttachment findByContainerIdAndFilename(Integer containerId, String filename);
}
