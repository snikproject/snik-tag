package eu.snik.tag;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Clazz
{ 
		String label;
		String localName;
		Subtop subtop;		
		
		final transient Model model = ModelFactory.createDefaultModel();
				
		@EqualsAndHashCode.Exclude
		final Set<Triple> triples = new HashSet<>();
		
		public Set<Triple> getTriples() {return Collections.unmodifiableSet(triples);}
		public void addTriple(Relation predicate,Clazz object) {triples.add(new Triple(this,predicate,object));}

		@Override
		public String toString()
		{		
			return label+','+localName+','+subtop;
		}

}
