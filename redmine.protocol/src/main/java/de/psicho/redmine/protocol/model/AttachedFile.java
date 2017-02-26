package de.psicho.redmine.protocol.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachedFile {

    Integer issueId;
    String fileName;
}
