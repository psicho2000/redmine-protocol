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
            new File(resultDir).mkdir();
        } catch (SecurityException se) {
            throw new RuntimeException("Could not create result directory " + resultDir, se);
        }
    }

    @Test
    public void createParagraph() throws DocumentCreationException {
        iTextile iTextile = new iTextile("results/paragraphWithFormat.pdf");
        iTextile.addParagraph("+My text+\n\n* Element", TextProperty.builder().build());
        iTextile.createFile();
    }

    @Test
    public void createParagraphWithDialect() throws DocumentCreationException {
        iTextile iTextile = new iTextile("results/paragraphWithDialect.pdf");
        iTextile.addParagraph("+My text+\n\n* Element", new TextileDialect());
        iTextile.createFile();
    }

    @Test
    public void createParagraphWithHeading() throws DocumentCreationException {
        iTextile iTextile = new iTextile("results/paragraphWithHeading.pdf");
        iTextile.addParagraph("h1. Header\n\nh2. Sub-Header\n\nh3. Level 3 Header\n\nContent\n\n* *bold element*",
            new TextileDialect());
        iTextile.createFile();
    }
}
