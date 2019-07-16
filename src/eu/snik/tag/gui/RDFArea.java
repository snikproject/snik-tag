package eu.snik.tag.gui;

import java.io.StringWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import eu.snik.tag.Clazz;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

public class RDFArea extends TextArea
{
	final ObservableList<Clazz> classes;
	
	public RDFArea(final ObservableList<Clazz> classes)
	{
		this.classes=classes;
		classes.addListener((ListChangeListener<Clazz>)(l)->{refresh();});
		this.setEditable(false);
	}

	public void refresh()
	{
		Model model = ModelFactory.createDefaultModel();
		classes.stream().map(Clazz::rdfModel).forEach(model::add);
		var writer = new StringWriter();
		model.write(writer,"Turtle");
		setText(writer.toString());
	}

}