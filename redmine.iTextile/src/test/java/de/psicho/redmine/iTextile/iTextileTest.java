package de.psicho.redmine.iTextile;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import de.psicho.redmine.iTextile.command.TextProperty;
import net.java.textilej.parser.markup.textile.TextileDialect;

public class iTextileTest {

    private static final String resultDir = "results";

    @BeforeClass
    public static void createDirectory() {
        try {
            boolean result = new File(resultDir).mkdir();
        } catch (SecurityException se) {
            throw new RuntimeException("Could not create result directory " + resultDir, se);
        }
    }

    @Test
    public void createParagraph() throws Exception {
        iTextile iTextile = new iTextile("results/paragraphWithFormat.pdf");
        iTextile.addParagraph("+My text+\n\n* Element", TextProperty.builder().build());
        iTextile.createFile();
    }

    @Test
    public void createParagraphWithDialect() throws Exception {
        iTextile iTextile = new iTextile("results/paragraphWithDialect.pdf");
        iTextile.addParagraph("+My text+\n\n* Element", new TextileDialect());
        iTextile.createFile();
    }

    @Test
    public void createParagraphWithHeading() throws Exception {
        iTextile iTextile = new iTextile("results/paragraphWithHeading.pdf");
        iTextile.addParagraph("h1. Header\n\nh2. Sub-Header\n\nh3. Level 3 Header\n\nContent\n\n* *bold element*",
            new TextileDialect());
        iTextile.createFile();
    }

    @Test
    public void createDocumentWithFooter() throws Exception {
        iTextile iTextile = new iTextile("results/documentWithFooter.pdf");
        iTextile.addParagraph("Some text", TextProperty.builder().build());
        iTextile.setFooter(
            "Created by code at https://github.com/psicho2000/redmine-protocol using iText and released under AGPL 3.0.");
        iTextile.createFile();
    }
}
