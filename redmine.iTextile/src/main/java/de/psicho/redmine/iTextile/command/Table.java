package de.psicho.redmine.iTextile.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * @author Markus
 */
public class Table implements Command {

    private static final float A4_WIDTH = 523f;
    private int columns;
    private LinkedList<Row> rows;
    private List<TextProperty> columnFormatting;
    private List<Float> columnWidths;
    private boolean headerSet;
    private int border;

    /**
     * <p>Creates a table with a visible border.
     * 
     * @param columns number of columns the table will be created with
     * @throws IllegalArgumentException if number of columns <= 0
     */
    public Table(int columns) {
        this(columns, Rectangle.BOX);
    }

    /**
     * @param columns number of columns the table will be created with
     * @param border Flag of type {@link Rectangle} indicating border style
     * @throws IllegalArgumentException if number of columns <= 0
     */
    public Table(int columns, int border) {
        if (columns <= 0) {
            throw new IllegalArgumentException(
                String.format("Number of columns must be greater 0. But %d was provided.", columns));
        }
        this.columns = columns;
        this.border = border;
        columnFormatting = new ArrayList<>();
        IntStream.range(0, columns).forEach(i -> columnFormatting.add(null));
        columnWidths = new ArrayList<>();
        IntStream.range(0, columns).forEach(i -> columnWidths.add(null));
        rows = new LinkedList<>();
        headerSet = false;
    }

    @Override
    public void process(Document document) {
        PdfPTable table = new PdfPTable(columns);
        setWidths(table);
        rows.forEach(row -> processRow(table, row));
        try {
            document.add(table);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
    }

    // http://developers.itextpdf.com/de/node/2399
    private void setWidths(PdfPTable table) {
        float spread = 0f;

        int numberOfSetWidths = columnWidths.stream().mapToInt(width -> width == null ? 0 : 1).sum();
        if (columns != numberOfSetWidths) {
            float freeSpace =
                columnWidths.stream().map(width -> Optional.ofNullable(width).orElse(0f)).reduce(A4_WIDTH, (a, b) -> a - b);
            spread = freeSpace / (columns - numberOfSetWidths);
        }

        float[] relativeWidths = ArrayUtils.toPrimitive(columnWidths.toArray(new Float[0]), spread);
        try {
            table.setTotalWidth(relativeWidths);
            table.setLockedWidth(true);
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
    }

    private void processRow(PdfPTable table, Row row) {
        row.getCells().forEach(cell -> processCell(table, cell, row.getBackgroundColor()));
    }

    private void processCell(PdfPTable table, Cell cell, BaseColor backgroundColor) {
        TextProperty formatting = cell.getFormatting();
        Chunk chunk;
        if (formatting != null) {
            Font font = new Font(formatting.getFont(), formatting.getSize(), formatting.getStyle(), formatting.getColor());
            chunk = new Chunk(cell.getContent(), font);
        } else {
            chunk = new Chunk(cell.getContent());
        }
        Phrase phrase = new Phrase(chunk);
        PdfPCell pdfCell = new PdfPCell(phrase);
        if (backgroundColor != null) {
            pdfCell.setBackgroundColor(backgroundColor);
        }
        if (formatting != null) {
            pdfCell.setHorizontalAlignment(formatting.getAlignment());
        }
        pdfCell.setBorder(border);
        table.addCell(pdfCell);
    }

    /**
     * @param cellsContent an input for each cell
     * @throws IllegalArgumentException if number of cells != number of columns
     */
    public void addRow(List<String> cellsContent) {
        addRow(cellsContent, null);
    }

    /**
     * @param cellsContent an input for each cell
     * @param backgroundColor background color for the row
     * @throws IllegalArgumentException if number of cells != number of columns
     */
    public void addRow(List<String> cellsContent, BaseColor backgroundColor) {
        int rowSize = cellsContent.size();
        if (rowSize != columns) {
            throw new IllegalArgumentException(
                String.format("Table has %d columns, but %d cells have been provided.", columns, rowSize));
        }

        List<Cell> cells = IntStream.range(0, rowSize).mapToObj(i -> new Cell(cellsContent.get(i), columnFormatting.get(i)))
            .collect(Collectors.toList());

        Row row = new Row(cells);
        row.setBackgroundColor(backgroundColor);
        rows.add(row);
    }

    /**
     * <p>Sets column formatting for the given column. A header will not be formatted. If rows were already added, format will be
     * applied to those rows aswell.
     * 
     * @param colNum zero based number of the column
     * @param formatting formatting for the columns cells
     * @throws IndexOutOfBoundsException if colNum < 0 or colNum >= number of columns
     */
    public void setColumnFormat(int colNum, TextProperty formatting) {
        if (colNum < 0 || colNum >= columns) {
            throw new IndexOutOfBoundsException(String.format("Given column num %d must be >= 0 and < %d", colNum, columns));
        }

        columnFormatting.set(colNum, formatting);

        Stream<Row> rowsStream = rows.stream();
        if (headerSet) {
            rowsStream = rowsStream.skip(1);
        }
        rowsStream.forEach(r -> r.getCells().get(colNum).setFormatting(formatting));
    }

    /**
     * <p>Sets the width in pixels (based on 72 dpi) for the given column. If width is not defined for a column, remaining width
     * (based on iText default width of 523 pixels) will be evenly distributed among the non defined columns.
     * 
     * @param colNum zero based number of the column
     * @param width width for the column
     * @throws IndexOutOfBoundsException if colNum < 0 or colNum >= number of columns
     */
    public void setColumnWidth(int colNum, float width) {
        if (colNum < 0 || colNum >= columns) {
            throw new IndexOutOfBoundsException(String.format("Given column num %d must be >= 0 and < %d", colNum, columns));
        }

        columnWidths.set(colNum, width);
    }

    /**
     * <p>Sets a header. If not set, header is inserted as first row. If already set, old header is overwritten.
     * 
     * @param formatting formatting of each cell of the header row
     * @param backgroundColor background color of the header row
     * @param cells contents for each cell of the header row
     * @throws IllegalArgumentException if number of cells != number of columns
     */
    public void setHeader(List<String> headerContents, TextProperty formatting, BaseColor backgroundColor) {
        int rowSize = headerContents.size();
        if (rowSize != columns) {
            throw new IllegalArgumentException(
                String.format("Table has %d columns, but %d cells have been provided.", columns, rowSize));
        }

        List<Cell> cells =
            headerContents.stream().map(cellContent -> new Cell(cellContent, formatting)).collect(Collectors.toList());

        if (headerSet) {
            Row header = rows.get(0);
            header.setCells(cells);
            header.setBackgroundColor(backgroundColor);
        } else {
            Row header = new Row(cells);
            header.setBackgroundColor(backgroundColor);
            rows.addFirst(header);
        }

        headerSet = true;
    }
}
