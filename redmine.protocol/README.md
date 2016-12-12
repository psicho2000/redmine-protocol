# Einführung
- spring boot application mit externer DB und spring JDBCTemplate
- Eingabe der Issue-ID per Rest-Param (Alternativ: Web-GUI)
    http://lifeline-herne.de:8090/protocol/{issueId}

# Quellen
## API für Redmine
- http://www.redmine.org/projects/redmine/wiki/Rest_api_with_java
- http://www.redmine.org/projects/redmine/wiki/Rest_api
- https://github.com/taskadapter/redmine-java-api/blob/master/README.md

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