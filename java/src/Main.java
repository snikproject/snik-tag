import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.commons.text.WordUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;


public class Main
{
	static final Model MODEL = ModelFactory.createDefaultModel();
	static final String BB2 = "http://www.snik.eu/ontology/bb2/";
	static final String META = "http://www.snik.eu/ontology/meta/";
	static final Resource ENTITY_TYPE = MODEL.createResource(META+"EntityType"); 
	static final Resource FUNCTION = MODEL.createResource(META+"Function");
	static final Resource ROLE = MODEL.createResource(META+"Role");
	
	static Optional<String> runText(R run)
	{
		return run.getContent()
				.stream()
				.map(JAXBElement.class::cast)
				.map(JAXBElement::getValue)
				.filter(Text.class::isInstance)
				.map(Text.class::cast)
				.map(Text::getValue)
				.findFirst();
	}
	
	
	static String labelToUri(String label)
	{
		return BB2+WordUtils.capitalizeFully(label).replaceAll(" ","");
	}
	
	static String labelToTriple(String label)
	{
		return labelToUri(label)+" rdfs:label "+'"'+label.trim()+'"'+"@en.";
	}

	public static void main(String[] args) throws Docx4JException, JAXBException
	{
		MODEL.setNsPrefix("bb2",BB2);
		MODEL.setNsPrefix("meta",META);

		
		var wordMLPackage =	Docx4J.load(new java.io.File("../benchmark/input.docx"));
		var doc = wordMLPackage.getMainDocumentPart();
		//	https://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
		// https://stackoverflow.com/questions/56579819/wt-not-an-instance-of-org-docx4j-wml-text/56580036#56580036

		//var boldText = (List<JAXBElement<Text>>)(List<?>)doc.getJAXBNodesViaXPath("//w:r[w:rPr/w:b]/w:t", false)
		Object[][] tagClasses = {{"w:b","Entity Type",ENTITY_TYPE},{"w:i","Role",ROLE},{"w:u","Function",FUNCTION}};

		for(var tc: tagClasses)
		{
			String tag = (String)tc[0];
			String clazz = (String)tc[1];
			var runs = (List<R>)(List<?>)doc.getJAXBNodesViaXPath("//w:r[w:rPr/"+tag+"]", false);
			
			Set<String> names = runs.stream().map(Main::runText).flatMap(Optional::stream)
					.map(s->s.replaceAll("[^A-Za-z0-9 ]", ""))
					.filter(s->s.length()>2)
					.collect(Collectors.toSet());

			System.out.println("*****************"+names.size()+" "+clazz+"s found********");
			for(String name: names)
			{
				Resource r = MODEL.createResource(labelToUri(name),(Resource)tc[2]);
				r.addLiteral(RDFS.label, MODEL.createLiteral(name, "en"));
			}
//					+names.stream()
//					.map(Main::labelToTriple)
//					.reduce("", (a,b)->a+"\n"+b));
			//System.out.println(names.stream().map(Main::labelToUri).reduce("", (a,b)->a+"\n"+b));
			/*
		for(R run: boldRuns)
		{
				runText(run).ifPresent(text->
				{
					System.out.println(text);
				});
		}
			 */
			MODEL.write(System.out,"Turtle");
		}
	}
}

