package eu.snik.tag.gui;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import eu.snik.tag.Clazz;
import eu.snik.tag.Extractor;
import eu.snik.tag.Relation;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class Main extends Application
{
	final TextArea rdfText = new TextArea("Ihr extrahierter Text");

	final ObservableList<Clazz> classes = FXCollections.observableArrayList();

	final ClassTableView tableView = new ClassTableView(classes, this::update);
	
	Stage stage;

	ComboBox<Clazz> subjectBox = new ComboBox<>();
	ComboBox<Clazz> objectBox = new ComboBox<>();
	ComboBox<Relation> predicateBox = new ComboBox<>();
	Button addButton = new Button("Verbindung hinzufügen");
	{
		subjectBox.setPromptText("Subjekt auswählen");
		objectBox.setPromptText("Objekt auswählen");
		predicateBox.setPromptText("Verbindung auswählen");

		ChangeListener<Clazz> classListener = (ov,old,neww)->
		{
			if(objectBox.getValue()==null||subjectBox.getValue()==null)
			{
				predicateBox.getItems().clear();
				return;
			}
			predicateBox.setItems(FXCollections.observableArrayList(
					Arrays
					.stream(Relation.values())
					.filter(r->r.domain==subjectBox.getValue().subtop&&r.range==objectBox.getValue().subtop)
					.collect(Collectors.toList())
					));
		};

		subjectBox.valueProperty().addListener(classListener);
		objectBox.valueProperty().addListener(classListener);
		
		addButton.setOnAction(e->
		{
			if(subjectBox.getValue()==null||objectBox.getValue()==null||predicateBox.getValue()==null) {return;}
			subjectBox.getValue().addTriple(predicateBox.getValue(), objectBox.getValue());
			subjectBox.setValue(null);
			objectBox.setValue(null);
			predicateBox.setValue(null);
		});
	}

	void update()
	{
		rdfText.setText(classes.toString());
		subjectBox.setItems(classes);
		objectBox.setItems(classes);		
	}

	@SneakyThrows
	void openDocx(File file)
	{
		classes.clear();
		classes.addAll(Extractor.extract(file));
		update();
		tableView.getItems().clear();
		tableView.getItems().addAll(classes);
	}
 

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

		var relationPane = new VBox();
		{
			relationPane.setAlignment(Pos.CENTER);
			Label l = new Label("Wählen Sie bitte zwei Klassen und eine passende Relation aus.");			

			relationPane.getChildren().addAll(l,subjectBox,objectBox,predicateBox,addButton);
		}

		Tab tableTab = new Tab();
		{
			tableTab.setClosable(false);
			tableTab.setText("Klassen");
			tableTab.setContent(tableView);
		}
		{
			TabPane tabPane = new TabPane();
			tabPane.setTabDragPolicy(TabDragPolicy.REORDER);
			Tab rdfTab = new Tab();
			{
				rdfTab.setClosable(false);
				rdfTab.setText("RDF");
				rdfTab.setContent(rdfText);			
			}
			Tab relationTab = new Tab();
			{
				relationTab.setClosable(false);
				relationTab.setText("Verbindungen");
				relationTab.setContent(relationPane);
			}

			tabPane.getTabs().addAll(tableTab,rdfTab,relationTab);
			pane.getChildren().add(tabPane);
		}
		openDocx(new File("benchmark/input.docx"));
	}

	public static void main(String[] args) {
		launch();		
	}

}