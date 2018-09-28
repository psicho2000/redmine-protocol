package de.psicho.redmine.protocol.model;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Validation {

    @Getter
    private List<String> messages = new ArrayList<>();

    private String linkToProtocol;

    public Validation(String linkToProtocol) {
        this.linkToProtocol = linkToProtocol;
    }

    public void add(String msg) {
        messages.add(msg);
    }

    public void add(Validation other) {
        other.getMessages().forEach(this::add);
    }

    public String render() {
        StringBuilder output = new StringBuilder();

        if (linkToProtocol != null) {
            output.append(format("Fehler für Ticket %s:<br />", linkToProtocol));
        }
        output.append("<ol>");

        for (String line : messages) {
            output.append("<li>").append(line).append("</li>");
        }

        return output.append("</ol>").toString();
    }

    public boolean isNotEmpty() {
        return messages.size() != 0;
    }
}
