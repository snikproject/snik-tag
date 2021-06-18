package eu.snik.tag.gui;

import java.io.StringWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import eu.snik.tag.Clazz;
import eu.snik.tag.Triple;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TextArea;

/** Displays all extracted RDF classes and relations as RDF turtle. */
public class RDFArea extends TextArea
{
	final State state;
	
	public RDFArea(final State state)
	{
		this.state=state;
		this.state.classes.addListener((ListChangeListener<Clazz>)(l)->{refresh();});
		this.state.triples.addListener((ListChangeListener<Triple>)(l)->{refresh();});
		this.setEditable(false);
	}

		/** @param classes SNIK classes with triples
	@return a Jena model of all triples with the classes as subjects. */
	public static Model rdfModel(State state)
	{
		Model model = ModelFactory.createDefaultModel();
		state.classes.stream().map(Clazz::rdfModel).forEach(model::add);
		state.triples.stream().map(Triple::statement).forEach(model::add);
		return model;
	}
	
	public void refresh()
	{
		var writer = new StringWriter();
		RDFArea.rdfModel(state).write(writer,"N-TRIPLE");
		setText(writer.toString());
	}

}