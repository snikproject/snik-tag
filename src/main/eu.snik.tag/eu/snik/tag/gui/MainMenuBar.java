package eu.snik.tag.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import eu.snik.tag.Clazz;
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

/** File and Help menu. */
public class MainMenuBar
{
	private MainMenuBar() {};

	static Menu fileMenu(Main main)
	{
		Menu fileMenu = new Menu("_Datei");
		{
			MenuItem openDocxItem = new MenuItem("_DOCX Datei Öffnen");
			fileMenu.getItems().add(openDocxItem);
			openDocxItem.setAccelerator(new KeyCodeCombination(KeyCode.D,KeyCombination.CONTROL_DOWN));
			var openChooser = new FileChooser();
			openChooser.getExtensionFilters().add(new ExtensionFilter("DOCX", "*.docx"));
			openDocxItem.setOnAction(e->
			{
				File file = openChooser.showOpenDialog(main.stage);
				if(file!=null) {main.openDocx(file);}
			});
		}
		{
			MenuItem openTaggedItem = new MenuItem("Ann_otierte Datei Öffnen");
			fileMenu.getItems().add(openTaggedItem);
			openTaggedItem.setAccelerator(new KeyCodeCombination(KeyCode.O,KeyCombination.CONTROL_DOWN));
			var openChooser = new FileChooser();
			openChooser.getExtensionFilters().add(new ExtensionFilter("SNIKT", "*.snikt"));
			openTaggedItem.setOnAction(e->
			{
				try
				{
					File file = openChooser.showOpenDialog(main.stage);
					if(file!=null) {main.openSnikt(new FileInputStream(file));}
				}
				catch(Exception ex) {new ExceptionAlert(ex).show();}
			});
		}

		{
			MenuItem saveTaggedItem = new MenuItem("Annotierte Datei _Speichern");
			fileMenu.getItems().add(saveTaggedItem);
			saveTaggedItem.setAccelerator(new KeyCodeCombination(KeyCode.S,KeyCombination.CONTROL_DOWN));
			var saveChooser = new FileChooser();
			saveChooser.getExtensionFilters().add(new ExtensionFilter("SNIKT", "*.snikt"));
			saveTaggedItem.setOnAction(e->
			{
				try
				{
					File f = saveChooser.showSaveDialog(main.stage);
					if(!f.getName().contains(".")) {f = new File(f.getAbsolutePath() + ".snikt");}

					if(f!=null) {main.saveSnikt(f);}
				}
				catch(Exception ex) {new ExceptionAlert(ex).show();}
			});
		}

		{
			MenuItem saveRdfItem = new MenuItem("_RDF Turtle Datei Exportieren");
			fileMenu.getItems().add(saveRdfItem);
			saveRdfItem.setAccelerator(new KeyCodeCombination(KeyCode.R,KeyCombination.CONTROL_DOWN));
			var saveRdfChooser = new FileChooser();
			saveRdfChooser.getExtensionFilters().add(new ExtensionFilter("Turtle", "*.ttl"));
			saveRdfItem.setOnAction(e->
			{
				File f = saveRdfChooser.showSaveDialog(main.stage);
				if(!f.getName().contains(".")) {f = new File(f.getAbsolutePath() + ".ttl");}
				if(f!=null)
				{
					try(FileWriter writer = new FileWriter(f))
					{
						{
							RDFArea.rdfModel(main.classes).write(writer,"TURTLE");
						}
					}
					catch(Exception ex) {new ExceptionAlert(ex).show();}
				}
			});
		}

		{
			MenuItem saveJsonItem = new MenuItem("Cytoscape _JSON Exportieren");
			fileMenu.getItems().add(saveJsonItem);
			saveJsonItem.setAccelerator(new KeyCodeCombination(KeyCode.J,KeyCombination.CONTROL_DOWN));
			var saveJsonChooser = new FileChooser();
			saveJsonChooser.getExtensionFilters().add(new ExtensionFilter("JSON", "*.json"));
			saveJsonItem.setOnAction(e->
			{
				File f = saveJsonChooser.showSaveDialog(main.stage);
				if(!f.getName().contains(".")) {f = new File(f.getAbsolutePath() + ".json");}
				if(f!=null)
				{
					try(FileWriter writer = new FileWriter(f))
					{
						{
							writer.write(Clazz.cytoscapeElements(main.classes).toString(2));
						}
					}
					catch(Exception ex) {new ExceptionAlert(ex).show();}
				}
			});
		}

		return fileMenu;
	}

	//	static Menu optionsMenu()
	//	{
	//		Menu optionsMenu = new Menu("_Optionen");
	//		MenuItem developerItem = new CheckMenuItem("Developer Mode");		
	//		
	//		optionsMenu.getItems().addAll(developerItem);
	//		return optionsMenu;
	//	}

	static MenuItem about()
	{
		MenuItem about = new MenuItem("Ü_ber");

		try
		{
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
		}
		catch(IOException e) {System.err.println("Couldn't load GIT information.");}

		return about;


	}

	static void browse(String uri)
	{
		if(!(Desktop.isDesktopSupported()&&Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)))
		{
			new Alert(AlertType.WARNING, "Kann Website nicht öffnen, nicht unterstützt.").show();;
			System.err.println("Cannot open Website, not supported.");
			return;
		};
		// Using the JavaFX thread for desktop hangs the program.
		new Thread(()->{
			try{Desktop.getDesktop().browse(new URI(uri));} catch (Exception e) {throw new RuntimeException(e);}
		}).start();
	}

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
