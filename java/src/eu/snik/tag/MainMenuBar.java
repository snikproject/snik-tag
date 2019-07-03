package eu.snik.tag;

import java.io.File;
import eu.snik.tag.util.ExceptionAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;

public class MainMenuBar
{
	private MainMenuBar() {};

	public static MenuBar create(Main main)
	{
		
		Menu fileMenu = new Menu("_Datei");
		{
			MenuItem openItem = new MenuItem("D_OCX Datei Öffnen");
			fileMenu.getItems().add(openItem);
			openItem.setAccelerator(new KeyCodeCombination(KeyCode.O,KeyCombination.CONTROL_DOWN));
			var openChooser = new FileChooser();		
			openItem.setOnAction(e->
			{
				try
				{
				File file = openChooser.showOpenDialog(main.stage);
				if(file!=null) {main.openDocx(file);}
				}
				catch(Exception ex) {new ExceptionAlert(ex).show();}
			});
		}
		
		
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
