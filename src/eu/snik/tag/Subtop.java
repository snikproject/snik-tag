package eu.snik.tag;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Subtop
{
	ROLE("b"), FUNCTION("u"), ENTITY_TYPE("i");
	
	public final String htmlTagOpen;
	public final String htmlTagClosed;
	
	Subtop(String htmlTagName)
	{
		this.htmlTagOpen = "<"+htmlTagName+">";
		this.htmlTagClosed = "</"+htmlTagName+">";
	}
	
	};