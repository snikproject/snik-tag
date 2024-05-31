/** @author Konrad Höffner */
module sniktag {
	opens eu.snik.tag.gui to javafx.graphics;
	opens eu.snik.tag to javafx.base;
	exports eu.snik.tag;

	requires org.apache.commons.collections4;
	requires org.apache.commons.lang3;
	requires org.apache.commons.text;
	// JSON server
	requires transitive org.json;
	requires transitive org.eclipse.jetty.server;
	// Apache Jena: RDF interaction
	requires transitive org.apache.jena.core;
	requires org.apache.jena.base;
	requires org.apache.jena.arq;
	// Buttons etc.
	requires org.controlsfx.controls;
	// import DOCX files
	requires org.docx4j.openxml_objects;
	requires org.docx4j.core;
	// import HTML files
	requires org.jsoup;
	requires jakarta.xml.bind;
}
