package eu.snik.tag.gui;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import eu.snik.tag.Clazz;
import eu.snik.tag.Triple;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/** Table of RDF classes with a filter search bar. */
public class TripleTable extends VBox
{
	final ObservableList<Clazz> classes;
	final ObservableList<Triple> triples = FXCollections.observableArrayList();
	//final Runnable update;
	final TableView<Triple> table;

	private TextField filterField = new TextField();
		
	public void refresh()
	{
		var triples = classes.stream().flatMap(c->c.getTriples().stream()).collect(Collectors.toList());
		this.triples.setAll(triples);
		System.out.println(triples);
	}
	
	/** @param classes may still be empty at constructor call time
	 * @param update	 callback that is run when the user changes a class.
	 * This is necessary because an observable list's change listeners only fire when a class is added or removed, not changed.*/
	public TripleTable(final ObservableList<Clazz> classes,final Runnable update)
	{
		this.classes = classes;
		refresh();
		this.classes.addListener((ListChangeListener)x->{refresh();});
		//this.update = update;
		this.table = new TableView<Triple>(); 
		
		table.setEditable(true);
		table.setMinHeight(1000);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.getChildren().addAll(filterField,table);

//		final var filteredClasses = new FilteredList<Triple>(triples);
//		table.setItems(filteredClasses);
//		//this.getItems().addAll(filteredClasses);
//
//		filterField.textProperty().addListener((observable, oldValue, newValue) ->
//		{
//			filteredClasses.setPredicate(Triple ->
//			{
//				if (newValue == null || newValue.isEmpty()) {return true;}
//				String rowText = Triple.labelString()+" "+Triple.localName+" "+Triple.subtop.name().replace('_',' ');
//				return rowText.toLowerCase().contains(newValue.toLowerCase());
//			});
//		});

		var subjectCol = new TableColumn<Triple,Set<String>>("Label");
		subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
		//subjectCol.setCellFactory(TextFieldTableCell.<Triple,Set<String>>forTableColumn(CollectionStringConverter.INSTANCE));

//		subjectCol.setMinWidth(600);
//		subjectCol.setOnEditCommit(e->
//		{
//			e.getRowValue().labels.clear();
//			e.getRowValue().labels.addAll(e.getNewValue());
//			update.run();
//		});
//
//		var localNameCol = new TableColumn<Triple,String>("Local Name");
//		localNameCol.setCellValueFactory(new PropertyValueFactory<>("localName"));
//		localNameCol.setCellFactory(TextFieldTableCell.<Triple>forTableColumn());
//		localNameCol.setMinWidth(350);
//		localNameCol.setOnEditCommit(e->
//		{
//			e.getRowValue().localName=e.getNewValue();
//			update.run();
//		});
//

		var removeCol = new RemoveColumn<Triple>("Entfernen", "x", triples::remove, ()->{});
		
		table.getColumns().addAll(subjectCol,removeCol);
	}

}

