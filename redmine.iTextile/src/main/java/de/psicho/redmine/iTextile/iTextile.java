package de.psicho.redmine.iTextile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import de.psicho.redmine.iTextile.command.Command;
import de.psicho.redmine.iTextile.command.Paragraph;
import de.psicho.redmine.iTextile.command.Table;
import de.psicho.redmine.iTextile.command.TextProperty;
import net.java.textilej.parser.markup.Dialect;

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
     * @param columns number of columns the table will be created with
     * @param border Flag of type {@link Rectangle} indicating border style
     * @throws IllegalStateException if already in table mode
     * @throws IllegalArgumentException if number of columns <= 0
     */
    public void startTable(int columns, int border) {
        if (isTableMode()) {
            throw new IllegalStateException("Already in table mode, cannot start again.");
        }

        table = new Table(columns, border);
    }

    /**
     * <p>Sets the formatting for the given column
     * 
     * @param colNum zero based number of the column
     * @param formatting formatting for the column
     * @throws IllegalStateException if not in table mode
     * @throws IndexOutOfBoundsException if colNum < 0 or colNum >= number of columns
     */
    public void setTableColumnFormat(int colNum, TextProperty formatting) {
        if (!isTableMode()) {
            throw new IllegalStateException("Not in table mode, cannot add row.");
        }

        table.setColumnFormat(colNum, formatting);
    }

    /**
     * <p>Sets the parser dialect for the given column
     * 
     * @param colNum zero based number of the column
     * @param dialect dialect for the column
     * @throws IllegalStateException if not in table mode
     * @throws IndexOutOfBoundsException if colNum < 0 or colNum >= number of columns
     */
    public void setTableColumnParser(int colNum, Dialect dialect) {
        if (!isTableMode()) {
            throw new IllegalStateException("Not in table mode, cannot add row.");
        }

        table.setColumnDialect(colNum, dialect);
    }

    /**
     * <p>Sets the width in pixels (based on 72 dpi) for the given column. If width is not defined for a column, remaining width
     * (based on iText default width of 523 pixels) will be evenly distributed among the non defined columns.
     * 
     * @param colNum zero based number of the column
     * @param width width for the column
     * @throws IllegalStateException if not in table mode
     * @throws IndexOutOfBoundsException if colNum < 0 or colNum >= number of columns
     */
    public void setTableColumnWidth(int colNum, float width) {
        if (!isTableMode()) {
            throw new IllegalStateException("Not in table mode, cannot add row.");
        }

        table.setColumnWidth(colNum, width);
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
     * @param backgroundColor background color for the row
     * @param cells an input for each cell
     * @throws IllegalStateException if not in table mode
     * @throws IllegalArgumentException if number of cells != number of columns
     */
    public void addTableRow(BaseColor backgroundColor, String... cells) {
        if (!isTableMode()) {
            throw new IllegalStateException("Not in table mode, cannot add row.");
        }

        table.addRow(Arrays.asList(cells), backgroundColor);
    }

    /**
     * <p>Sets a header. If not set, header is inserted as first row. If already set, old header is overwritten.
     * 
     * @param formatting formatting of each cell of the header row
     * @param backgroundColor background color of the header row
     * @param cells contents for each cell of the header row
     * @throws IllegalArgumentException if number of cells != number of columns
     */
    public void setTableHeader(TextProperty formatting, BaseColor backgroundColor, String... cells) {
        if (!isTableMode()) {
            throw new IllegalStateException("Not in table mode, cannot add row.");
        }

        table.setHeader(Arrays.asList(cells), formatting, backgroundColor);
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

    /**
     * <p>Creates the pdf file and processes all provided Commands as input
     * 
     * @throws DocumentCreationException when file could not be created
     * @throws ProcessingException when input could not be processed
     */
    public void createFile() throws DocumentCreationException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
        } catch (FileNotFoundException | DocumentException ex) {
            throw new DocumentCreationException(ex);
        }
        document.open();
        commands.forEach(command -> command.process(document));
        document.close();
    }

    private boolean isTableMode() {
        return table != null;
    }
}
