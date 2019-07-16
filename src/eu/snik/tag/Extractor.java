package eu.snik.tag;
import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.text.WordUtils;
import org.docx4j.Docx4J;
import org.docx4j.TextUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.wml.Comments.Comment;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.R.CommentReference;
import org.docx4j.wml.Text;

/** Extracts SNIK classes from a tagged DOCX file. */
public class Extractor
{
	private static ObjectFactory factory = Context.getWmlObjectFactory();
	private static int commentId = 10000;

	private static String labelToLocalName(String label)
	{
		return WordUtils.capitalizeFully(label).replaceAll(" ","");
	}

	/** Test script that loads an example file and prints the result to the console.*/
	public static void main(String[] args) throws Docx4JException, JAXBException
	{
		System.out.println(extract(new File("../benchmark/input.docx")).toString().replaceAll("\\), ", "\\),\n"));
	}
	
	/** @return 	the complete text from the DOCX file without any formatting */
	public static String extractText(File docxFile) throws Docx4JException, JAXBException
	{		
		var wordMLPackage =	Docx4J.load(docxFile);

		var doc = wordMLPackage.getMainDocumentPart();
		return TextUtils.getText(doc.getContents());
	}

	/**	@return all classes extracted from the tagged parts of the DOCX file*/
	public static Collection<Clazz> extract(File docxFile) throws Docx4JException, JAXBException
	{		
		var wordMLPackage =	Docx4J.load(docxFile);
		var doc = wordMLPackage.getMainDocumentPart();
		List<Comment> comments = doc.getCommentsPart().getContents().getComment();

		Object[][] tagClasses = {{"w:i","Entity Type",Subtop.EntityType},{"w:b","Role",Subtop.Role},{"w:u","Function",Subtop.Function}};

		var classes = new HashSet<Clazz>();

		for(var tc: tagClasses)
		{
			String tag = (String)tc[0];
			//String clazz = (String)tc[1];
			var runs = (List<R>)(List<?>)doc.getJAXBNodesViaXPath("//w:r[w:rPr/"+tag+"]", false);

			for(R run: runs)
			{
				String name = TextUtils.getText(run).replaceAll("[^A-Za-z0-9 ]", "");
				if(name.length()<3) continue;

				Comment comment = factory.createCommentsComment();
				comments.add(comment);
				comment.setId(BigInteger.valueOf(++commentId));
				Text commentText = factory.createText();
				commentText.setValue("this is a comment for "+name);
				comment.getContent().add(commentText);
				CommentReference commentRef = factory.createRCommentReference();
				run.getContent().add(commentRef);
				commentRef.setId(BigInteger.valueOf(commentId));				

				classes.add(new Clazz(name,labelToLocalName(name),((Subtop)tc[2])));
			}
		}

		System.out.println(classes.size()+" classes extracted.");
		return classes;
	}
}
