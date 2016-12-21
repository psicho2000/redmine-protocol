Letzte Schritte:
    8. Protokoll an markus.d.meier@gmail.com schicken mit default-Text für den GR
    9. closeProtocol() entkommentieren
   11. Test MySQL Connection for Lifeline (running Jar directly on Lifeline)
   12. Logging (insbes. für Fehler)
   13. Namen hervorheben? Wenn ja, wie erkennen und wie von anderen, gleichartigen Namen unterscheiden?
   
   Reorganize ProtocolController
    a. see notes in ProtocolController
    b. Remove dependency of TextileDialect: use enum
    c. Move dependency of itextpdf to separate package
    d. Move dependency of taskadapter to separate package

Später
    * Protokoll-Nummer automatisch zählen
    * Agenda erstellen