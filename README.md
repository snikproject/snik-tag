# SNIK Tag

Semi-automatic extraction of RDF triples from text based on the SNIK meta model.
Support authors of medical informatics textbooks.
You need to prepare a SNIK DOCX file where the classes are tagged in the following way:

 * Entity Type: Italic
 * Role: Bold
 * Function: Underline

SNIK Tag will display the text with those tagged classes but also show the classes separately and allow you to edit them and add links between them.

## Java
Uses project lombok and may require [setup for your IDE](https://projectlombok.org/setup/overview). For now removed

## Input
A .docx word document where meta model subtop classes are annotated using formatting as **Role**, *Entity Type* and <u>Function</u>.

## Output
The extracted classes and their relations as an RDF Turtle file.

## Build
* Run  `mvn compile`
* Run  `mvn javafx:compile`
* Run  `mvn javafx:run`

## Documentation
A brief documentation of SNIK Tag is found at [gh-pages](https://imise.github.io/snik-tag/#/Dokumentation)
