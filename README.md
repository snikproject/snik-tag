# SNIK Tag
[![CI](https://github.com/snikproject/snik-tag/actions/workflows/ci.yml/badge.svg)](https://github.com/snikproject/snik-tag/actions/workflows/ci.yml)

Semi-automatic extraction of RDF triples from text based on the SNIK meta model.
Support authors of medical informatics textbooks.
You need to prepare a SNIK DOCX file where the classes are tagged in the following way:

 * Entity Type: Italic
 * Role: Bold
 * Function: Underline

SNIK Tag will display the text with those tagged classes but also show the classes separately and allow you to edit them and add links between them.

## Input
A .docx word document where meta model subtop classes are annotated using formatting as **Role**, *Entity Type* and <u>Function</u>.

## Output
The extracted classes and their relations as an RDF Turtle file.

# Motivation and History

During the [SNIK Project](https://snik.eu/), knowledge from several textbooks was manually extracted according to the SNIK metamodel.
This resulted in one subontology per book, for example <https://www.snik.eu/ontology/bb>.
This process was time consuming, unintuitive and error-prone.
SNIK Tag should allow tagging a terms of a new subontology directly by the authors of a new book, such as the [3rd edition of Health Information Systems](https://link.springer.com/book/10.1007/978-3-031-12310-8) (BB2),
while being within Word by just reusing basic formating, so the burden of the authors is minimized.
Relations between those terms (modelled as RDF classes) can then be specified inside SNIK Tag by others.
After polishing, such as merging, splitting or renaming of classes, the resulting subontology can be exported.
While SNIK Tag ultimately wasn't used during writing of BB2, we are currently investigating whether we can efficiently extract the finished BB2 and add it to SNIK.

# Windows Release

Download the portable Windows release version 0.3.1 [here](https://github.com/IMISE/snik-tag/releases/download/0.3.1/sniktag.zip).

# Development

## Requirements
* Java 22 or higher
* Maven 3

## Run
* Run  `mvn compile`
* Run  `mvn javafx:run`

## Package
Create a native package including a stripped down JRE and an installer using jlink and jpackage:

    mvn package
    cd deploy
    ./jlink
    ./package

jlink only needs to be run once for each JRE. Requires the JavaFX JMOD files to be placed in `deploy/javafx-jmods-15 `.
jpackage does NOT support CROSSCOMPILING, you need to be on Windows to create Windows binaries.
Then you can compress the contents of `deploy/SnikTag` to `sniktag.zip` and add that archive as an asset to a new release.

## Documentation
A brief documentation of SNIK Tag is found at [gh-pages](https://snikproject.github.io/snik-tag/)

## Code Formatting
Install the npm dependencies `prettier` and [`prettier-plugin-java`](https://github.com/jhipster/prettier-java):

    # Local installation
    npm install prettier-plugin-java --save-dev
    
    # Or globally
    npm install -g prettier prettier-plugin-java

Then reformat all files with:

    # If you have installed the package locally
    npx prettier --write "src/**/*.java"

    # Or globally
    prettier --write "src/**/*.java"

Source: [`prettier-plugin-java GitHub page`](https://github.com/jhipster/prettier-java).
