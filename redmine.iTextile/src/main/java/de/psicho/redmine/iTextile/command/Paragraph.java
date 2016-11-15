package de.psicho.redmine.iTextile.command;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Paragraph implements Command {
    final private String text;
    final private TextProperty property;

    @Override
    public void process(Document document) {
        Font font = new Font(property.getFont(), property.getSize(), property.getStyle(), property.getColor());
        Chunk chunk = new Chunk(text, font);
        com.itextpdf.text.Paragraph paragraph = new com.itextpdf.text.Paragraph(chunk);
        paragraph.setAlignment(property.getAlignment());
        try {
            document.add(paragraph);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
    }

}
