package eu.snik.tag.gui;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import eu.snik.tag.Clazz;
import eu.snik.tag.Subtop;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;

/** Table of RDF classes with a filter search bar. */
public class ClassTable extends VBox
{
	final State state;
	final Runnable update;
	final TableView<Clazz> table;

	private TextField filterField = new TextField();
	{filterField.setPromptText("Klassen durchsuchen");}

	TableColumn<Clazz,Clazz> buttonCol(String columnText, String buttonText, final Consumer<Clazz> classOperation)
	{
		var removeCol = new TableColumn<Clazz,Clazz>(columnText);	
		removeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		removeCol.setCellFactory(param -> new TableCell<Clazz,Clazz>()
		{
			final Button deleteButton = new Button(buttonText);

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
							classOperation.accept(clazz);
							update.run();
						});
			}
		});
		return removeCol;
	}

	/** Merges all selected classes minus if included the class where the button is clicked (the mergees) into the specified one (the merger).
	 *  @param merger the target class which will still exist at the end*/
	private void merge(Clazz merger)
	{
		var mergees = new HashSet<>(table.getSelectionModel().getSelectedItems());
		mergees.remove(merger);

		if(mergees.stream().filter(mergee->mergee.subtop!=merger.subtop).findAny().isPresent())
		{
			Log.warn("Zusammenführen nicht möglich: Unterschiedliche Typen.",this.getScene().getWindow());
			return;
		}
		{
			var invalidSubjectTriples = state.triples.stream().filter(t->mergees.contains(t.subject));
			var validSubjectTriples = invalidSubjectTriples.map(t->t.replaceSubject(merger)).collect(Collectors.toList());

			state.triples.remove(invalidSubjectTriples);
			state.triples.addAll(validSubjectTriples);
		}
		{
			var invalidObjectTriples = state.triples.stream().filter(t->mergees.contains(t.object));
			var validObjectTriples = invalidObjectTriples.map(t->t.replaceObject(merger)).collect(Collectors.toList());

			state.triples.remove(invalidObjectTriples);
			state.triples.addAll(validObjectTriples);
		}
				
		for(Clazz mergee: mergees) {merger.labels.addAll(mergee.labels);}

		state.classes.removeAll(mergees);
	}

	/** @param classes may still be empty at constructor call time
	 * @param update	 callback that is run when the user changes a class.
	 * This is necessary because an observable list's change listeners only fire when a class is added or removed, not changed.*/
	public ClassTable(final State state,final Runnable update)
	{
		this.state = state;
		this.update = update;
		this.table = new TableView<Clazz>(); 

		table.setEditable(true);
		table.setMinHeight(1000);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.getChildren().addAll(filterField,table);

		final var filteredClasses = new FilteredList<Clazz>(state.classes);
		table.setItems(filteredClasses);
		//this.getItems().addAll(filteredClasses);

		filterField.textProperty().addListener((observable, oldValue, newValue) ->
		{
			filteredClasses.setPredicate(clazz ->
			{
				if (newValue == null || newValue.isEmpty()) {return true;}
				String rowText = clazz.labelString()+" "+clazz.localName+" "+clazz.subtop.name().replace('_',' ');
				return rowText.toLowerCase().contains(newValue.toLowerCase());
			});
		});

		var labelCol = new TableColumn<Clazz,Set<String>>("Label");
		labelCol.setCellValueFactory(new PropertyValueFactory<>("labels"));
		labelCol.setCellFactory(TextFieldTableCell.<Clazz,Set<String>>forTableColumn(CollectionStringConverter.INSTANCE));

		labelCol.setMinWidth(600);
		labelCol.setOnEditCommit(e->
		{
			e.getRowValue().labels.clear();
			e.getRowValue().labels.addAll(e.getNewValue());
			update.run();
		});

		var localNameCol = new TableColumn<Clazz,String>("Local Name");
		localNameCol.setCellValueFactory(new PropertyValueFactory<>("localName"));
		localNameCol.setCellFactory(TextFieldTableCell.<Clazz>forTableColumn());
		localNameCol.setMinWidth(350);
		localNameCol.setOnEditCommit(e->
		{
			e.getRowValue().localName=e.getNewValue();
			update.run();
		});


		var subtopCol = new TableColumn<Clazz,Subtop>("Type");
		subtopCol.setCellValueFactory(new PropertyValueFactory<>("subtop"));
		subtopCol.setCellFactory(ComboBoxTableCell.forTableColumn(Subtop.values()));
		subtopCol.setMinWidth(300);
		subtopCol.setOnEditCommit(e->
		{
			e.getRowValue().subtop=e.getNewValue();
			update.run();
		});

		var removeCol = buttonCol("Entfernen", "x", state.classes::remove);

		var mergeCol = buttonCol("Zusammenführen", "Zusammenführen", this::merge);
		mergeCol.setMinWidth(150);

		table.getColumns().addAll(labelCol,localNameCol,subtopCol,removeCol,mergeCol);
	}

}

