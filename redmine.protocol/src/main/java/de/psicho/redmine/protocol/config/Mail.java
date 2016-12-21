package de.psicho.redmine.protocol.config;

import lombok.Data;

@Data
public class Mail {

    private String recipient;
    private String subject;
    private String body;
}
