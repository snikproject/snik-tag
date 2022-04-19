## :warning: **SNIK Tag is not maintained**
This repository has been archived on 2022-04-19.

# SNIK Tag

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

# Windows Release

Download the portable Windows release version 0.3.1 [here](https://github.com/IMISE/snik-tag/releases/download/0.3.1/sniktag.zip).

# Development

## Requirements
* Java 16 or higher
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
A brief documentation of SNIK Tag is found at [gh-pages](https://imise.github.io/snik-tag/#/Dokumentation)

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
