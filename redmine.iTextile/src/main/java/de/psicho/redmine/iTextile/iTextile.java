package de.psicho.redmine.iTextile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import de.psicho.redmine.iTextile.command.Command;
import de.psicho.redmine.iTextile.command.Paragraph;
import de.psicho.redmine.iTextile.command.Table;
import de.psicho.redmine.iTextile.command.TextProperty;

public class iTextile {

    private String filename;
    private List<Command> commands;
    private Table table;

    public iTextile(String filename) {
        this.filename = filename;
        this.table = null;
        this.commands = new ArrayList<>();
    }

    /**
     * <p>Prints text as new paragraph
     * 
     * @param text the text to print
     * @param size size of the text
     * @param bold flag whether the text shall be bold
     * @param color color of the text
     * @throws IllegalStateException if in table mode
     */
    public void addParagraph(String text, TextProperty property) {
        if (isTableMode()) {
            throw new IllegalStateException("Cannot add paragraph in table mode.");
        }

        Paragraph paragraph = new Paragraph(text, property);
        commands.add(paragraph);
    }

    /**
     * @param columns number of columns the table will be created with
     * @throws IllegalStateException if already in table mode
     * @throws IllegalArgumentException if number of columns <= 0
     */
    public void startTable(int columns) {
        if (isTableMode()) {
            throw new IllegalStateException("Already in table mode, cannot start again.");
        }

        table = new Table(columns);
    }

    /**
     * @param cells an input for each cell
     * @throws IllegalStateException if not in table mode
     * @throws IllegalArgumentException if number of cells != number of columns
     */
    public void addTableRow(String... cells) {
        if (!isTableMode()) {
            throw new IllegalStateException("Not in table mode, cannot add row.");
        }

        table.addRow(Arrays.asList(cells));
    }

    /**
     * @throws IllegalStateException if not in table mode
     */
    public void endTable() {
        if (!isTableMode()) {
            throw new IllegalStateException("Not in table mode, cannot end table.");
        }

        commands.add(table);
        table = null;
    }

    public void createFile() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
        } catch (FileNotFoundException | DocumentException ex) {
            ex.printStackTrace();
        }
        document.open();
        commands.forEach(command -> command.process(document));
        document.close();
    }

    private boolean isTableMode() {
        return table != null;
    }
}
