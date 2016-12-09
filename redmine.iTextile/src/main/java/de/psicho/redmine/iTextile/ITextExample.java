package de.psicho.redmine.iTextile;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

public class ITextExample {

    private static String LOREM_IPSUM =
        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    /**
     * @author Bruno Lowagie (iText Software)
     */
    public void createPdf(String filename) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        Paragraph paragraph1 = new Paragraph("First paragraph" + LOREM_IPSUM);
        Paragraph paragraph2 = new Paragraph("Second paragraph" + LOREM_IPSUM);
        paragraph2.setSpacingBefore(40f);
        paragraph1.setAlignment(Element.ALIGN_JUSTIFIED);

        List numberedList = new List(true, 20);
        numberedList.add(new ListItem("First line"));
        numberedList.add(new ListItem(
            "The second line is longer to see what happens once the end of the line is reached. Will it start on a new line?"));
        numberedList.add(new ListItem("Third line"));

        List bulletList = new List(false, 20);
        bulletList.add(new ListItem("This is an item"));
        bulletList.add("This is another item");

        Font f1 = new Font(FontFamily.HELVETICA, 11.0f, Font.NORMAL, BaseColor.BLACK);
        Font f2 = new Font(FontFamily.HELVETICA, 11.0f, Font.BOLD, BaseColor.BLACK);
        Chunk chunk1 = new Chunk("Starter ", f1);
        Paragraph coloredElement = new Paragraph(chunk1);
        Chunk chunk2 = new Chunk("(Bold yellow string)", f2);
        chunk2.setBackground(BaseColor.YELLOW);
        coloredElement.add(chunk2);
        Chunk chunk3 = new Chunk(" ender", f1);
        coloredElement.add(chunk3);
        List coloredList = new List(false, 20);
        coloredList.setListSymbol("\u2022");
        ListItem listItem = new ListItem();
        listItem.add(chunk1);
        listItem.add(chunk2);
        listItem.add(chunk3);
        coloredList.add(listItem);

        Font anchorFont = new Font(FontFamily.HELVETICA, 11.0f, Font.UNDERLINE, BaseColor.BLUE);
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Phrase("Link zum Ticket: "));
        Chunk anchorChunk = new Chunk("#463", anchorFont);
        Anchor anchor = new Anchor(anchorChunk);
        anchor.setReference("http://redmine.lifeline-herne.de/issues/463");
        paragraph.add(anchor);
        document.add(paragraph);

        document.add(coloredElement);
        document.add(coloredList);
        document.add(numberedList);
        document.add(bulletList);
        document.add(paragraph1);
        document.add(paragraph2);

        document.close();
    }
}
