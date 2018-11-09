package de.psicho.redmine.protocol.config;

import lombok.Data;

import java.util.List;

@Data
public class Mail {

    private List<String> recipients;
    private String subject;
    private String body;
}
