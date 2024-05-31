package eu.snik.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.Docx4J;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.R;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;


/** Extracts SNIK classes from a tagged DOCX file. */
public class DocxLoader extends Loader {

	//	private static ObjectFactory factory = Context.getWmlObjectFactory();
	//	private static int commentId = 10000;

	/**
	 * Creates a new instance for the DOCX loader to load one DOCX file
	 * @param in Input stream for the DOCX file to load
	 * @throws IOException Exceptions while trying to open/read the file
	 */
	public DocxLoader(InputStream in) throws IOException {
		super(in);
	}

	/** from https://stackoverflow.com/questions/19676282/docx4j-find-and-replace */
	static List<Object> getAllElementsFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

		if (obj.getClass().equals(toSearch)) result.add(obj); else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementsFromObject(child, toSearch));
			}
		}
		return result;
	}

	/** @return the complete text from the DOCX file without any formatting */
	@Override
	public String getText() {
		try {
			var wordMLPackage = Docx4J.load(in());

			var doc = wordMLPackage.getMainDocumentPart();
			var parts = new ArrayList<String>();

			List<Object> texts = getAllElementsFromObject(doc, org.docx4j.wml.Text.class);
			for (Object t : texts) {
				org.docx4j.wml.Text content = (org.docx4j.wml.Text) t;
				parts.add(content.getValue());
			}
			return parts
				.stream()
				.reduce(
					(a, b) -> {
						if (
							(a.endsWith(" ") || b.startsWith(" ")) ||
							(b.startsWith(".")) ||
							(b.startsWith(",")) ||
							(b.startsWith(";")) ||
							(b.startsWith("’")) ||
							(a.endsWith("(") || b.startsWith(")"))
						) {
							return a + b;
						}

						if (a.endsWith(",") || a.endsWith(".")) {
							return a + (b.startsWith(" ") ? b : (" " + b));
						}

						return a + '\n' + b;
					}
				)
				.get();
		} catch (Docx4JException e) {
			throw new RuntimeException("Error loading Docx File", e);
		}
	}

	record TagClass(String tag, String description, Subtop subtop) {}

	/**	@return all classes extracted from the tagged parts of the DOCX document*/
	@Override
	public Collection<Clazz> getClasses() {
		try {
			var wordMLPackage = Docx4J.load(in());
			var doc = wordMLPackage.getMainDocumentPart();
			//List<Comment> comments = doc.getCommentsPart().getContents().getComment();

			TagClass[] tagClasses = {
				new TagClass("w:i", "Entity Type", Subtop.EntityType),
				new TagClass("w:b", "Role", Subtop.Role),
				new TagClass("w:u", "Function", Subtop.Function),
			};

			var classes = new LinkedHashSet<Clazz>();
			var processedRuns = new HashSet<R>();
			var processedLabels = new HashSet<String>();
			//var warnings = new HashSet<String>(); // prevent the same warning from showing multiple times

			for (var tc : tagClasses) {
				String xpath = "//w:r[w:rPr/" + tc.tag + "[not(@w:val='false')]]";
				// find next bold/italic/underlined passage
				@SuppressWarnings("unchecked")
				var runs = (List<R>) (List<?>) doc.getJAXBNodesViaXPath(xpath, false);
				// remove previously processed passages
				runs.removeAll(processedRuns); // we cannot handle overlapping tags right now

				for (R run : runs) {
					String text = TextUtils.getText(run);
					String label = StringUtils.strip(text, "., ");
					if (label.length() > 80) {
						continue;
					} // too long texts seems to be erroneously detected
					//label = label.replaceAll("[^A-Za-z0-9 ]", ""); // removing non-alphanumerical characters leads to missing matches in the text tab
					String filterLabel = label.replaceAll("[^A-Za-z0-9 ]", "").replaceAll("(the)|(and)|(or)", "");
					if (filterLabel.length() < 4 && !filterLabel.matches("[A-Z]{3}")) {} // abbreviations with 3 letters are OK
					if (filterLabel.length() < 3) {
						continue;
					} // abbreviations
					processedRuns.add(run);

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
					Clazz clazz = new Clazz(label, labelToLocalName(label), tc.subtop);
					//System.out.println(text+" "+ clazz);
					if (processedLabels.contains(label)) {
						classes
							.stream()
							.filter(c -> c.subtop() != clazz.subtop())
							.findAny()
							.ifPresent(
								c -> {
									System.err.println(
										"Typenkonflikt. Klasse \"" +
										label +
										"\" ist getagged als sowohl " +
										c.subtop() +
										" als auch " +
										clazz.subtop() +
										". " +
										"Ignoriere " +
										clazz.subtop() +
										". Bitte beheben Sie den Konflikt im Dokument."
									);
								}
							);
						continue;
					}
					classes.add(clazz);
					processedLabels.add(label);
				}
			}

			//warningCallback.ifPresent(c->c.accept(warnings.stream().reduce("", (a,b)->a+"\n"+b)));
			System.out.println(classes.size() + " classes extracted.");

			return classes;
		} catch (Docx4JException | JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
