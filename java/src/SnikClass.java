import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class SnikClass
{
		static int count = 0; 
		final String name;
		final String uri;
		final String subtop;
		
		Model model = ModelFactory.createDefaultModel();
		
		
}
