package eu.snik.tag;

import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Triple
{
	final Clazz subject,object;
	final Relation predicate;
	
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
	
	public Statement statement()
	{
		return ResourceFactory.createStatement(subject.resource(), predicate.property, object.resource());
	}
}
