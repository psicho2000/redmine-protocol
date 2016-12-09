package de.psicho.redmine.iTextile.command;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TextProperty {

    private FontFamily font = FontFamily.HELVETICA;
    private float size = 12.0f;
    private int style = Font.NORMAL;
    private BaseColor color = BaseColor.BLACK;
    private int alignment = Element.ALIGN_LEFT;
}
