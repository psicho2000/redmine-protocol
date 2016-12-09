package de.psicho.redmine.iTextile.command;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
public class Cell {

    @NonNull
    String content;
    @Setter
    TextProperty formatting;
}
