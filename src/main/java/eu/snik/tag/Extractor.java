package eu.snik.tag;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/** Extracts SNIK classes from a tagged DOCX file. */
public class Extractor
{
	private static ObjectFactory factory = Context.getWmlObjectFactory();
	private static int commentId = 10000;

	private static String labelToLocalName(String label)
	{
		return WordUtils.capitalizeFully(label).replaceAll("[^A-Za-z0-9]","");
	}

		/** Test script that loads an example file and prints the result to the console.*/
	/*
	public static void main(String[] args) throws Docx4JException, JAXBException
	{
		System.out.println(extract(new File("../benchmark/input.docx")).toString().replaceAll("\\), ", "\\),\n"));
	}
	*/

	/** @return 	the complete text from the DOCX file without any formatting */
	public static String extractText(InputStream in) throws Docx4JException, JAXBException
	{		
		var wordMLPackage =	Docx4J.load(in);

		var doc = wordMLPackage.getMainDocumentPart();
		return TextUtils.getText(doc.getContents());
	}

	enum TextPartType {NORMAL, HEADING, CLASS}

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class TaggedText
	{
		TextPartType type;		
		String text;		
		Optional<Clazz> clazz;

		@Override public String toString() {return text.toString();}		

		public static TaggedText createNormal(String text) {return new TaggedText(TextPartType.NORMAL,text,Optional.empty());}
		public static TaggedText createHeading(String text) {return new TaggedText(TextPartType.NORMAL,text,Optional.empty());}
		public static TaggedText createClass(String text, Clazz clazz) {return new TaggedText(TextPartType.CLASS,text,Optional.of(clazz));}

		public static String allText(Collection<TaggedText> taggedTexts)
		{
			return taggedTexts.stream().map(TaggedText::toString).reduce("", String::concat);
		};
	}


	/**	@return all classes extracted from the tagged parts of the DOCX document*/
	public static Collection<Clazz> extract(InputStream in) throws Docx4JException, JAXBException
	{		
		var wordMLPackage =	Docx4J.load(in);
		var doc = wordMLPackage.getMainDocumentPart();
		List<Comment> comments = doc.getCommentsPart().getContents().getComment();

		Object[][] tagClasses = {{"w:i","Entity Type",Subtop.EntityType},{"w:b","Role",Subtop.Role},{"w:u","Function",Subtop.Function}};

		var classes = new LinkedHashSet<Clazz>();
		var processedRuns = new HashSet<R>();
		var processedLabels = new HashSet<String>();
		var warnings = new HashSet<String>(); // prevent the same warning from showing multiple times
		var taggedTexts  = new LinkedHashSet<TaggedText>();

		System.out.println(doc.getContent().stream().reduce((a,b)->a.getClass()+"\n"+b.getClass()).get());
		doc.getContent().stream().map(o->o).forEachOrdered(run->
		{
			System.out.println("<"+run+">");
		});
		
		return null;
//
////		for(var tc: tagClasses)
////		{
////			String tag = (String)tc[0];
//			var runs = (List<R>)(List<?>)doc.getJAXBNodesViaXPath("//w:r[w:rPr/"+tag+"]", false);
////			runs.removeAll(processedRuns); // we cannot handle overlapping tags right now			
////
////			processedRuns.addAll(runs);
//
//			for(R run: runs)
//			{
//
//				String text = TextUtils.getText(run);
//
//				String label = StringUtils.strip(text,"., ");
//				//label = label.replaceAll("[^A-Za-z0-9 ]", ""); // removing non-alphanumerical characters leads to missing matches in the text tab
//
//				String filterLabel = label.replaceAll("[^A-Za-z0-9 ]", "").replaceAll("(the)|(and)|(or)",""); 
//				if(filterLabel.length()<4&&!filterLabel.matches("[A-Z]{3}")) {continue;} // abbreviations with 3 letters are OK
//				if(filterLabel.length()<3)
//				{
//					taggedTexts.add(TaggedText.createNormal(text));
//					continue;
//				} // abbreviations
//
//				/*
//				Comment comment = factory.createCommentsComment();
//				comments.add(comment);
//				comment.setId(BigInteger.valueOf(++commentId));
//				Text commentText = factory.createText();
//				commentText.setValue("this is a comment for "+label);
//				comment.getContent().add(commentText);
//				CommentReference commentRef = factory.createRCommentReference();
//				run.getContent().add(commentRef);
//				commentRef.setId(BigInteger.valueOf(commentId));				
//				 */
//				Clazz clazz = new Clazz(label,labelToLocalName(label),((Subtop)tc[2]));
//				if(processedLabels.contains(label))
//				{
//					classes.stream().filter(c->c.subtop!=clazz.subtop).findAny().ifPresent(c->
//					{
//						warnings.add("Typenkonflikt. Klasse \""+label+"\" ist getagged als sowohl "+c.subtop+" als auch "+clazz.subtop+". "
//								+"Ignoriere "+clazz.subtop+". Bitte beheben Sie den Konflikt im Dokument.");
//					});					
//					continue;
//				}
//				classes.add(clazz);
//				processedLabels.add(label);
//			}
//		}		
//
//		System.err.println(warnings.stream().reduce("", (a,b)->a+"\n"+b));
//		System.out.println(classes.size()+" classes extracted.");
//		return classes;
	}
	
}
