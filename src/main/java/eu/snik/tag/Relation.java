package eu.snik.tag;

import static eu.snik.tag.Subtop.EntityType;
import static eu.snik.tag.Subtop.Function;
import static eu.snik.tag.Subtop.Role;

import java.util.Set;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;

/**
 * Relations in SNIK according to the Meta Model from 10.12.2018
 */
public enum Relation {
	
	isInvolvedIn(Role, Function),
	isResponsibleForFunction(Role, Function),
	approvesFunction(Role, Function),
	uses(Function, EntityType),
	updates(Function, EntityType),
	increases(Function, EntityType),
	decreases(Function, EntityType),
	isResponsibleForEntityType(Role, EntityType),
	approvesEntityType(Role, EntityType),

	isResponsibleForRole(Role, Role),
	roleComponent(Role, Role),
	functionComponent(Function, Function),
	entityTypeComponent(EntityType, EntityType),
	isBasedOn(EntityType, EntityType),

	subClassOfEntityType(EntityType, EntityType),
	subClassOfRole(Role, Role),
	subClassOfFunction(Function, Function),

	isAssociatedWith(new Subtop[] { Role, Function, EntityType }, new Subtop[] { Role, Function, EntityType });

	@Override
	public String toString() {
		if (super.toString().startsWith("subClassOf")) {
			return "subClassOf";
		}
		return super.toString();
	}

	/**
	 * Domain of the Relation; Subject of the RDF triple.
	 * One or more Subtops.
	 * Only the children of these Subtops can have this relation with others.
	 */
	public final Set<Subtop> domain;
	/**
	 * Range of the Relation; Object of the RDF triple.
	 * One or more Subtops.
	 * This relationship is possible to be had only with the children of the Subtops contained in range.
	 */
	public final Set<Subtop> range;
	/**
	 * Property to 
	 */
	public final Property property;
	/**
	 * Unique Resource Identifier of the relation.
	 * All relations are contained in the ({@link Snik#META meta subontology}.
	 */
	public final String uri;

	/**
	 * Create a new relation with only one possible domain and range Subtop.
	 * They can be different or the same.
	 * @param domain Domain of the relation
	 * @param range Range of the relation
	 * @see #domain
	 * @see #range
	 */
	Relation(Subtop domain, Subtop range) {
		this(new Subtop[] { domain }, new Subtop[] { range });
	}

	/**
	 * Create a new relation with one or more (possibly none) possible domain and range Subtop.
	 * They do not need to be of the same order.
	 * @param domain Domain of the relation
	 * @param range Range of the relation
	 * @see #domain
	 * @see #range
	 */
	Relation(Subtop[] domain, Subtop[] range) {
		this.domain = Set.<Subtop>of(domain);
		this.range = Set.<Subtop>of(range);

		uri = this.toString().equals("subClassOf") ? RDFS.subClassOf.getURI() : Snik.META + this.toString();
		this.property = ResourceFactory.createProperty(uri);
	}
}
