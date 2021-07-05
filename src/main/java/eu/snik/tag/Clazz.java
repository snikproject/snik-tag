package eu.snik.tag;

import eu.snik.tag.gui.CollectionStringConverter;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONObject;

/** An RDF class following the SNIK meta model. Fields can be modified. */
public record Clazz(Set<String> labels, String localName, Subtop subtop) implements Serializable {
	/** rdfs:label*/
	public Set<String> getLabels() {
		return Collections.unmodifiableSet(labels);
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
	}

	public Clazz(String label, String localName, Subtop subtop) {
		this(Collections.singleton(label), localName, subtop);
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
		for (String label : labels) {
			model.add(clazz, RDFS.label, model.createLiteral(label, "en"));
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
		return new Clazz(this.labels, newLocalName, this.subtop);
	}

	/** @return returns a modified copy with a new subtop*/
	public Clazz replaceSubtop(Subtop newSubtop) {
		return new Clazz(this.labels, this.localName, newSubtop);
	}
}
