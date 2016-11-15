package de.psicho.redmine.iTextile.command;

import com.itextpdf.text.Document;

public interface Command {
    public void process(Document document);
}
