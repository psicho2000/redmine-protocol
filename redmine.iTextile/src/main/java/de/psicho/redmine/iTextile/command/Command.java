package de.psicho.redmine.iTextile.command;

import com.itextpdf.text.Document;

import de.psicho.redmine.iTextile.ProcessingException;

public interface Command {

    /**
     * @param document the itext document the command is to be added to
     * @throws ProcessingException if anything goes wrong
     */
    public void process(Document document);
}
