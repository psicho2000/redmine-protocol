Bugs "Doppelte Listen funktionieren nicht"
    * Erste Ebene
    ** Zweite Ebene

	"Nested lists don't work in a cell"
	http://developers.itextpdf.com/de/node/2243
	Allerdings gilt das nur für die Standard-Variante. Die Working-Variante geht auch in Zellen!
	
	Fazit:
		* MarkupParser() erzeugt korrektes HTML
		* XMLWorkerHelper kann dieses XML außerhalb von Tabellen verstehen
		* innerhalb von Tabellen muss es anders formuliert werden
	
	Transformer:					zu
		<ul>						<ul>
			<li>1						<li>1</li>
				<ul>					<ul>
					<li>2</li>			<li>2</li>
				</ul>					</ul>
			</li>
		</ul>						</ul>

http://www.programmableweb.com/api/chuck-norris-facts
http://www.programmableweb.com/api/xkcd-swagger

Inbetriebnahme
 * Test MySQL Connection for Lifeline (running Jar directly on Lifeline)

----

Später

 * Protokoll-Nummer automatisch zählen
 * Agenda erstellen
 
Reorganize ProtocolController
   
 1. Remove dependency of TextileDialect: use enum instead
 1. Move dependency of itextpdf to separate package
 1. Move dependency of taskadapter to separate package
 