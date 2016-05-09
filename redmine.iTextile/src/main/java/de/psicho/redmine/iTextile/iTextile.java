package de.psicho.redmine.iTextile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

public class iTextile {
    Document document;
    String filename;
    String textile;

    public String getTextile() {
        return textile;
    }

    public void setTextile(String textile) {
        this.textile = textile;
    }

    public iTextile(String argFilename) {
        this.filename = argFilename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void createFile() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
        } catch (FileNotFoundException | DocumentException ex) {
            ex.printStackTrace();
        }
        document.open();
        process();
        document.close();
    }

    private void process() {

    }

}
