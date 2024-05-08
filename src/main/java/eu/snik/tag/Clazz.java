package eu.snik.tag;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;
import org.json.JSONObject;

import eu.snik.tag.gui.CollectionStringConverter;

/** An RDF class following the SNIK meta model. Fields can be modified. */
public record Clazz(Set<String> labels, Set<String> abbreviations, Set<String> definitions, String localName, Subtop subtop) implements Serializable {
	/**
	 * Getter for all attributes which are of the type Set&lt;String&gt;
	 * @param name "labels", "abbreviations" or "definitions"
	 * @return The Set of the requested items; in case the specified String doesn't exist, null is returned
	 * @todo use enums instead
	 */
	public Set<String> get(String name) {
		switch(name) {
		case "labels": return this.labels();
		case "abbreviations": return this.abbreviations();
		case "definitions": return this.definitions();
		default: return null;
		}
	}
	
	/** rdfs:label*/
	public Set<String> getLabels() {
		return Collections.unmodifiableSet(labels);
	} // for table view
	
	public Set<String> getAbbreviations() {
		return Collections.unmodifiableSet(abbreviations);
	} // for table view
	
	public Set<String> getDefinitions() {
		return Collections.unmodifiableSet(definitions);
	} // for table view
	

	/** the URI part after the prefix*/

	public String getLocalName() {
		return localName;
	} // for table view

	/** whether the class is a function, role or entity type.*/
	public Subtop getSubtop() {
		return subtop;
	} // expected by PropertyValueFactory<>("localName") in table view

	public Clazz {
		labels = new LinkedHashSet<>(labels);
		abbreviations = new LinkedHashSet<>(abbreviations);
		definitions = new LinkedHashSet<>(definitions);
	}

	public Clazz(String label, String localName, Subtop subtop) {
		this(Collections.singleton(label), Collections.singleton(""), Collections.singleton(""), localName, subtop);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Clazz)) {
			return false;
		}
		Clazz other = (Clazz) obj;
		return (other.localName.equals(this.localName)) && (other.subtop.equals(this.subtop));
	}

	@Override
	public int hashCode() {
		return localName.hashCode();
	}

	public String labelString() {
		return CollectionStringConverter.INSTANCE.toString(labels);
	}

	@Override
	public String toString() {
		return localName + " (" + subtop + ')';
		//return label+','+localName+','+subtop; // Too technical for users. If needed in detail more often, encapsulate this where the simple form is needed, e.g. in the relation pane.
	}

	/** The RDF model is not cached because local name, label and subtop may all be edited by the user.
	 * @return a Jena model with all triples where this RDF class is a subject. */
	public Model rdfModel() {
		var model = ModelFactory.createDefaultModel();
		Resource clazz = model.createResource(uri(), OWL.Class);
		model.add(clazz, RDFS.subClassOf, subtop.resource);

		// labels as rdfs:label and skos:altLabel
		Iterator<String> labelIterator = this.labels().iterator();
		model.add(clazz, RDFS.label, model.createLiteral(labelIterator.next(), "en"));
		labelIterator.forEachRemaining(label -> model.add(clazz, SKOS.altLabel, model.createLiteral(label, "en")));

		// abbreviations as skos:altLabel
		for(String abbrv : this.abbreviations()) {
			model.add(clazz, SKOS.altLabel, model.createLiteral(abbrv, "en"));
		}
		
		// definitions as skos:definition
		for(String def : this.definitions()) {
			model.add(clazz, SKOS.definition, model.createLiteral(def, "en"));
		}
		
		return model;
	}

	String uri() {
		return Snik.BB2 + localName;
	}

	/** @return create a non-model-backed resource with the classe's local name in the SNIK bb2 namespace.*/
	public Resource resource() {
		return ResourceFactory.createResource(Snik.BB2 + localName);
	}

	public JSONObject cytoscapeNode() {
		var l = new JSONObject().put("en", this.labels);

		var data = new JSONObject().put("id", uri()).put("l", l).put("st", subtop.toString().substring(0, 1).toUpperCase()).put("prefix", "bb");

		var position = new JSONObject().put("x", Math.random() * 100).put("y", Math.random() * 100);

		return new JSONObject().put("group", "nodes").put("data", data).put("position", position);
		//.append("classes",subtop.toString());
	}

	/** @return returns a modified copy with a new local name */
	public Clazz replaceLocalName(String newLocalName) {
		return new Clazz(this.labels, this.abbreviations, this.definitions, newLocalName, this.subtop);
	}

	/** @return returns a modified copy with a new subtop*/
	public Clazz replaceSubtop(Subtop newSubtop) {
		return new Clazz(this.labels, this.abbreviations, this.definitions, this.localName, newSubtop);
	}
}
