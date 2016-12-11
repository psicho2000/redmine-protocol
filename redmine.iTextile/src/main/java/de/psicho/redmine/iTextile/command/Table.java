package de.psicho.redmine.iTextile.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;

/**
 * @author Markus
 */
public class Table implements Command {

    private int columns;
    private LinkedList<Row> rows;
    private List<TextProperty> columnFormatting;
    private boolean headerSet;

    @Override
    public void process(Document document) {
        // TODO Auto-generated method stub
    }

    /**
     * @param columns number of columns the table will be created with
     * @throws IllegalArgumentException if number of columns <= 0
     */
    public Table(int columns) {
        if (columns <= 0) {
            throw new IllegalArgumentException(
                String.format("Number of columns must be greater 0. But %d was provided.", columns));
        }
        this.columns = columns;
        columnFormatting = new ArrayList<>();
        IntStream.range(0, columns).forEach(i -> columnFormatting.add(null));
        rows = new LinkedList<>();
        headerSet = false;
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
     * <p>Sets column formatting for the given column. A header will not be formatted.
     * 
     * @param colNum zero based number of the columns cells
     * @param formatting formatting for the columns cells
     * @throws IndexOutOfBoundsException if colNum < 0 or colNum >= number of columns
     */
    public void setColumnFormat(int colNum, TextProperty formatting) {
        if (colNum < 0 || colNum >= columns) {
            throw new IndexOutOfBoundsException(String.format("Given column num %d must be >= 0 and < %d", colNum, columns));
        }

        Stream<Row> rowsStream = rows.stream();
        if (headerSet) {
            rowsStream = rowsStream.skip(1);
        }
        rowsStream.forEach(r -> r.getCells().get(colNum).setFormatting(formatting));
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
