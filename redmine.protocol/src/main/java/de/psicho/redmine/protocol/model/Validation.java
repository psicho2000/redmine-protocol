package de.psicho.redmine.protocol.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Validation {

    @Getter
    List<String> messages = new ArrayList<>();

    public void add(String msg) {
        messages.add(msg);
    }

    public void add(Validation other) {
        other.getMessages().forEach(this::add);
    }

    public String render() {
        StringBuilder output = new StringBuilder("<ol>");

        for (String line : messages) {
            output.append("<li>").append(line).append("</li>");
        }

        return output.append("</ol>").toString();
    }

    public boolean isNotEmpty() {
        return messages.size() != 0;
    }
}
