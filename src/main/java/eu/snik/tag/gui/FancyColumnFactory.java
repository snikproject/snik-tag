package eu.snik.tag.gui;

import eu.snik.tag.Clazz;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Contains static functions creating columns for {@link ClassTable}.
 */
public class FancyColumnFactory {
	/**
	 * Creates a free text column, with a Semicolon (";") being used as a separator for a new entry.
	 * Also creates restore point for Ctrl+Z etc.
	 * To add a new column, you also need to hardcode add it in {@link Clazz} at the moment.
	 * 
	 * @param columnLabel The label shown at the top of the column (heading in the first row of the table)
	 * @param propertyName The name of the property for export/saving purposes
	 * @param minWidth The minimum width of the cell.
	 * @param createRestorePoint Callback to create a restore point
	 * @return a new ready-to-use column which still needs to be added to the table
	 */
	public static TableColumn<Clazz, Set<String>> freeTextCol(String columnLabel, String propertyName, int minWidth, Runnable createRestorePoint) {
		var column = new TableColumn<Clazz, Set<String>>(columnLabel);
		column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
		column.setCellFactory(TextFieldTableCell.<Clazz, Set<String>>forTableColumn(CollectionStringConverter.INSTANCE));

		column.setMinWidth(minWidth);
		column.setOnEditCommit(
			e -> {
				if (e.getOldValue().equals(e.getNewValue())) {
					return;
				}
				e.getRowValue().get(propertyName).clear();
				e.getRowValue().get(propertyName).addAll(e.getNewValue());
				
				createRestorePoint.run();
			}
		);
		return column;
	}
	
	/**
	 * Creates a column with a button running a function when pressed in each cell
	 * @param columnText The label shown at the top of the column (heading in the first row of the table)
	 * @param buttonText The text on the buttons
	 * @param classOperation What's run when clicking the button
	 * @param createRestorePoint Callback to create a restore point
	 * @return A column full of buttons
	 */
	public static TableColumn<Clazz, Clazz> buttonCol(String columnText, String buttonText, final Consumer<Clazz> classOperation, Runnable createRestorePoint) {
		var removeCol = new TableColumn<Clazz, Clazz>(columnText);
		removeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		removeCol.setCellFactory(
			param ->
				new TableCell<Clazz, Clazz>() {
					final Button deleteButton = new Button(buttonText);

					@Override
					protected void updateItem(Clazz clazz, boolean empty) {
						super.updateItem(clazz, empty);

						if (clazz == null) {
							setGraphic(null);
							return;
						}

						setGraphic(deleteButton);
						deleteButton.setOnAction(
							event -> {
								createRestorePoint.run();
								classOperation.accept(clazz);
							}
						);
					}
				}
		);
		return removeCol;
	}
}
