package eu.snik.tag;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.json.JSONObject;

/** A triple connecting two SNIK classes using a meta model relation.*/
@SuppressWarnings("serial")
public class Triple implements Serializable
{
	public final Clazz subject;
	public final Clazz object;
	public final Relation predicate;

	public Clazz getSubject() {return subject;}
	public Clazz getObject() {return object;}
	public Relation getPredicate() {return predicate;}
	
	public static final AtomicInteger count = new AtomicInteger(0);
	public final int cytoscapeId;
	
	/**@throws IllegalArgumentException if domain or range of the predicate are violated by the subtop of the subject or object, respectively.	 */
	public Triple(Clazz subject, Relation predicate, Clazz object) throws IllegalArgumentException
	{		
		if(!predicate.domain.contains(subject.subtop)) {throw new IllegalArgumentException("Domain of "+predicate+" is "+predicate.domain+" but subject subtop is "+subject.subtop);}
		if(!predicate.range.contains(object.subtop)) {throw new IllegalArgumentException("Range of "+predicate+" is "+predicate.range+" but object subtop is "+object.subtop);}
		
		cytoscapeId = count.getAndIncrement();
		
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

	public JSONObject cytoscapeEdge()
	{
		var data = new JSONObject()
				.put("id", cytoscapeId)
				.put("source", subject.uri())
				.put("target", object.uri())
				.put("p",predicate.uri)
				.put("pl",predicate.toString());

		return new JSONObject()
				.put("group", "edges")
				.put("data",data);
	}
	
	/** @return returns a modified copy with a new subject */
	public Triple replaceSubject(Clazz newSubject) {return new Triple(newSubject, this.predicate, this.object);}
	
	/** @return returns a modified copy with a new object */
	public Triple replaceObject(Clazz newObject) {return new Triple(this.subject, this.predicate, newObject);}

}
