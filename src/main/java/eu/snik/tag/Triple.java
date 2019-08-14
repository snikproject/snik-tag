package eu.snik.tag;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import lombok.EqualsAndHashCode;

/** A triple connecting two SNIK classes using a meta model relation.*/
@EqualsAndHashCode
public class Triple
{
	public final	Clazz subject;
	public 				Clazz object; // needs to be changed on merge
	public final Relation predicate;

	/**@throws IllegalArgumentException if domain or range of the predicate are violated by the subtop of the subject or object, respectively.	 */
	public Triple(Clazz subject, Relation predicate, Clazz object) throws IllegalArgumentException
	{		
		if(predicate.domain!=subject.subtop) {throw new IllegalArgumentException("Domain of "+predicate+" is "+predicate.domain+" but subject subtop is "+subject.subtop);}
		if(predicate.range!=object.subtop) {throw new IllegalArgumentException("Range of "+predicate+" is "+predicate.range+" but object subtop is "+object.subtop);}
		
		this.subject=subject;
		this.object=object;
		this.predicate=predicate;
	}

	@Override
	public String toString()
	{		
		return '('+subject.localName+", "+predicate+", "+object.localName+')';
	}
	
	/** @return create a statement that represents this triple. */
	public Statement statement()
	{
		return ResourceFactory.createStatement(subject.resource(), predicate.property, object.resource());
	}
}
