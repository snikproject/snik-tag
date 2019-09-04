# SNIK Tag Dokumentation

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
Um sich das Ergebnis zu visualisieren speichern Sie die Triples als JSON Datei (`Datei -> Cytoscape JSON exportieren` oder STRG+J) ab.
Anschließend öffnen Sie den [SNIK Graph](http://www.snik.eu/graph/) im Browser. Wenn dieser fertig geladen ist, laden Sie über `File -> Load Graph with Layout` die zuvor gespeicherte JSON Datei in die Visualisierung.

---
## Ein beispielhafter Arbeitsablauf

## Schritt 1: Textdatei öffnen und laden
Unter Datei -> DOCX Datei öffnen (oder STRG+D) die zu bearbeitende Word-Datei im docx-Format öffen.
Die Datei wird automatisch entsprechend der gesetzten Markierungen formatiert und in das Tool geladen.

---
## Schritt 2: Überprüfen der Klassen
Zunächst ist es sinnvoll, die getaggten Klassen auf Richtigkeit bzw. Sinnhaftigkeit zu überprüfen.
Dazu können Sie in der im Reiter `Klassen` angezeigten Tabelle folgende Operationen durchführen:
* Nach Klassen suchen:
Geben Sie bei `Klassen durchsuchen` einen Suchbegriff ein, um die Auswahl einzuschränken.
* Umbenennen von Labels und Local Names (URIs):
Durch Dopppelklick auf den entsprechenden Eintrag kann man Einträge ändern oder (im Falle von Labels) neue zusätzliche Einträge anfügen. Diese müssen mit Semikolon getrennt werden. So können jeder URI mehrere, frei definierbare Labels zugeordnet werden.
* Zeilenweises Entfernen:
Durch Klick auf das Kreuz können Klassen zeilenweise gelöscht werden.
* Zusammenführen von Einträgen (nur bei gleichem Typ möglich):
Es gibt die Möglichkeit, Einträge zu verschmelzen. Dafür die betreffenden Zeilen per Klick mit gedrückter Shift oder STRG Taste markieren und dann in der Zeile, in die verschmolzen werden soll auf `Zusammenführen` klicken. Die URI ist die der Klasse, in die hinein verschmolzen wurde.
* Ändern des Types:
Dopppelklick auf den Type einer Klasse öffnet ein Dropdown-Menü, über das Sie den Typ durch Klicken ändern können.

---
## Schritt 3: Hinzufügen von Verbindungen
Es gibt prinzipiell 2 Möglichkeiten: über Listen oder interaktiv im Text

1. Listen:
Im rechten Teil der Anzeige finden Sie 2 Dropdown Menüs, hier können Sie das gewünschte Subjekt bzw Objekt auswählen.
Anschließend muss noch im Dropdown-Menü `Verbindung auswählen` die passende Beziehung ausgewählt werden. Das SNIK TAG Tool stellt dabei automatisch nur die laut dem Metamodell relevanten Verbindungen zur Verfügung.
Danach noch auf `Verbindung hinzufügen` klicken, damit die Relation gespeichert wird.

2. Interaktiv im Text:
Das Subjekt wird per Klick mit linker Maustaste, das Objekt per Klick mit rechter Maustaste ausgewählt, die Verbindung wird analog zu Punkt 1 defniert.

---
## Schritt 4: Überprüfen der Verbindungen
Im letzten Arbeitsschritt der halbautomatischen Textextraktion sollten Sie die generierten Verbindungen noch auf Vollständigkeit und Korrektheit überprüfen.
Dazu finden Sie im Reiter `Verbindungen` die folgenden Funktionen:
* Nach Verbindungen suchen:
Geben Sie bei `Verbindungen durchsuchen` einen Suchbegriff ein, um die Auswahl einzuschränken.
* Zeilenweises Entfernen:
Durch Klick auf das Kreuz können Triple zeilenweise gelöscht werden.

Nun kann man die entstandenen Triples noch in turtle-Syntax betrachten, durch Klick auf den Reiter `RDF`.
 
