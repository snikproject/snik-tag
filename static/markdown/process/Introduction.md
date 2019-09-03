
## Einführung und allgemeine Hinweise und Tipps zum SNIK TAG Tool

### Die Dokumentation richtet sich hauptsächlich an Anwender.

Bitte nutzen Sie die [Schritt für Schritt Anleitung](#Dokumentation/process/StepByStep) um sich ein Gesamtbild der Funktionalitäten des SNIK TAG Tools zu erarbeiten.

### Voraussetzung für die sinnvolle Nutzung des Tools ist eine Word-Datei (docx-Format) mit folgenden Vorbereitungen:
* *Entity Types* sind kursiv,
* **Roles** sind fett und
* <u>Functions</u> sind unterstrichen dargestellt

### Erläuterung der Reiter
* Text: hier wird der formatierte Text angezeigt und man kann interaktiv Verbindungen definieren und hinzufügen
* Klassen: eine tabellarische Aufstellung der Klassen mit ihren Labels, lokalen Namen, des Typs und Funktionen zum Entfernen und Zusammenführen; außerdem können hier Namen im Freitext beliebig verändert werden.
* Verbindungen: eine Darstellung der semantischen Beziehungen (Subjekt, Relation, Objekt) und der Funktion zum Entfernen einzelner Einträge
* RDF: Darstellung des generierten Outputs im turtle-Format

### Das SNIKT-Format
Zum Zwischenspeichern von bereits bearbeiteten Tags und Relationen wurde eine neues Dateiformat definiert, welches allerdings momentan noch nicht versionsübergreifend funktioniert.
Daher ist bei Versionsupdates des SNIK Tag Tools Vorsicht geboten, es droht Datenverlust!

### Grafische Anzeige der erzeugten Triples im SNIK Graph
Um sich das Ergebnis zu visualisieren speichern Sie die Triples als JSON Datei (`Datei -> Cytoscape JSON exportieren` ODER STRG+J) ab.
Anschließend öffnen Sie den [SNIK Graph](http://www.snik.eu/graph/) im Browser. Wenn dieser fertig geladen ist, laden Sie über `File -> Load Graph with Layout` die zuvor gespeicherte JSON Datei in die Visualisierung.


# docx öffnen
# ändern
# verknüpfungen einfügen
# als .snikt speichern (für späteres weiterarbeiten) --> Vorsicht bei Updates
# zum Anschauen im Graph als .json Datei speicher und in Graph laden
# Labels, local names und Types können geändert werden
# merge von rows beschreiben
# evtl Type spalte schmaler?
# deutsch
