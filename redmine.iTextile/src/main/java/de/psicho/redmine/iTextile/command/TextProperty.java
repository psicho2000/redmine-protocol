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

    private FontFamily font;
    private float size;
    private int style;
    private BaseColor color;
    private int alignment;

    // https://reinhard.codes/2016/07/13/using-lomboks-builder-annotation-with-default-values/
    public static class TextPropertyBuilder {

        private FontFamily font = FontFamily.HELVETICA;
        private float size = 12.0f;
        private int style = Font.NORMAL;
        private BaseColor color = BaseColor.BLACK;
        private int alignment = Element.ALIGN_LEFT;
    }
}
