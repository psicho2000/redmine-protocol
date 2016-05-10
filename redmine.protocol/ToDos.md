- spring boot application mit externer DB und spring JDBCTemplate
- Eingabe der Issue-ID per http-Param (Alternativ: Web-GUI)
- API für Redmine
	http://www.redmine.org/projects/redmine/wiki/Rest_api_with_java
	http://www.redmine.org/projects/redmine/wiki/Rest_api
	https://github.com/taskadapter/redmine-java-api/blob/master/README.md
- apiUrl und apiAccessKey aus Properties holen
	http://docs.spring.io/autorepo/docs/spring/4.1.1.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html
	https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/PropertySource.html
	https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html
	http://www.mkyong.com/spring/spring-propertysources-example/
- Validierungenen:
	* Issue zur issueId gefunden
	* Tracker = Protokoll
	Pflichtparameter angegeben (s. Redmine-Wiki)
	* Zugewiesen an 
	* Beginn
	* Nummer
	* Anwesend
	* Essen
	* Ort
	* Moderation
	* Andacht
- Protokoll weitersetzen
 	* Thema setzen
 	* Status setzen
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
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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