package eu.snik.tag.gui;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.stage.Window;
import javafx.util.Duration;

public final class Log {

	public static void warn(String message, Window window) {
		Platform.runLater(
			() -> {
				Notifications.create().owner(window).title("Warnung").text(message).hideAfter(Duration.seconds(10 + Math.log10(message.length()))).showWarning();
				/*
			    VBox content = new VBox();
			    Pane backgroundPane = new Pane();
			    backgroundPane.getChildren().add(content);
			    backgroundPane.setStyle("-fx-background-color: white;");

			    Label messageLabel = new Label(message);
			    content.getChildren().add(messageLabel);

			    // Create the popup
			    Popup popup = new Popup();
			    popup.getContent().addAll(backgroundPane);
			    
			    // Add a button to close the popup (optional)
			    Button closeButton = new Button("Close");
			    closeButton.setOnAction(event -> popup.hide());
			    content.getChildren().add(closeButton);

			    // Create a PauseTransition to hide the popup after a delay
			    PauseTransition pause = new PauseTransition(Duration.seconds(8 + Math.log10(message.length())));
			    pause.setOnFinished(e -> popup.hide());

			    // Show the popup and start the auto-hide timer
			    popup.show(window);
			    pause.play();
			    */
			}
		);

		System.err.println("Warnung: " + message);
	}

	public static void warn(String message) {
		System.err.println(message);
	}

	public static void info(String message) {
		System.out.println(message);
	}

	public static void error(String title, Exception ex) {
		Platform.runLater(
			() -> {
				new ExceptionAlert(title, ex).show();
			}
		);
		//ex.printStackTrace();
	}
}
