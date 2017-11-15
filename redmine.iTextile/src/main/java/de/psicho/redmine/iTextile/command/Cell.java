package de.psicho.redmine.iTextile.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.java.textilej.parser.markup.Dialect;

@Data
@AllArgsConstructor
public class Cell {

    // TODO this can be generalized together with fields of {@link Paragraph}

    @NonNull
    String content;
    TextProperty formatting;
    Dialect dialect;
}
