package eu.snik.tag;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONObject;
import eu.snik.tag.gui.CollectionStringConverter;

/** An RDF class following the SNIK meta model. Fields can be modified. */
public class Clazz implements Serializable
{
	/** rdfs:label*/
	public final Set<String> labels = new LinkedHashSet<>();
	public Set<String> getLabels() {return Collections.unmodifiableSet(labels);} // for table view
	
	/** the URI part after the prefix*/
	public String localName;
	public String getLocalName() {return localName;} // for table view
	
	/** whether the class is a function, role or entity type.*/
	public Subtop subtop;
	public Subtop getSubtop() {return subtop;} // for table view

	public Clazz(String label,String localName,Subtop subtop)
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

//	// Set would be better but for some reason State.load throws an error then when calling hashCode of Clazz where localName is null 
//	final List<Triple> triples = new ArrayList<>();
//
//	/** an unmodifiable copy of the triples */
//	public List<Triple> getTriples() {return Collections.unmodifiableList(triples);}
//
//	/** Add a SNIK meta model conforming triple.
//	  @param predicate a SNIK meta model relation  
//	  @param object another SNIK class	 
//	 */
//	public void addTriple(Relation predicate,Clazz object)
//	{		
//		triples.add(new Triple(this,predicate,object));
//	}

	public String labelString()
	{
		return CollectionStringConverter.INSTANCE.toString(labels);
	}

	@Override
	public String toString()
	{
		return localName+" ("+subtop+')';
		//return label+','+localName+','+subtop; // Too technical for users. If needed in detail more often, encapsulate this where the simple form is needed, e.g. in the relation pane.  
	}

	/** The RDF model is not cached because local name, label and subtop may all be edited by the user.
	 * @return a Jena model with all triples where this RDF class is a subject. */
	public Model rdfModel()
	{
		var model = ModelFactory.createDefaultModel();
		Resource clazz = model.createResource(uri(), OWL.Class);
		model.add(clazz, RDFS.subClassOf, subtop.resource);
		for(String label: labels ) {model.add(clazz, RDFS.label, model.createLiteral(label, "en"));}

		return model;
	}

	String uri() {return Snik.BB2+localName;}

	/** @return create a non-model-backed resource with the classe's local name in the SNIK bb2 namespace.*/
	public Resource resource()
	{
		return ResourceFactory.createResource(Snik.BB2+localName);
	}

	public JSONObject cytoscapeNode()
	{
		var l = new JSONObject()		
				.put("en", this.labels);
		
		var data = new JSONObject()
				.put("id", uri())
				.put("l", l)
				.put("st", subtop.toString().substring(0, 1).toUpperCase())
				.put("prefix", "bb");

		var position = new JSONObject()
				.put("x", Math.random()*100)
				.put("y", Math.random()*100);

		return new JSONObject()
				.put("group", "nodes")
				.put("data",data)
				.put("position",position);
				//.append("classes",subtop.toString());
	}
	
}
