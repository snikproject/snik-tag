package eu.snik.tag;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** An RDF class following the SNIK meta model. Fields can be modified. */
@Getter // used by cell PropertyValueFactory 
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Clazz
{
	/** rdfs:label*/
	public String label;
	/** the URI part after the prefix*/
	public String localName;
	/** whether the class is a function, role or entity type.*/
	public Subtop subtop;

	@EqualsAndHashCode.Exclude
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

	@Override
	public String toString()
	{		
		return label+','+localName+','+subtop;
	}

	/** The RDF model is not cached because local name, label and subtop may all be edited by the user.
	 * @return a Jena model with all triples where this RDF class is a subject. */
	public Model rdfModel()
	{
		var model = ModelFactory.createDefaultModel();
		Resource clazz = model.createResource(Snik.BB2+localName, OWL.Class);
		model.add(clazz, RDFS.subClassOf, subtop.resource);

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
