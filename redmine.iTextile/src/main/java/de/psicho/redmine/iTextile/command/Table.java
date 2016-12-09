package de.psicho.redmine.iTextile.command;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;

public class Table implements Command {

    private int columns;
    private List<Row> rows = new ArrayList<>();

    @Override
    public void process(Document document) {
        // TODO Auto-generated method stub
    }

    /**
     * @param columns
     *        number of columns the table will be created with
     * @throws IllegalArgumentException
     *         if number of columns <= 0
     */
    public Table(int columns) {
        if (columns <= 0) {
            throw new IllegalArgumentException(
                String.format("Number of columns must be greater 0. But %d was provided.", columns));
        }
        this.columns = columns;
    }

    /**
     * @param cells
     *        an input for each cell
     * @throws IllegalArgumentException
     *         if number of cells != number of columns
     */
    public void addRow(List<String> cells) {
        int rowSize = cells.size();
        if (rowSize != columns) {
            throw new IllegalArgumentException(
                String.format("Table has %d columns, but %d cells have been provided.", columns, rowSize));
        }

        Row row = new Row();
        row.setCells(cells);
        rows.add(row);
    }
}
