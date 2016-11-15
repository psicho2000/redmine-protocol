package de.psicho.redmine.iTextile.command;

import com.itextpdf.text.Document;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Paragraph implements Command {
    final private String text;
    final private TextProperty property;

    @Override
    public void process(Document document) {
        // TODO Auto-generated method stub

    }

}
