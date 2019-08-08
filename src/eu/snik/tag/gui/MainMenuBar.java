package eu.snik.tag.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

/** File and Help menu. */
@Log
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

	@SneakyThrows
	static MenuItem about()
	{
		MenuItem about = new MenuItem("Ü_ber");
		Properties git = new Properties();
		git.load(MainMenuBar.class.getClassLoader().getResourceAsStream("git.properties"));

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(1000);
		about.setOnAction(e->alert.show());
		alert.setTitle("Über");
		alert.setHeaderText("SNIK Tag "+git.getProperty("git.build.version"));
		alert.setContentText("JavaFX "+ System.getProperty("javafx.version") + ", running on Java " + System.getProperty("java.version") + ".\n"+
				"Built "+git.getProperty("git.build.time")+"\n"+
				"Commited "+git.getProperty("git.commit.time")+" ID "+git.getProperty("git.commit.id")+"\n"+
				"Commit Message "+git.getProperty("git.commit.message.short")+"\n"+
				"Committer "+git.getProperty("git.build.user.name")+" "+git.getProperty("git.build.user.email")+"\n"+
				"Nearest Tag "+git.getProperty("git.closest.tag.name")
				);
		return about;
	}

	static void browse(String uri)
	{
		if(!(Desktop.isDesktopSupported()&&Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)))
		{
			new Alert(AlertType.WARNING, "Kann Website nicht öffnen, nicht unterstützt.").show();;
			log.warning("Cannot open Website, not supported.");
			return;
		};
		// Using the JavaFX thread for desktop hangs the program.
		new Thread(()->{
			try{Desktop.getDesktop().browse(new URI(uri));} catch (Exception e) {throw new RuntimeException(e);}
		}).start();
	}

	@SneakyThrows
	static Menu helpMenu()
	{
		Menu helpMenu = new Menu("_Hilfe");

		MenuItem helpItem = new MenuItem("_Hilfe");
		helpItem.setOnAction(event->{browse("https://imise.github.io/snik-tag/");});

		MenuItem reportItem = new MenuItem("Bug oder Vorschlag melden");		
		reportItem.setOnAction(event->{browse("https://github.com/IMISE/snik-tag/issues");});

		helpMenu.getItems().addAll(helpItem,reportItem,about());

		return helpMenu;
	}

	public static MenuBar create(Main main)
	{
		return new MenuBar(fileMenu(main),helpMenu());			
	}

}
