package eu.snik.tag;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.json.JSONObject;

/** A RDF triple connecting two SNIK classes using a meta model relation.*/
public record Triple(Clazz subject, Relation predicate, Clazz object) implements Serializable {
	/**
	 * Getter for JavaFX table view.
	 * @return Subject of this RDF triple. Custom, user-generated class (normally annotated in the {@link Loader input document}).
	 */
	public Clazz getSubject() {
		return subject;
	}
	/**
	 * Getter for JavaFX table view.
	 * @return Object of this RDF triple. Custom, user-generated class (normally annotated in the {@link Loader input document}).
	 */
	public Clazz getObject() {
		return object;
	}
	/**
	 * Getter for JavaFX table view.
	 * @return Predicate of this RDF triple. Relation, class of the {@link Snik#META meta subontology}.
	 */
	public Relation getPredicate() {
		return predicate;
	}

	public static final AtomicInteger count = new AtomicInteger(0);

	/**@throws IllegalArgumentException if domain or range of the predicate are violated by the subtop of the subject or object, respectively.	 */
	public Triple {
		if (!predicate.domain.contains(subject.subtop())) {
			throw new IllegalArgumentException("Domain of " + predicate + " is " + predicate.domain + " but subject subtop is " + subject.subtop());
		}
		if (!predicate.range.contains(object.subtop())) {
			throw new IllegalArgumentException("Range of " + predicate + " is " + predicate.range + " but object subtop is " + object.subtop());
		}
	}

	@Override
	public String toString() {
		return '(' + subject.localName() + ", " + predicate + ", " + object.localName() + ')';
	}

	/** @return create a statement that represents this triple. */
	public Statement statement() {
		return ResourceFactory.createStatement(subject.resource(), predicate.property, object.resource());
	}

	/**
	 * Creates a Cytoscape Edge from this RDF triple.
	 * Puts the source and target as nodes.
	 * The label is the label of the predicate.
	 * Cytoscape is a JavaScript graph library.
	 * @return New Cytoscape Edge as JSON
	 * @see Clazz#cytoscapeNode()
	 */
	public JSONObject cytoscapeEdge() {
		var data = new JSONObject()
			.put("id", hashCode())
			.put("source", subject.uri())
			.put("target", object.uri())
			.put("p", predicate.uri)
			.put("pl", predicate.toString());

		return new JSONObject().put("group", "edges").put("data", data);
	}

	/** 
	 * Creates a new copy of this instance and replaces its subject.
	 * Does not modify the original triple.
	 * @param newSubject the new subject
	 * @return A modified copy with a new subject.
	 */
	public Triple replaceSubject(Clazz newSubject) {
		return new Triple(newSubject, this.predicate, this.object);
	}

	/** 
	 * Creates a new copy of this instance and replaces its object.
	 * Does not modify the original triple.
	 * @param newObject the new object
	 * @return A modified copy with a new object.
	 */
	public Triple replaceObject(Clazz newObject) {
		return new Triple(this.subject, this.predicate, newObject);
	}

	/** 
	 * Creates a new copy of this instance and replaces its predicate.
	 * Does not modify the original triple.
	 * @param newPredicate the new predicate
	 * @return A modified copy with a new predicate.
	 */
	public Triple replacePredicate(Relation newPredicate) {
		return new Triple(this.subject, newPredicate, this.object);
	}
}
