package eu.snik.tag.gui;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import eu.snik.tag.Clazz;
import eu.snik.tag.Extractor;
import eu.snik.tag.State;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private String text = "";

	public final ObservableList<Clazz> classes = FXCollections.observableArrayList();	

	private final SplitPane textPane = new SplitPane();

	private final RDFArea rdfText = new RDFArea(classes);

	RelationPane textRelationPane = new RelationPane(classes,this::refresh);
	private ClassTextPane textArea = new ClassTextPane(classes,textRelationPane);

	private final ClassTable tableView = new ClassTable(classes, this::refresh);
	private final TripleTable tripleTable = new TripleTable(classes, this::refresh);

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
		this.text = newText;				

		Platform.runLater(()->
		{
			this.classes.clear();
			this.classes.addAll(newClasses);
			textArea.setText(this.text);
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
			textArea.setText(state.text);
			classes.clear();
			classes.addAll(state.classes);
			refresh();
		});
	}

	public void saveSnikt(File f)
	{
		try
		{
			new State(this.text,this.classes).save(new FileOutputStream(f));
		}
		catch(Exception e) {throw new RuntimeException(e);}
	}

}