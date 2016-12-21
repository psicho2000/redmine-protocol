Letzte Schritte:

 * Test MySQL Connection for Lifeline (running Jar directly on Lifeline)
 * Logging (insbes. für Fehler)
 * Namen hervorheben? Wenn ja, wie erkennen und wie von anderen, gleichartigen Namen unterscheiden?
 * closeProtocol() entkommentieren und testen
   
Reorganize ProtocolController
   
 1. see notes in ProtocolController
 1. Remove dependency of TextileDialect: use enum
 1. Move dependency of itextpdf to separate package
 1. Move dependency of taskadapter to separate package

Später

 * Protokoll-Nummer automatisch zählen
 * Agenda erstellen