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
import org.json.JSONObject;

import eu.snik.tag.gui.CollectionStringConverter;

/**
 * An RDF class following the SNIK meta model. Fields can be modified.
 * @param labels Main label and alternatives of the class (rdfs:label and skos:altLabel)
 * @param abbreviations Alternative labels of the class
 * @param definitions One or more definitions of the class
 * @param localName Unique identifier, without prefix (i.e. {@code "ChiefInformationOfficer"} for class {@code bb:ChiefInformationOfficer}
 * @param subtop Function, Role or EntityType (which class this one is a child of); cf. the SNIK metamodel for more information
 */
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
	
	/**
	 * Strings representing the rdfs:label and, if more than one, alternatives as skos:altLabel of this class.
	 * Primarily for table view.
	 * For more reliant handling of multiple labels, use {@link this#getAbbreviations()}.
	 * @return Set of Strings representing labels (rdfs:label/skos:altLabel) of this RDF class.
	 */
	public Set<String> getLabels() {
		return Collections.unmodifiableSet(labels);
	} // for table view
	
	/**
	 * Strings each representing one skos:altLabel of this class.
	 * Must not be an abbreviation, can be any alternative label.
	 * Primarily for table view.
	 * @return Set of Strings representing alternative labels (skos:altLabel) of this RDF class.
	 */
	public Set<String> getAbbreviations() {
		return Collections.unmodifiableSet(abbreviations);
	} // for table view
	
	/**
	 * Strings each representing one skos:definition of this class.
	 * Expected is one, but maybe someone wants to enter multiple.
	 * @return Set of Strings representing definitions (skos:definition) of this RDF class.
	 */
	public Set<String> getDefinitions() {
		return Collections.unmodifiableSet(definitions);
	} // for table view
	

	/**
	 * Gets the unique name in the domain, so the URI part after the prefix.
	 * For example, if the class is called {@code bb:ChiefInformationOfficer} with the prefix,
	 * this method returns the String {@code "ChiefInformationOfficer"}.
	 * The local name should be unique, because SnikTag is for one ontology (with one prefix) at a time.
	 * @return the URI part after the prefix
	 */
	public String getLocalName() {
		return localName;
	} // for table view

	/** whether the class is a function, role or entity type.*/
	public Subtop getSubtop() {
		return subtop;
	} // expected by PropertyValueFactory<>("localName") in table view

	/**
	 * Labels, abbreviations and definitions are initially empty.
	 */
	public Clazz {
		labels = new LinkedHashSet<>(labels);
		abbreviations = new LinkedHashSet<>(abbreviations);
		definitions = new LinkedHashSet<>(definitions);
	}

	/**
	 * Creates a new class, with one main label, a unique local name and a subtop parent class
	 * @param label Label of the class (rdfs:label)
	 * @param localName Unique identifier, without prefix (i.e. {@code "ChiefInformationOfficer"} for class {@code bb:ChiefInformationOfficer}
	 * @param subtop Function, Role or EntityType (which class this one is a child of); cf. the SNIK metamodel for more information
	 */
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

	/**
	 * Creates a semicolon-separated String of all the labels in the label set
	 * @return A semicolon-separated String of all the labels in the label set
	 */
	public String labelString() {
		return CollectionStringConverter.INSTANCE.toString(labels);
	}

	@Override
	public String toString() {
		return localName + " (" + subtop + ')';
		//return label+','+localName+','+subtop; // Too technical for users. If needed in detail more often, encapsulate this where the simple form is needed, e.g. in the relation pane.
	}

	/** The RDF model is not cached because local name, label and subtop may all be edited by the user.
	 * @return a Jena model with all triples where this RDF class is a subject.
	 */
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

	/**
	 * Creates the URI of the class by concatenating the hardcoded prefix (here: {@link Snik#BB2 Snik.BB2}) and the local name
	 * @return prefix concatenated with local name of this class
	 */
	String uri() {
		return Snik.BB2 + this.localName;
	}

	/** @return create a non-model-backed resource with the classe's local name in the SNIK bb2 namespace.*/
	public Resource resource() {
		return ResourceFactory.createResource(Snik.BB2 + this.localName);
	}

	/**
	 * Creates a Cytoscape node from the current class.
	 * Contains x and y positioning, URI as {@code id} labels with language English and no edges.
	 * Cytoscape is a JavaScript graph library.
	 * @return New Cytoscape Node as JSON
	 */
	public JSONObject cytoscapeNode() {
		var l = new JSONObject().put("en", this.labels);

		var data = new JSONObject().put("id", uri()).put("l", l).put("st", subtop.toString().substring(0, 1).toUpperCase()).put("prefix", "bb");

		var position = new JSONObject().put("x", Math.random() * 100).put("y", Math.random() * 100);

		return new JSONObject().put("group", "nodes").put("data", data).put("position", position);
		//.append("classes",subtop.toString());
	}

	/** 
	 * Creates a modified copy of this class, but with a different local name.
	 * @param newLocalName the new local name
	 * @return A modified copy with a new local name
	 */
	public Clazz replaceLocalName(String newLocalName) {
		return new Clazz(this.labels, this.abbreviations, this.definitions, newLocalName, this.subtop);
	}

	/** 
	 * Creates a modified copy of this class, but with a different Subtop.
	 * @param newSubtop The new Subtop
	 * @return A modified copy with a new Subtop
	 */
	public Clazz replaceSubtop(Subtop newSubtop) {
		return new Clazz(this.labels, this.abbreviations, this.definitions, this.localName, newSubtop);
	}
}
