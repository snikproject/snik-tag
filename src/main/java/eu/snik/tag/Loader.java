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

	static String labelToLocalName(String label) {
		return WordUtils.capitalizeFully(label).replaceAll("[^A-Za-z0-9]", "");
	}
}
