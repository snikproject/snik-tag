package eu.snik.tag;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class Main extends Application
{
	final TextArea rdfText = new TextArea("Ihr extrahierter Text");
	final TableView tableView = tableView();

	Collection<Clazz> classes = Collections.emptyList();

	void update()
	{
		rdfText.setText(classes.toString());
	}

	@SneakyThrows
	void openDocx(File file)
	{
		classes = Extractor.extract(file);
		update();
		tableView.getItems().clear();
		tableView.getItems().addAll(classes);
	}


	TableView<Clazz> tableView()
	{
		var tableView = new TableView<Clazz>();
		tableView.setEditable(true);		

		var labelCol = new TableColumn<Clazz,String>("Label");
		labelCol.setCellValueFactory(new PropertyValueFactory<>("label"));
		labelCol.setCellFactory(TextFieldTableCell.<Clazz>forTableColumn());
		labelCol.setMinWidth(300);
		labelCol.setOnEditCommit(e->
		{
			e.getRowValue().setLabel(e.getNewValue());
			update();
		});

		var localNameCol = new TableColumn<Clazz,String>("Local Name");
		localNameCol.setCellValueFactory(new PropertyValueFactory<>("localName"));
		localNameCol.setMinWidth(300);
		localNameCol.setOnEditCommit(e->
		{
			e.getRowValue().setLocalName(e.getNewValue());
			update();
		});


		var subtopCol = new TableColumn<Clazz,Subtop>("Type");
		subtopCol.setCellValueFactory(new PropertyValueFactory<>("subtop"));
		subtopCol.setMinWidth(300);

		var relationCol = new TableColumn<Clazz,Object>("Type");
		
		var removeCol = new TableColumn<Clazz,Clazz>("Entfernen");
		removeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		removeCol.setCellFactory(param -> new TableCell<Clazz,Clazz>()
		{
			final Button deleteButton = new Button("X");

			@Override
			protected void updateItem(Clazz clazz, boolean empty)
			{
				super.updateItem(clazz, empty);

				if (clazz == null) {
					setGraphic(null);
					return;
				}

				setGraphic(deleteButton);
				deleteButton.setOnAction(
						event -> 
						{
							getTableView().getItems().remove(clazz);
							classes.remove(clazz);
							update();
						}
						);
			}
		});

		tableView.getColumns().addAll(labelCol,localNameCol,subtopCol,removeCol);

		return tableView;
	}


	@Override
	public void start(Stage stage)
	{		
		String javaVersion = System.getProperty("java.version");
		String javafxVersion = System.getProperty("javafx.version");		

		var pane = new VBox();
		{
			pane.setAlignment(Pos.CENTER);
			Scene scene = new Scene(pane, 1600, 1000);
			stage.setScene(scene);
			stage.show();
		}
		pane.getChildren().add(new Label(javafxVersion + ", running on Java " + javaVersion + "."));

		var openChooser = new FileChooser();		
		openChooser.setTitle(".docx Datei öffnen");
		var openButton = new Button(".docx Datei öffnen");


		pane.getChildren().add(openButton);

		rdfText.setMinSize(300, 500);		

		openButton.setOnAction(e->
		{
			File file = openChooser.showOpenDialog(stage);
			if(file!=null) {openDocx(file);}
		});


		var relationPane = new VBox();
		{
			relationPane.setAlignment(Pos.CENTER);
			Label l = new Label("Ziehen Sie bitte zwei Klassen in die Felder, wählen Sie die passende Relation aus und bestätigen.");
			relationPane.getChildren().addAll(l);
		}

		{
			TabPane tabPane = new TabPane();
			Tab rdfTab = new Tab();
			{
				rdfTab.setClosable(false);
				rdfTab.setText("RDF");
				rdfTab.setContent(rdfText);			
			}
			Tab tab = new Tab();
			{
				tab.setClosable(false);
				tab.setText("Klassen");
				tab.setContent(tableView);
			}
			tabPane.getTabs().addAll(rdfTab,tab);
			pane.getChildren().add(tabPane);
		}
		openDocx(new File("../benchmark/input.docx"));
	}

	public static void main(String[] args) {
		launch();		
	}

}