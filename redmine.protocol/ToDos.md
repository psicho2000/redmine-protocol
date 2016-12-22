Letzte Schritte:

 * Logging (insbes. für Fehler)
 * Test MySQL Connection for Lifeline (running Jar directly on Lifeline)
 * closeProtocol() entkommentieren und testen
 
 Bugs
 * Doppelte Listen funktionieren nicht:
    * Erste Ebene
    ** Zweite Ebene
   
Reorganize ProtocolController
   
 1. see notes in ProtocolController
 1. Remove dependency of TextileDialect: use enum
 1. Move dependency of itextpdf to separate package
 1. Move dependency of taskadapter to separate package

Später

 * Protokoll-Nummer automatisch zählen
 * Agenda erstellen