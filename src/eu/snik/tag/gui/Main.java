package eu.snik.tag.gui;
import java.io.File;
import eu.snik.tag.Clazz;
import eu.snik.tag.Extractor;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;

/** GUI entry point. Run with Maven via javafx:run. */
public class Main extends Application
{
	public final ObservableList<Clazz> classes = FXCollections.observableArrayList();
	
	private final Pane textPane = new HBox();

	private final RDFArea rdfText = new RDFArea(classes);

	private ClassTextPane textArea = new ClassTextPane(classes);

	private final ClassTable tableView = new ClassTable(classes, this::refresh);

	public Stage stage;

	/** Refresh the different visualizations of the RDF classes. Use after classes are modified.*/
	public void refresh()
	{		
		textArea.refresh();
		rdfText.refresh();
	}

	/** @param file DOCX file with tagged entity types (italic), roles (bold) and functions (function).*/
	@SneakyThrows
	void openDocx(File file)
	{
		classes.clear();
		classes.addAll(Extractor.extract(file));
		textArea.setText(Extractor.extractText(file));
		
		refresh();		
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
		}		
		pane.getChildren().add(MainMenuBar.create(this));

		rdfText.setMinSize(300, 500);
		textArea.setMaxWidth(1000);
		textPane.getChildren().addAll(textArea,new RelationPane(classes,this::refresh));		
		{
			TabPane tabPane = new TabPane();
			tabPane.setTabDragPolicy(TabDragPolicy.REORDER);			

			tabPane.getTabs().addAll(
					new UnclosableTab("Text", textPane),
					new UnclosableTab("Klassen", tableView),
					new UnclosableTab("RDF", rdfText),
					new UnclosableTab("Verbindungen", new RelationPane(classes,this::refresh)));

			pane.getChildren().add(tabPane);
		}
		openDocx(new File("benchmark/input.docx"));
	}

	public static void main(String[] args) {launch();} // Running this directly may fail. Use "mvn javafx:run" instead. 

}