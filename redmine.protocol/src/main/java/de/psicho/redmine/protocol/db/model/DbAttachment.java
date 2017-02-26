package de.psicho.redmine.protocol.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;

@Entity
@Table(name = "attachments")
@Getter
public class DbAttachment {

    @Id
    @Column
    private Integer id;

    @Column
    private Integer containerId;

    @Column
    private String filename;
}
