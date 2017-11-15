package de.psicho.redmine.iTextile.command;

import static de.psicho.redmine.iTextile.command.Constants.STYLESHEET;

import java.io.IOException;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import de.psicho.redmine.iTextile.ProcessingException;
import de.psicho.redmine.iTextile.utils.ResourceUtils;
import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.markup.Dialect;

public class Paragraph implements Command {

    private String text;
    private TextProperty property;
    private Dialect dialect;

    public Paragraph(String text, TextProperty textProperty) {
        this.text = text;
        this.property = textProperty;
    }

    public Paragraph(String text, Dialect dialect) {
        this.text = text;
        this.dialect = dialect;
    }

    @Override
    public void process(Document document) throws ProcessingException {
        if (dialect != null) {
            processWithDialect(document);
        } else if (property != null) {
            processWitFormat(document);
        } else {
            throw new ProcessingException("Dialect and formatting not set. Provide exactly one!");
        }
    }

    private void processWithDialect(Document document) {
        com.itextpdf.text.Paragraph paragraph = new com.itextpdf.text.Paragraph();
        String htmlContent = new MarkupParser(dialect).parseToHtml(text);
        String css = ResourceUtils.readResource(STYLESHEET);

        try {
            ElementList list = XMLWorkerHelper.parseToElementList(htmlContent, css);
            paragraph.addAll(list);
            document.add(paragraph);
        } catch (IOException | DocumentException ex) {
            throw new ProcessingException(ex);
        }
    }

    private void processWitFormat(Document document) {
        Font font = new Font(property.getFont(), property.getSize(), property.getStyle(), property.getColor());
        Chunk chunk = new Chunk(text, font);
        com.itextpdf.text.Paragraph paragraph = new com.itextpdf.text.Paragraph(chunk);
        paragraph.setAlignment(property.getAlignment());
        paragraph.setSpacingAfter(8.0f);
        try {
            document.add(paragraph);
        } catch (DocumentException ex) {
            throw new ProcessingException(ex);
        }
    }

}
