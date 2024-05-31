package eu.snik.tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.apache.commons.text.WordUtils;

/**
 * A Loader loads a document and extracts both the normalised text and the annotated RDF classes from it.
 * @see DocxLoader
 * @see HtmlLoader
 */
public abstract class Loader {

	private final byte[] data;

	/**
	 * Get the file as an input strean
	 * @return Input stream of the file to load with this loader
	 */
	public InputStream in() {
		return new ByteArrayInputStream(this.data);
	} // reusable

	/**
	 * Creates a new loader to load an annotated document.
	 * @param in Input stream for the file to load
	 * @throws IOException Exceptions while trying to open/read the file
	 */
	Loader(InputStream in) throws IOException {
		this.data = in.readAllBytes();
	}

	/**
	 * Get full normalised textual contents of the document as a String
	 * @return Text that the document contains, without formatting
	 */
	public abstract String getText();

	/**
	 * Gets the classes annotated in the document using bold, italic and underlined faces for roles, entity types and functions, respectively
	 * @return A collection of RDF classes
	 */
	public abstract Collection<Clazz> getClasses();

	/**
	 * Normalises the capitalisation of the given String.
	 * Removes all non-alphanumeric characters, so it can be used as a local name/URI.
	 * @param label String, presumably annotated label, from which a local name is generated
	 * @return String capitalised at spaces and hyphens with only alphanumeric characters (A-Za-z0-9)
	 */
	static String labelToLocalName(String label) {
		var delimiters = new char[] {' ','-'};
		return WordUtils.capitalizeFully(label, delimiters).replaceAll("[^A-Za-z0-9]", "");
	}
}
