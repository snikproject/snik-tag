package eu.snik.tag;
import static eu.snik.tag.Subtop.EntityType;
import static eu.snik.tag.Subtop.Function;
import static eu.snik.tag.Subtop.Role;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public enum Relation
{
	// Meta Model from 10.12.2018
	isInvolvedIn(Role,Function),isReponsibleForFunction(Role,Function),approvesFunction(Role,Function),
	uses(Function,EntityType),updates(Function,EntityType),increases(Function,EntityType),decreases(Function,EntityType),
	isResponsibleForEntityType(Role,EntityType),approvesEntityType(Role,EntityType),
		
	isResponsibleForRole(Role,Role),roleComponent(Role,Role),
	functionComponent(Function,Function),
	entityTypeComponent(EntityType,EntityType),isBasedOn(EntityType,EntityType);	
	
	
	public final Subtop domain,range;
	public final Property property;
	
	Relation(Subtop domain,Subtop range)
	{		
		this.domain=domain;
		this.range=range;		
		this.property = ResourceFactory.createProperty(Snik.META, this.name());
	}
	
	public final String uri = Snik.META+this.toString();

}
