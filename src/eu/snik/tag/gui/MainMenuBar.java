package eu.snik.tag.gui;

import java.io.File;
import java.io.FileWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainMenuBar
{
	private MainMenuBar() {};

	static Menu fileMenu(Main main)
	{
		Menu fileMenu = new Menu("_Datei");
		MenuItem openItem = new MenuItem("D_OCX Datei Öffnen");
		fileMenu.getItems().add(openItem);
		openItem.setAccelerator(new KeyCodeCombination(KeyCode.O,KeyCombination.CONTROL_DOWN));
		var openChooser = new FileChooser();
		openChooser.getExtensionFilters().add(new ExtensionFilter("DOCX", "*.docx"));
		openItem.setOnAction(e->
		{
			try
			{
				File file = openChooser.showOpenDialog(main.stage);
				if(file!=null) {main.openDocx(file);}
			}
			catch(Exception ex) {new ExceptionAlert(ex).show();}
		});
	
		MenuItem saveRdfItem = new MenuItem("_RDF Turtle Datei Exportieren");
		fileMenu.getItems().add(saveRdfItem);
		saveRdfItem.setAccelerator(new KeyCodeCombination(KeyCode.R,KeyCombination.CONTROL_DOWN));
		var saveRdfChooser = new FileChooser();
		saveRdfChooser.getExtensionFilters().add(new ExtensionFilter("Turtle", "*.ttl"));
		saveRdfItem.setOnAction(e->
		{
			File file = saveRdfChooser.showSaveDialog(main.stage);
			if(file!=null)
			{
				try(FileWriter writer = new FileWriter(file))
				{
					{
						RDFArea.rdfModel(main.classes).write(writer,"TURTLE");
					}
				}
				catch(Exception ex) {new ExceptionAlert(ex).show();}
			}
		});
		return fileMenu;
	}

	public static MenuBar create(Main main)
	{
		Menu fileMenu = fileMenu(main);

		Menu helpMenu = new Menu("_Hilfe");
		MenuItem aboutItem = new MenuItem("Ü_ber");		
		Alert aboutAlert = new Alert(AlertType.INFORMATION);
		aboutItem.setOnAction(e->aboutAlert.show());
		aboutAlert.setTitle("Über");
		aboutAlert.setHeaderText("SNIK Tag 0.0.1");
		aboutAlert.setContentText("JavaFX "+ System.getProperty("javafx.version") + ", running on Java " + System.getProperty("java.version") + ".");		
		helpMenu.getItems().add(aboutItem);

		return new MenuBar(fileMenu,helpMenu);			
	}

}
