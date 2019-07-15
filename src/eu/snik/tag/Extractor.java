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
import lombok.extern.java.Log;

public class Extractor
{
	//	static final Model MODEL = ModelFactory.createDefaultModel();
	//	static final String BB2 = "bb2:";
	//	static final String META = "meta:";
	{
		//	MODEL.setNsPrefix("bb2",BB2);
		//	MODEL.setNsPrefix("meta",META);
	}

	//	static final Resource ENTITY_TYPE = MODEL.createResource(META+"EntityType"); 
	//	static final Resource FUNCTION = MODEL.createResource(META+"Function");
	//	static final Resource ROLE = MODEL.createResource(META+"Role");
	//	
	static ObjectFactory factory = Context.getWmlObjectFactory();
	static int commentId = 10000;

	static String labelToLocalName(String label)
	{
		return WordUtils.capitalizeFully(label).replaceAll(" ","");
	}

	//	static String labelToTriple(String label)
	//	{
	//		return labelToLocalName(label)+" rdfs:label "+'"'+label.trim()+'"'+"@en.";
	//	}

	public static void main(String[] args) throws Docx4JException, JAXBException
	{
		System.out.println(extract(new File("../benchmark/input.docx")).toString().replaceAll("\\), ", "\\),\n"));
	}
	
	public static String extractText(File docxFile) throws Docx4JException, JAXBException
	{		

		var wordMLPackage =	Docx4J.load(docxFile);

		var doc = wordMLPackage.getMainDocumentPart();
		return TextUtils.getText(doc.getContents());
		//return doc.getContents()
	}

	public static Collection<Clazz> extract(File docxFile) throws Docx4JException, JAXBException
	{		

		var wordMLPackage =	Docx4J.load(docxFile);

		var doc = wordMLPackage.getMainDocumentPart();
		List<Comment> comments = doc.getCommentsPart().getContents().getComment();
		//	 for(Comment c: comments) {System.out.println(c.getContent());}
		//	https://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
		// https://stackoverflow.com/questions/56579819/wt-not-an-instance-of-org-docx4j-wml-text/56580036#56580036

		// put comment ranges into runs so that they don't overlap with commentreferences in the runs
		//		var commentRangeStart = (List<JAXBElement<CommentRangeStart>>)(List<?>)doc.getJAXBNodesViaXPath("//w:commentRangeStart", false);
		//		var commentRangeEnd = (List<JAXBElement<CommentRangeEnd>>)(List<?>)doc.getJAXBNodesViaXPath("//w:commentRangeEnd", false);
		//		for(var el: commentRangeStart)
		//		{			
		//			var start = el.getValue();
		//			var id = start.getId();
		//			var endEl = commentRangeEnd.stream().filter(e->e.getValue().getId().equals(id)).findFirst().get();
		//
		//		}


		Object[][] tagClasses = {{"w:i","Entity Type",Subtop.ENTITY_TYPE},{"w:b","Role",Subtop.ROLE},{"w:u","Function",Subtop.FUNCTION}};

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
				//				Model model = ModelFactory.createDefaultModel();
				//				Resource r = model.createResource(labelToUri(name),(Resource)tc[2]);
				//				r.addLiteral(RDFS.label, MODEL.createLiteral(name, "en"));					
				//				MODEL.add(model);				
			}
		}

		//		Docx4J.save(wordMLPackage, new File("test.docx"));
		//		var writer = new StringWriter();
		//		MODEL.write(writer,"Turtle");
		//		return writer.toString();
		System.out.println(classes.size()+" classes extracted.");
		return classes;
	}
}

/*
Set<String> names = runs.stream().map(TextUtils::getText)			
		.map(s->s.replaceAll("[^A-Za-z0-9 ]", ""))
		.filter(s->s.length()>2)
		.collect(Collectors.toSet());

System.out.println("*****************"+names.size()+" "+clazz+"s found********");
for(String name: names)
{
	Resource r = MODEL.createResource(labelToUri(name),(Resource)tc[2]);
	r.addLiteral(RDFS.label, MODEL.createLiteral(name, "en"));
}
 */

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