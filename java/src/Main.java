import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;


public class Main
{

	public static Optional<String> runText(R run)
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
	
	

	public static void main(String[] args) throws Docx4JException, JAXBException
	{
		var wordMLPackage =	Docx4J.load(new java.io.File("../benchmark/input.docx"));
		var doc = wordMLPackage.getMainDocumentPart();
		//	https://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
		// https://stackoverflow.com/questions/56579819/wt-not-an-instance-of-org-docx4j-wml-text/56580036#56580036

		//var boldText = (List<JAXBElement<Text>>)(List<?>)doc.getJAXBNodesViaXPath("//w:r[w:rPr/w:b]/w:t", false)
		String[][] tagClasses = {{"w:b","Entity Type"},{"w:i","Role"},{"w:u","Function"}};

		for(var tc: tagClasses)
		{
			String tag = tc[0];
			String clazz = tc[1];
			var runs = (List<R>)(List<?>)doc.getJAXBNodesViaXPath("//w:r[w:rPr/"+tag+"]", false);
			Set<String> names = runs.stream().map(Main::runText).flatMap(Optional::stream).collect(Collectors.toSet());

			System.out.println("*****************"+names.size()+" "+clazz+"s found********"+names.stream().reduce("", (a,b)->a+"\n"+b));
			/*
		for(R run: boldRuns)
		{
				runText(run).ifPresent(text->
				{
					System.out.println(text);
				});
		}
			 */

		}
	}
}

