package eu.snik.tag.gui;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import eu.snik.tag.Extractor;
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
public class Main extends Application
{
	public final State state = new State();

	private final SplitPane textPane = new SplitPane();

	private final RDFArea rdfText = new RDFArea(state);

	RelationPane textRelationPane = new RelationPane(state,this::refresh);
	private ClassTextPane textArea = new ClassTextPane(state,textRelationPane);

	private final ClassTable tableView = new ClassTable(state, this::refresh);
	private final TripleTable tripleTable = new TripleTable(state.triples, this::refresh);

	public Stage stage;

	private Window window;

	/** Refresh the different visualizations of the RDF classes. Use after classes are modified.*/
	public void refresh()
	{		
		textArea.refresh();
		rdfText.refresh();
	}

	/** @param file DOCX file with tagged entity types (italic), roles (bold) and functions (function).
	 * @throws FileNotFoundException 
	 * @throws Docx4JException */
	void openDocx(File file) throws FileNotFoundException, Docx4JException
	{
		var newClasses = Extractor.extract(new FileInputStream(file),Optional.of(s->Log.warn(s, window)));
		var newText = Extractor.extractText(new FileInputStream(file));

		Platform.runLater(()->
		{
			this.state.classes.setAll(newClasses);
			this.state.triples.clear();
			this.state.text.set(newText);
			refresh();
		});
	}		


	private class UnclosableTab extends Tab
	{
		UnclosableTab(String text, Node content)
		{
			super(text,content);
			setClosable(false);			
		}
	}

	/** Setup the GUI and load an example tagged DOCX file. Called automatically with "mvn javafx:run".*/
	@Override
	public void start(Stage stage)
	{		
		this.stage=stage;	
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
		textPane.getItems().addAll(textArea,textRelationPane);		
		{
			TabPane tabPane = new TabPane();
			tabPane.setTabDragPolicy(TabDragPolicy.REORDER);			

			tabPane.getTabs().addAll(
					new UnclosableTab("Text", textPane),
					new UnclosableTab("Klassen", tableView),
					new UnclosableTab("Verbindungen", tripleTable),
					new UnclosableTab("RDF", rdfText));


			pane.getChildren().add(tabPane);
		}

		//openDocx(new File("src/main/resources/eu/snik/tag/benchmark.docx")); // use resource after finished refactoring  
	}

	public static void main(String[] args) {launch();} // Running this directly may fail. Use "mvn javafx:run" instead. 

	public void openSnikt(InputStream in) throws IOException
	{
		State state = new State(in);
		Platform.runLater(()->
		{
			//textArea.setText(state.text.get());
			this.state.text.set(state.text.get());
			this.state.classes.setAll(state.classes);
			this.state.triples.setAll(state.triples);
			refresh();
		});
	}

	public void saveSnikt(File f)
	{
		try
		{
			this.state.save(new FileOutputStream(f));
		}
		catch(Exception e) {throw new RuntimeException(e);}
	}

}