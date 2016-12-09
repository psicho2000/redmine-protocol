package de.psicho.redmine.iTextile.command;

import java.util.List;

import com.itextpdf.text.BaseColor;

import lombok.Data;
import lombok.NonNull;

@Data
public class Row {

    private BaseColor backgroundColor;
    @NonNull
    private List<Cell> cells;
}
