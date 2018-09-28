package de.psicho.redmine.protocol.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Protocol {

    private List<String> mandatory = new ArrayList<String>();
    private List<String> members = new ArrayList<String>();
    private String name;
    private String closed;
    private Fields fields;
    private Switches switches;
}
