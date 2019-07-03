package eu.snik.tag;
import static eu.snik.tag.Subtop.*;

public enum Relation
{
	// Meta Model from 10.12.2018
	isInvolvedIn(ROLE,FUNCTION),isReponsibleForFunction(ROLE,FUNCTION),approvesFunction(ROLE,FUNCTION),
	uses(FUNCTION,ENTITY_TYPE),updates(FUNCTION,ENTITY_TYPE),increases(FUNCTION,ENTITY_TYPE),decreases(FUNCTION,ENTITY_TYPE),
	isResponsibleForEntityType(ROLE,ENTITY_TYPE),approvesEntityType(ROLE,ENTITY_TYPE),
		
	isResponsibleForRole(ROLE,ROLE),roleComponent(ROLE,ROLE),
	FunctionComponent(FUNCTION,FUNCTION),
	EntityTypeComponent(ENTITY_TYPE,ENTITY_TYPE),isBasedOn(ENTITY_TYPE,ENTITY_TYPE);	
	
	
	public final Subtop domain,range;
	
	Relation(Subtop domain,Subtop range)
	{		
		this.domain=domain;
		this.range=range;		
	}
	
	public final String uri = Snik.META+this.toString();

}
