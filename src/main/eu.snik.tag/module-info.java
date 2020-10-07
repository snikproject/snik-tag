/** @author Konrad Höffner */
module sniktag
{
	exports eu.snik.tag.gui;
	exports eu.snik.tag;

	requires java.desktop;
	requires java.xml.bind;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javatuples;
	requires jetty.servlet.api;
	requires org.apache.commons.lang3;
	requires org.apache.commons.text;
	requires org.apache.jena.core;
	requires org.apache.jena.ext.com.google;
	requires org.controlsfx.controls;
	requires org.docx4j.core;
	requires org.docx4j.openxml_objects;
	requires org.eclipse.jetty.server;
	requires org.eclipse.jetty.util;
	requires org.json;
	requires org.jsoup;
}