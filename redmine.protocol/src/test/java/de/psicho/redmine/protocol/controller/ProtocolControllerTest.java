package de.psicho.redmine.protocol.controller;

import org.junit.Test;

import de.psicho.redmine.iTextile.iTextile;
import net.java.textilej.parser.markup.textile.TextileDialect;

public class ProtocolControllerTest {

    private iTextile iTextile;

    @Test
    public void htmlListsShouldShowProperly() throws Exception {
        String input = "* one\r\n** two\r\n* three";

        iTextile = new iTextile("results/demo.pdf");
        iTextile.startTable(1);
        iTextile.setTableColumnParser(0, new TextileDialect());
        iTextile.addTableRow(input);
        iTextile.endTable();
        iTextile.createFile();
    }
}
