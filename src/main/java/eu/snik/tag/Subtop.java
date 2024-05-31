package eu.snik.tag;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/** Direct subclass of meta:Top, called subtop class. */
public enum Subtop {
	/**
	 * Represents <a href="https://www.snik.eu/ontology/meta/Role">meta:Role</a>.
	 * Is tagged with <b>bold</b> face in documents.
	 */
	Role("b"),
	/**
	 * Represents <a href="https://www.snik.eu/ontology/meta/Function">meta:Function</a>.
	 * Is tagged with <u>underlined</u> face in documents.
	 */
	Function("u"),
	/**
	 * Represents <a href="https://www.snik.eu/ontology/meta/EntityType">meta:EntityType</a>.
	 * Is tagged with <i>italic</i> face in documents.
	 */
	EntityType("i");

	/** Tag that is used to annotate in HTML, open */
	public final String htmlTagOpen;
	/** Tag that is used to annotate in HTML, closed */
	public final String htmlTagClosed;

	/**	Not-model-backed Jena resource for the subtop. */
	public final Resource resource;

	private Subtop(String htmlTagName) {
		this.htmlTagOpen = "<" + htmlTagName + ">";
		this.htmlTagClosed = "</" + htmlTagName + ">";
		this.resource = ResourceFactory.createResource(Snik.META + this.name());
	}
}
