package eu.snik.tag;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString(includeFieldNames=false)
public class Clazz
{
		//static int count = 0; 
		String label;
		String localName;
		Subtop subtop;		
		
		@ToString.Exclude
		final Model model = ModelFactory.createDefaultModel();
		
		@ToString.Exclude		
		final List<SimpleImmutableEntry<Relation,Clazz>> triples = new ArrayList<>();
		
		public List<SimpleImmutableEntry<Relation, Clazz>> getTriples() {return Collections.unmodifiableList(triples);}
		
		public void addTriple(Relation relation, Clazz clazz) throws IllegalArgumentException
		{
			if(relation.domain!=this.subtop) {throw new IllegalArgumentException("Domain of "+relation+" is "+relation.domain+" but source subtop is "+this.subtop);}
			if(relation.range!=clazz.subtop) {throw new IllegalArgumentException("Range of "+relation+" is "+relation.range+" but target subtop is "+clazz.subtop);}
			triples.add(new SimpleImmutableEntry<Relation, Clazz>(relation, clazz));
		}
		
}
