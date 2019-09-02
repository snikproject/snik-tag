package eu.snik.tag.gui;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import eu.snik.tag.Clazz;
import eu.snik.tag.Subtop;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
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
	final ObservableList<Clazz> classes;
	final Runnable update;
	final TableView<Clazz> table;

	private TextField filterField = new TextField();

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

	/** Merges all selected classes into the specified one.
	 *  @param clazz the target class which will still exist at the end*/
	private void merge(Clazz clazz)
	{
		var selected = new HashSet<>(table.getSelectionModel().getSelectedItems());
		
		if(selected.stream().filter(s->s.subtop!=clazz.subtop).findAny().isPresent())
		{
			System.err.println("Zusammenführen nicht möglich: Unterschiedliche Typen.");
			return;
		}
		
		selected.remove(clazz);
		// Move triples where a selected class is subject  
		
//		for(Clazz sel: selected)
//		{
//			clazz.labels.addAll(sel.labels);
//			for(Triple t: triples)
//			{
//				clazz.addTriple(t.predicate, t.object);				
//			}
//		}
//		
//		// Move triples where a selected class is object
//		
//		for(Clazz c: classes)
//		{
//			for(Triple t: c.getTriples())
//			{
//				if(selected.contains(t.object)) {t.object = clazz;}
//			}
//		}
		
		classes.removeAll(selected);
	}
		
	/** @param classes may still be empty at constructor call time
	 * @param update	 callback that is run when the user changes a class.
	 * This is necessary because an observable list's change listeners only fire when a class is added or removed, not changed.*/
	public ClassTable(final ObservableList<Clazz> classes,final Runnable update)
	{
		this.classes = classes;
		this.update = update;
		this.table = new TableView<Clazz>(); 
		
		table.setEditable(true);
		table.setMinHeight(1000);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.getChildren().addAll(filterField,table);

		final var filteredClasses = new FilteredList<Clazz>(classes);
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

		var removeCol = buttonCol("Entfernen", "x", classes::remove);
		
		var mergeCol = buttonCol("Zusammenführen", "Zusammenführen", this::merge);
		mergeCol.setMinWidth(150);
		
		table.getColumns().addAll(labelCol,localNameCol,subtopCol,removeCol,mergeCol);
	}

}

