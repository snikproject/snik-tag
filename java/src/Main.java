import java.io.File;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;

public class Main extends Application
{

	@SneakyThrows
	String openDocx(File file)
	{
		return 	Extractor.extract(file);
	}


	@Override
	public void start(Stage stage) {
		String javaVersion = System.getProperty("java.version");
		String javafxVersion = System.getProperty("javafx.version");		

		var pane = new VBox();
		{
			pane.setAlignment(Pos.CENTER);
			Scene scene = new Scene(pane, 640, 720);
			stage.setScene(scene);
			stage.show();
		}
		pane.getChildren().add(new Label(javafxVersion + ", running on Java " + javaVersion + "."));

		var openChooser = new FileChooser();		
		openChooser.setTitle(".docx Datei öffnen");
		var openButton = new Button(".docx Datei öffnen");


		pane.getChildren().add(openButton);

		var rdfText = new TextArea("Ihr extrahierter Text");
		rdfText.setMinSize(300, 500);		

		openButton.setOnAction(e->
		{
			File file = openChooser.showOpenDialog(stage);
			if(file!=null) {rdfText.setText(openDocx(file));}
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
				tabPane.getTabs().add(rdfTab);
			}
			Tab tab = new Tab();
			{
				tab.setClosable(false);
				tab.setText("RDF");
				tab.setContent(rdfText);
			}
			tabPane.getTabs().addAll(rdfTab,tab);
			pane.getChildren().add(tabPane);
		}		
	}


	public static void main(String[] args) {
		launch();
	}

}