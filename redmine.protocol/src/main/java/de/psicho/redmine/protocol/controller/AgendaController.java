package de.psicho.redmine.protocol.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgendaController {

    private final static String AGENDA_FILENAME = "results/Agenda.pdf";

    @RequestMapping("/agenda")
    public String createAgenda() {
        // startITextile(AGENDA_FILENAME); // TODO generalize startITextile
        // TODO create new protocol ticket
        // TODO create agenda.pdf
        return null;
    }
}
