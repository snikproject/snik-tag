package eu.snik.tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.apache.commons.text.WordUtils;

public abstract class Loader {

	private final byte[] data;

	public InputStream in() {
		return new ByteArrayInputStream(this.data);
	} // reusable

	Loader(InputStream in) throws IOException {
		this.data = in.readAllBytes();
	}

	public abstract String getText();

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
