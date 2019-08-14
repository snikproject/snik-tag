package eu.snik.tag;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import eu.snik.tag.gui.CollectionStringConverter;
import lombok.Getter;
import lombok.Setter;

/** An RDF class following the SNIK meta model. Fields can be modified. */
@Getter // used by cell PropertyValueFactory 
@Setter
public class Clazz
{
	/** rdfs:label*/
	public final Set<String> labels = new LinkedHashSet<>();
	/** the URI part after the prefix*/
	public String localName;
	/** whether the class is a function, role or entity type.*/
	public Subtop subtop;

	public Clazz(String label, String localName, Subtop subtop)
	{
		this.labels.add(label);
		if(Math.random()>0.5) this.labels.add("schm"+label.substring(1)); // testing
		this.localName=localName;
		this.subtop=subtop;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Clazz)) {return false;}
		return (((Clazz)(obj)).localName).equals(this.localName);
	}
	
	@Override
	public int hashCode()
	{
		return localName.hashCode();
	}
	
	final Set<Triple> triples = new HashSet<>();

	/** an unmodifiable copy of the triples */
	public Set<Triple> getTriples() {return Collections.unmodifiableSet(triples);}

	/** Add a SNIK meta model conforming triple.
	  @param predicate a SNIK meta model relation  
	  @param object another SNIK class	 
	 */
	public void addTriple(Relation predicate,Clazz object)
	{		
		triples.add(new Triple(this,predicate,object));
	}

	public String labelString()
	{
		return CollectionStringConverter.INSTANCE.toString(labels);
	}

	@Override
	public String toString()
	{
		return labelString()+", "+subtop;
		//return label+','+localName+','+subtop; // Too technical for users. If needed in detail more often, encapsulate this where the simple form is needed, e.g. in the relation pane.  
	}

	/** The RDF model is not cached because local name, label and subtop may all be edited by the user.
	 * @return a Jena model with all triples where this RDF class is a subject. */
	public Model rdfModel()
	{
		var model = ModelFactory.createDefaultModel();
		Resource clazz = model.createResource(Snik.BB2+localName, OWL.Class);
		model.add(clazz, RDFS.subClassOf, subtop.resource);
		for(String label: labels ) {model.add(clazz, RDFS.label, model.createLiteral(label, "en"));}

		for(Triple triple: triples)
		{
			model.add(triple.statement());
		}
		return model;
	}

	/** @return create a non-model-backed resource with the classe's local name in the SNIK bb2 namespace.*/
	public Resource resource()
	{
		return ResourceFactory.createResource(Snik.BB2+localName);
	}

}
