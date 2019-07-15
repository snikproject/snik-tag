package eu.snik.tag.gui;

import java.util.Collection;
import eu.snik.tag.Clazz;
import eu.snik.tag.Subtop;
import eu.snik.tag.Triple;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;

public class ClassTablePane extends VBox
{

	private TextField filterField = new TextField();

	public ClassTablePane(final ObservableList<Clazz> classes,final Runnable update)
	{
		var table = new TableView();		
		table.setEditable(true);
		table.setMinHeight(1000);
		this.getChildren().addAll(filterField,table);

		final var filteredClasses = new FilteredList<Clazz>(classes);
		table.setItems(filteredClasses);
		//this.getItems().addAll(filteredClasses);

		filterField.textProperty().addListener((observable, oldValue, newValue) ->
		{
			filteredClasses.setPredicate(clazz ->
			{
				if (newValue == null || newValue.isEmpty()) {return true;}
				String rowText = clazz.label+" "+clazz.localName+" "+clazz.subtop.name().replace('_',' ');
				return rowText.toLowerCase().contains(newValue.toLowerCase());
			});
		});

		var labelCol = new TableColumn<Clazz,String>("Label");
		labelCol.setCellValueFactory(new PropertyValueFactory<>("label"));
		labelCol.setCellFactory(TextFieldTableCell.<Clazz>forTableColumn());
		labelCol.setMinWidth(300);
		labelCol.setOnEditCommit(e->
		{
			e.getRowValue().setLabel(e.getNewValue());
			update.run();;
		});

		var localNameCol = new TableColumn<Clazz,String>("Local Name");
		localNameCol.setCellValueFactory(new PropertyValueFactory<>("localName"));
		localNameCol.setMinWidth(300);
		localNameCol.setOnEditCommit(e->
		{
			e.getRowValue().setLocalName(e.getNewValue());
			update.run();
		});


		var subtopCol = new TableColumn<Clazz,Subtop>("Type");
		subtopCol.setCellValueFactory(new PropertyValueFactory<>("subtop"));
		subtopCol.setMinWidth(300);		

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
							//getTableView().getItems().remove(clazz);
							classes.remove(clazz);
							update.run();
						}
						);
			}
		});

		var relationCol = new TableColumn<Clazz,Collection<Triple>>("Relations");
		relationCol.setCellValueFactory(new PropertyValueFactory<>("triples"));
		relationCol.setMinWidth(300);		

		table.getColumns().addAll(labelCol,localNameCol,subtopCol,removeCol,relationCol);
	}

}

