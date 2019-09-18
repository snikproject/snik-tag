package eu.snik.tag.gui;

import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class RemoveColumn<T> extends TableColumn<T,T>
{
	RemoveColumn(String columnText, String buttonText, final Consumer<T> operation, final Runnable createRestorePoint)
	{
		super(columnText);	
		this.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		this.setCellFactory(param -> new TableCell<T,T>()
		{
			final Button deleteButton = new Button(buttonText);

			@Override
			protected void updateItem(T T, boolean empty)
			{
				super.updateItem(T, empty);

				if (T == null) {
					setGraphic(null);
					return;
				}

				setGraphic(deleteButton);
				deleteButton.setOnAction(
						event -> 
						{
							createRestorePoint.run();
							operation.accept(T);
						});
			}
		});
	}

}
