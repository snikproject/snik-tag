/** @author Konrad Höffner */
module sniktag {
	exports eu.snik.tag.gui ;
	exports eu.snik.tag ;

	requires java.desktop;
	requires org.docx4j.openxml_objects;
	requires org.apache.commons.lang3;
	requires org.apache.commons.collections4;
	requires org.docx4j.core;
	requires org.jsoup;
	requires jakarta.xml.bind;
	requires transitive org.json;
	requires org.apache.commons.text;
	requires transitive org.apache.jena.core;
	requires org.apache.jena.base;
	requires org.apache.jena.iri;
	requires org.apache.jena.arq;
	requires org.controlsfx.controls;
	requires transitive org.eclipse.jetty.server;
	requires javafx.base;
	requires transitive javafx.controls;
}
