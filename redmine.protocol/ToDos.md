Nächste Schritte:
    1. POC für
        String htmlContent = new MarkupParser().parseToHtml(markupContent);
        ElementList list = XMLWorkerHelper.parseToElementList(htmlContent, null);
        for (Element element : list) {
            cell.addElement(element);
        }
    2. Obiges Konstrukt einbinden
        * neues Package
        * Steuerung, welche Zelle od. Spalte geparst wird
    3. Pre- oder Postprocessor für #123
        * http://redmine.lifeline-herne.de/issues/ wird als Param übergeben
    4. processTop
    5. Abstände vor und nach Tabellen, Farbe von "P R O T O K O L L", etc.
    6. Protokoll-Nummer automatisch zählen
    7. PDF an Ticket anhängen
    8. Protokoll an markus.d.meier@gmail.com schicken mit default-Text für den GR
    9. closeProtocol() entkommentieren
   10. Sicherheit (BasicAuth mit Spring Security)
   11. Test MySQL Connection for Lifeline (running Jar directly on Lifeline)
   12. Logging (insbes. für Fehler)