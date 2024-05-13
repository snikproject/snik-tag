package eu.snik.tag.gui;

import eu.snik.tag.Clazz;
import eu.snik.tag.Triple;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

class State {

	public final StringProperty text = new SimpleStringProperty();
	public final ObservableList<Clazz> classes = FXCollections.observableArrayList();
	public final ObservableList<Triple> triples = FXCollections.observableArrayList();

	public transient ObjectProperty<Clazz> selectedSubject = new SimpleObjectProperty<>();
	public transient ObjectProperty<Clazz> selectedObject = new SimpleObjectProperty<>();

	public State() {
		classes.addListener(
			(ListChangeListener<Clazz>) change -> {
				while (change.next()) {
					var removed = new HashSet<>(change.getRemoved());

					if (removed.contains(selectedSubject.get())) {
						selectedSubject.set(null);
					}
					if (removed.contains(selectedObject.get())) {
						selectedObject.set(null);
					}

					triples.removeIf(t -> removed.contains(t.subject()) || removed.contains(t.object()));
				}
			}
		);
	}

	@SuppressWarnings("unchecked")
	public State(InputStream in) throws IOException {
		this();
		try (var oin = new ObjectInputStream(in)) {
			this.text.set((String) oin.readObject());
			this.classes.addAll((ArrayList<Clazz>) oin.readObject());
			this.triples.addAll((ArrayList<Triple>) oin.readObject());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public State(String text, Collection<Clazz> classes, Collection<Triple> triples) {
		this();
		this.text.set(text);
		this.classes.addAll(classes);
		this.triples.addAll(triples);
	}

	public void save(OutputStream out) throws IOException {
		try (var oout = new ObjectOutputStream(out)) {
			oout.writeObject(text.get());
			oout.writeObject(new ArrayList<>(classes));
			oout.writeObject(new ArrayList<>(triples));
		}
	}

	public JSONArray cytoscapeElements() {
		var elements = new ArrayList<JSONObject>();

		// nodes first, edges second
		for (Clazz c : classes) {
			elements.add(c.cytoscapeNode());
		}
		for (Triple t : triples) {
			elements.add(t.cytoscapeEdge());
		}
		return new JSONArray(elements);
	}
}
