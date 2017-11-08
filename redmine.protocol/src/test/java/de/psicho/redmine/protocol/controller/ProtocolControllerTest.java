package de.psicho.redmine.protocol.controller;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;

import de.psicho.redmine.iTextile.iTextile;
import de.psicho.redmine.iTextile.utils.ResourceUtils;
import net.java.textilej.parser.markup.textile.TextileDialect;

@Slf4j
public class ProtocolControllerTest {

    private iTextile iTextile;
    private List<String> members = Arrays.asList("Alberto", "Ann", "Anne M.", "Markus", "Stefan E.", "Stefan Q.");
    private static final String resultDir = "results";

    @BeforeClass
    public static void createDirectory() {
        try {
            new File(resultDir).mkdir();
        } catch (SecurityException se) {
            log.error("Could not create result directory " + resultDir, se);
        }
    }

    @Test
    public void complexHtmlListsShouldShowProperly() throws Exception {
        String input = ResourceUtils.readResource("demo.html");

        input = markPersons(input);

        iTextile = new iTextile(resultDir + "/demoComplex.pdf");
        iTextile.startTable(1);
        iTextile.setTableColumnParser(0, new TextileDialect());
        iTextile.addTableRow(input);
        iTextile.endTable();
        iTextile.createFile();
    }

    @Test
    public void simpleHtmlListsShouldShowProperly() throws Exception {
        String input = "* one\r\n** two\r\n* three";

        iTextile = new iTextile(resultDir + "/demoSimple.pdf");
        iTextile.startTable(1);
        iTextile.setTableColumnParser(0, new TextileDialect());
        iTextile.addTableRow(input);
        iTextile.endTable();
        iTextile.createFile();
    }

    // copy of ITextService.markPersons(String)
    private String markPersons(String content) {
        String replaced = content;
        for (String member : members) {
            Matcher matcher = Pattern.compile("(" + member + ")([^a-zA-ZäöüÄÖÜß])").matcher(replaced);
            replaced = matcher.replaceAll("<b>%{background:yellow}$1%</b>$2");
        }
        return replaced;
    }
}
