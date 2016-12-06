- Validierungenen:
	Pflichtparameter angegeben (s. Redmine-Wiki)
	* Zugewiesen an 
	* Beginn
- Neues Projekt iTextile
    * Nutzt iText
    * Verwendet reguläre Ausdrücke von textile-j (https://svn.java.net/svn/textile-j~svn/)
    * konvertiert #123 in einen Link
- Rendering
    * Renderer für Wiki-Syntax
    * Zeilenumbrüche / Paragraphen
    * Tabellen
- Finalizing
    * Beschreibung nicht mehr nötig, anstelle desssen wird das PDF an das Ticket angehängt
- Sicherheit
- Test MySQL Connection for Lifeline (running Jar directly on Lifeline)
- Logging
- Autowiring

# Gegenüberstellung PDF Frameworks 
- PDFBox (GPL)
	* https://github.com/dhorions/boxable
	* https://issues.apache.org/jira/browse/PDFBOX-2618
	* https://pdfbox.apache.org/index.html
	Fazit:
	* Low-Level-API
	* Tabellen nur mit externen Bibliotheken (boxable)
	* keine Unterstützung für Paragraphen  
- iText (AGPL, wie OTRS)
	* http://developers.itextpdf.com/examples/itext-building-blocks/list-examples
	Fazit:
	* High-Level-API
	* Tabellen
	* Paragraphen
	* Fett, farbig, Listen (auch mit eigenen Bullets), Blocksatz

# Probleme
Warum geht Spring Boot 1.4.0-RELEASE nicht?
