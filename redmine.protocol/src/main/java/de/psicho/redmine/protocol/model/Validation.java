package de.psicho.redmine.protocol.model;

import java.util.ArrayList;
import java.util.List;

public class Validation {
    List<String> messages = new ArrayList<String>();

    public void add(String msg) {
        messages.add(msg);
    }

    public String render() {
        StringBuilder output = new StringBuilder();

        for (String line : messages) {
            output.append("<li>" + line + "</li>");
        }

        return "<ol>" + output.toString() + "</ol>";
    }

    public boolean isEmpty() {
        return messages.size() == 0;
    }
}
