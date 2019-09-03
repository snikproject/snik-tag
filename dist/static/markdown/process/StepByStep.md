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
