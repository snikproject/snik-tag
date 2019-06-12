import java.util.List;
import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.finders.ClassFinder;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class Main
{

	static class TraversalCallback extends TraversalUtil.CallbackImpl {
		@Override
		public List<Object> apply(Object o) {
	
			System.out.println(o.getClass());
//			org.docx4j.wml.Text textNode = (org.docx4j.wml.Text) o;
//
//			String textContent = textNode.getValue();
//
//
//			System.out.println("Found a string: " + textContent);

			//root.appendChild(element);

			return null;
		}

		@Override
		public boolean shouldTraverse(Object o) {
			return true;
		}
	}
	public static void main(String[] args) throws Docx4JException
	{
		final WordprocessingMLPackage wordMLPackage =	Docx4J.load(new java.io.File("../benchmark/input.docx"));
		final MainDocumentPart part = wordMLPackage.getMainDocumentPart();
		new TraversalCallback().walkJAXBElements(part);
		//final Document doc = (org.docx4j.wml.Document) part.getJaxbElement();
		//Body body = doc.getBody();
		//final ClassFinder finder = new ClassFinder(Object.class);
		//new TraversalUtil(part,finder);
		//finder.results.stream().forEach(e->{System.out.println(e);});




	}

}
