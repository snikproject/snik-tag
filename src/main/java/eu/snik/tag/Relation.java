package eu.snik.tag;

import static eu.snik.tag.Subtop.EntityType;
import static eu.snik.tag.Subtop.Function;
import static eu.snik.tag.Subtop.Role;

import java.util.Set;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;

public enum Relation {
	// Meta Model from 10.12.2018
	isInvolvedIn(Role, Function),
	isReponsibleForFunction(Role, Function),
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

	public final Set<Subtop> domain, range;
	public final Property property;
	public final String uri;

	Relation(Subtop domain, Subtop range) {
		this(new Subtop[] { domain }, new Subtop[] { range });
	}

	Relation(Subtop[] domain, Subtop[] range) {
		this.domain = Set.<Subtop>of(domain);
		this.range = Set.<Subtop>of(range);

		uri = this.toString().equals("subClassOf") ? RDFS.subClassOf.getURI() : Snik.META + this.toString();
		this.property = ResourceFactory.createProperty(uri);
	}
}
