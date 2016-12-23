Letzte Schritte:

 * Test MySQL Connection for Lifeline (running Jar directly on Lifeline)
 
 Bugs
 * Doppelte Listen funktionieren nicht:
    * Erste Ebene
    ** Zweite Ebene
   

Später

 * Protokoll-Nummer automatisch zählen
 * Agenda erstellen
 
Reorganize ProtocolController
   
 1. Remove dependency of TextileDialect: use enum
 1. Move dependency of itextpdf to separate package
 1. Move dependency of taskadapter to separate package
 