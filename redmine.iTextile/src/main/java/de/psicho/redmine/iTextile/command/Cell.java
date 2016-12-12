package de.psicho.redmine.iTextile.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Cell {

    @NonNull
    String content;
    TextProperty formatting;
}
