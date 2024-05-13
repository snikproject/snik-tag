package eu.snik.tag.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

import eu.snik.tag.Loader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

/** GUI entry point. Run with Maven via javafx:run. */
public class Main extends Application {

	final State state = new State();

	private final SplitPane textPane = new SplitPane();

	private final RDFArea rdfText = new RDFArea(state);

	RelationPane textRelationPane = new RelationPane(state, this::createRestorePoint);
	private ClassTextPane textArea = new ClassTextPane(state, textRelationPane);

	private final ClassTable tableView = new ClassTable(state, this::createRestorePoint);
	private final TripleTable tripleTable = new TripleTable(state.triples, this::createRestorePoint);

	Stage stage;

	private Window window;

	/** Refresh the different visualizations of the RDF classes. Use after classes are modified.*/
	public void refresh() {
		textArea.refresh();
		rdfText.refresh();
	}

	public static final Deque<ByteArrayInputStream> history = new ArrayDeque<>();

	/** Restore the last saved state.*/
	public void restore() {
		if (history.isEmpty()) {
			Log.warn("Cannot undo. No more restore points.", this.window);
			return;
		}
		try {
			openSnikt(history.pop());
		} catch (IOException e) {
			Log.error("Cannot go back to restore point.", e);
		}
	}

	/** Add the current state to the top of the history. */
	public void createRestorePoint() {
		var stream = new ByteArrayOutputStream();
		try {
			state.save(stream);
			history.push(new ByteArrayInputStream(stream.toByteArray()));
		} catch (IOException e) {
			Log.error("Could not save undo state", e);
			throw new RuntimeException(e);
		}
	}

	/** Close the current document and replace it with the one from the given loader. */
	void load(Loader loader) {
		var newClasses = loader.getClasses();
		var newText = loader.getText();

		Platform.runLater(
			() -> {
				createRestorePoint();
				this.state.classes.setAll(newClasses);
				this.state.triples.clear();
				this.state.text.set(newText);
			}
		);
	}

	private class UnclosableTab extends Tab {

		UnclosableTab(String text, Node content) {
			super(text, content);
			setClosable(false);
		}
	}

	/** Setup the GUI. Called automatically with "mvn javafx:run". */
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setTitle("SNIK Tag");

		var pane = new VBox();
		{
			pane.setAlignment(Pos.TOP_CENTER);
			Scene scene = new Scene(pane, 1600, 1000);
			scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
			stage.setScene(scene);
			stage.setMaximized(true);
			stage.show();
			this.window = scene.getWindow();
		}
		pane.getChildren().add(MainMenuBar.create(this));

		rdfText.setMinSize(300, 500);
		textArea.prefHeight(Double.MAX_VALUE); // goal is to fill parent vertically, does not work fully though
		textPane.setPrefWidth(Double.MAX_VALUE);
		textRelationPane.setMinWidth(400);
		textRelationPane.setMaxWidth(500);
		textPane.getItems().addAll(textArea, textRelationPane);
		{
			TabPane tabPane = new TabPane();
			tabPane.setTabDragPolicy(TabDragPolicy.REORDER);

			tabPane
				.getTabs()
				.addAll(
					new UnclosableTab("Text", textPane),
					new UnclosableTab("Klassen", tableView),
					new UnclosableTab("Verbindungen", tripleTable),
					new UnclosableTab("RDF", rdfText)
				);

			pane.getChildren().add(tabPane);
		}
		//openDocx(new File("src/main/resources/eu/snik/tag/benchmark.docx")); // use resource after finished refactoring
	}

	public static void main(String[] args) {
		launch();
	} // Running this directly may fail. Use "mvn javafx:run" instead.

	public void openSnikt(InputStream in) throws IOException {
		State state = new State(in);
		Platform.runLater(
			() -> {
				//textArea.setText(state.text.get());
				this.state.text.set(state.text.get());
				this.state.classes.setAll(state.classes);
				this.state.triples.setAll(state.triples);
				refresh();
			}
		);
	}

	public void saveSnikt(File f) {
		try {
			this.state.save(new FileOutputStream(f));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
