import java.io.File;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class Main extends Application
{
	final TextArea rdfText = new TextArea("Ihr extrahierter Text");
	final TableView tableView = tableView();
	
	@SneakyThrows
	void openDocx(File file)
	{
		var classes = Extractor.extract(file);
		
		rdfText.setText(classes.toString());
		tableView.getItems().clear();
		tableView.getItems().addAll(classes);
	}


	TableView tableView()
	{
		var tableView = new TableView<>();
		
		var nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setMinWidth(300);

		var uriColumn = new TableColumn<>("URI");
		uriColumn.setCellValueFactory(new PropertyValueFactory<>("uri"));
		uriColumn.setMinWidth(300);
		
		var subtopColumn = new TableColumn<>("Type");
		subtopColumn.setCellValueFactory(new PropertyValueFactory<>("subtop"));
		subtopColumn.setMinWidth(300);
		
		var removeCol = new TableColumn<>("Entfernen");
		removeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		removeCol.setCellFactory(param -> new TableCell<Object,Object>() {
		    private final Button deleteButton = new Button("X");

		    @Override
		    protected void updateItem(Object person, boolean empty) {
		        super.updateItem(person, empty);

		        if (person == null) {
		            setGraphic(null);
		            return;
		        }

		        setGraphic(deleteButton);
		        deleteButton.setOnAction(
		            event -> getTableView().getItems().remove(person)
		        );
		    }
		});
		
		tableView.getColumns().addAll(nameColumn,uriColumn,subtopColumn,removeCol);
		
		return tableView;
	}


	@Override
	public void start(Stage stage) {
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
	}

	public static void main(String[] args) {
		launch();
	}

}