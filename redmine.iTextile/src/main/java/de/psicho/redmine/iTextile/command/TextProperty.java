package de.psicho.redmine.iTextile.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TextProperty {
    private int size;
    private boolean bold;
    private String color;
}
