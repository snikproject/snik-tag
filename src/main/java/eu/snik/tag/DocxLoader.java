package eu.snik.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

	/**
	 * Creates a new instance for the DOCX loader to load one DOCX file
	 * @param in Input stream for the DOCX file to load
	 * @throws IOException Exceptions while trying to open/read the file
	 */
	public DocxLoader(InputStream in) throws IOException {
		super(in);
	}

	/**
	 * Search for any occurences of Docx4J instances from any given node in the document tree.
	 * Originally from <a href="http://www.smartjava.org/content/create-complex-word-docx-documents-programatically-docx4j/">this blog post</a>.
	 * @param obj object to search in
	 * @param toSearch Classes to search for
	 * @return List of all occurrences of instances of the given classes as self, children or transitive children of the given object.
	 */
	private static List<Object> getAllElementsFromObject(Object obj, Class<?>... toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

		// only add object to found after processing its children
		if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementsFromObject(child, toSearch));
			}
		}
		
		if (Arrays.asList(toSearch).contains(obj.getClass())) {
			result.add(obj);
		}
		
		return result;
	}

	/**
	 * Get the entire unformatted textual content of the document.
	 * @return the complete text from the DOCX file without any formatting (except line breaks)
	 */
	@Override
	public String getText() {
		try {
			var wordMLPackage = Docx4J.load(in());

			var doc = wordMLPackage.getMainDocumentPart();
			var parts = new ArrayList<String>();

			// extract all text passages (including paragraph objects for information on line breaks)
			List<Object> texts = DocxLoader.getAllElementsFromObject(doc, org.docx4j.wml.Text.class, org.docx4j.wml.P.class);
			
			// convert org.docx4j.wml.Text-s to Strings (interpret paragraphs as line breaks)
			for (Object t : texts) {
				if(t instanceof org.docx4j.wml.P) {
					parts.add("\n\n");
				} else {
					org.docx4j.wml.Text content = (org.docx4j.wml.Text) t;
					parts.add(content.getValue());
				}
			}
			
			// put the parts together
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

						return a + b;
					}
				)
				.get();
		} catch (Docx4JException e) {
			throw new RuntimeException("Error loading Docx File", e);
		}
	}

	/**
	 * Local type used for quickly identifying tagged tokens.
	 */
	private record TagClass(String tag, String description, Subtop subtop) {}

	/**
	 * Extract all classes marked in the Docx document, without any duplicates.
	 * @return all classes extracted from the tagged parts of the DOCX document
	 */
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
					
					// remove multiply annotated tokens, then add the rest to processedLabels
					Clazz clazz = new Clazz(label, labelToLocalName(label), tc.subtop);
					if (processedLabels.contains(label)) {
						classes
							.stream()
							.filter(c -> c.labels().contains(label) && (!c.subtop().equals(clazz.subtop())))
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

			System.out.println(classes.size() + " classes extracted.");

			return classes;
		} catch (Docx4JException | JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
