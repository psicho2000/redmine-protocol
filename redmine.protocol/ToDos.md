Inbetriebnahme
1. redmine-protocol in Autostart packen

2. DB-Verbindung korrigieren
	http://stackoverflow.com/questions/6865538/solving-a-communications-link-failure-with-jdbc-and-mysql

3. mvn clean install (für redmine.protocol) zum Laufen bringen

4. Protokoll verschicken
_________________________

Später

 * Zitate einfügen
	http://www.programmableweb.com/api/xkcd-swagger
	http://www.programmableweb.com/api/chuck-norris-facts (bad quality)
 * Protokoll-Nummer automatisch zählen
 * Agenda erstellen
 * ggf. Sortierung der Journals nach Zeitstempel?
 
Reorganize ProtocolController
   
 1. Remove dependency of TextileDialect: use enum instead
 1. Move dependency of itextpdf to separate package
 1. Move dependency of taskadapter to separate package
 