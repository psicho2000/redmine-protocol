package de.psicho.redmine.iTextile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DocumentFooter extends PdfPageEventHelper {

    private Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.LIGHT_GRAY);

    @NonNull
    private String footerText;

    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        Phrase footer = new Phrase(footerText, footerFont);
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
            (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
    }
}
