package eu.snik.tag;
import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;
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
		var processedRuns = new HashSet<R>();
		var processedLabels = new HashSet<String>();
		var warnings = new HashSet<String>(); // prevent the same warning from showing multiple times
		
		for(var tc: tagClasses)
		{
			String tag = (String)tc[0];
			var runs = (List<R>)(List<?>)doc.getJAXBNodesViaXPath("//w:r[w:rPr/"+tag+"]", false);
			runs.removeAll(processedRuns); // we cannot handle overlapping tags right now			
			
			processedRuns.addAll(runs);

			for(R run: runs)
			{
				
				String text = StringUtils.strip(TextUtils.getText(run),"., ");
				String label = text;//.replaceAll("[^A-Za-z0-9 ]", ""); // removing non-alphanumerical characters leads to missing matches in the text tab

				String filterLabel = label.replaceAll("[^A-Za-z0-9 ]", ""); 
				if(filterLabel.length()<4&&!filterLabel.matches("[A-Z]{3}")) {continue;} // abbreviations with 3 letters are OK
				if(filterLabel.length()<3) {continue;} // abbreviations
				
				/*
				Comment comment = factory.createCommentsComment();
				comments.add(comment);
				comment.setId(BigInteger.valueOf(++commentId));
				Text commentText = factory.createText();
				commentText.setValue("this is a comment for "+label);
				comment.getContent().add(commentText);
				CommentReference commentRef = factory.createRCommentReference();
				run.getContent().add(commentRef);
				commentRef.setId(BigInteger.valueOf(commentId));				
				 */
				Clazz clazz = new Clazz(label,labelToLocalName(label),((Subtop)tc[2]));
				if(processedLabels.contains(label))
				{
					classes.stream().filter(c->c.subtop!=clazz.subtop).findAny().ifPresent(c->
					{
						warnings.add("Typenkonflikt. Klasse \""+label+"\" ist getagged als sowohl "+c.subtop+" als auch "+clazz.subtop+". "
					+"Ignoriere "+clazz.subtop+". Bitte beheben Sie den Konflikt im Dokument.");
					});					
					continue;
				}
				classes.add(clazz);
				processedLabels.add(label);
			}
		}		
		
		System.err.println(warnings.stream().reduce("", (a,b)->a+"\n"+b));
		System.out.println(classes.size()+" classes extracted.");
		return classes;
	}
}
