package de.psicho.redmine.iTextile.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
class Row {
    private List<String> cells = new ArrayList<>();
}
