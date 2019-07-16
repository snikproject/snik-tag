package eu.snik.tag;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Subtop
{
	Role("b"), Function("u"), EntityType("i");
	
	public final String htmlTagOpen;
	public final String htmlTagClosed;
	public final Resource resource;
	
	Subtop(String htmlTagName)
	{
		this.htmlTagOpen = "<"+htmlTagName+">";
		this.htmlTagClosed = "</"+htmlTagName+">";
		this.resource=ResourceFactory.createResource(Snik.META+this.name());
	}
	
	};