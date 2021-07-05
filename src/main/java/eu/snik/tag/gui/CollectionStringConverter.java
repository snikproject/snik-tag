package eu.snik.tag.gui;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.util.StringConverter;

public class CollectionStringConverter extends StringConverter<Set<String>> {

	private CollectionStringConverter() {}

	public static final CollectionStringConverter INSTANCE = new CollectionStringConverter();

	@Override
	public String toString(Set<String> collection) {
		if (collection.isEmpty()) {
			return "";
		}
		return collection.stream().reduce((a, b) -> a + ";" + b).get();
	}

	@Override
	public Set<String> fromString(String semicolonSep) {
		return new LinkedHashSet<>(Arrays.asList(semicolonSep.split(" *; *")));
	}
}
