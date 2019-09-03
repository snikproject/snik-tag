package eu.snik.tag.gui;

import java.util.Set;
import eu.snik.tag.Triple;
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
	final ObservableList<Triple> triples;
	//final Runnable update;
	final TableView<Triple> table;

	private TextField filterField = new TextField();
	{filterField.setPromptText("Verbindungen durchsuchen");}
		
//	public void refresh()
//	{
////		var triples = classes.stream().flatMap(c->c.getTriples().stream()).collect(Collectors.toList());
////		this.triples.setAll(triples);
////		System.out.println(triples);
//	}
	
	/** @param classes may still be empty at constructor call time
	 * @param update	 callback that is run when the user changes a class.
	 * This is necessary because an observable list's change listeners only fire when a class is added or removed, not changed.*/
	public TripleTable(final ObservableList<Triple> triples, final Runnable update)
	{
		this.triples = triples;
//		refresh();
//		this.classes.addListener((ListChangeListener)x->{refresh();});
		//this.update = update;
		this.table = new TableView<Triple>(triples); 
		
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

		var subjectCol = new TableColumn<Triple,Set<String>>("Subject");
		subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
		//subjectCol.setCellFactory(TextFieldTableCell.<Triple,Set<String>>forTableColumn(CollectionStringConverter.INSTANCE));
		subjectCol.setMinWidth(400);
		
//		subjectCol.setOnEditCommit(e->
//		{
//			e.getRowValue().labels.clear();
//			e.getRowValue().labels.addAll(e.getNewValue());
//			update.run();
//		});
//
		
		var predicateCol = new TableColumn<Triple,Set<String>>("Relation");
		predicateCol.setCellValueFactory(new PropertyValueFactory<>("predicate"));
		//subjectCol.setCellFactory(TextFieldTableCell.<Triple,Set<String>>forTableColumn(CollectionStringConverter.INSTANCE));
		predicateCol.setMinWidth(400);

		var objectCol = new TableColumn<Triple,Set<String>>("Object");
		objectCol.setCellValueFactory(new PropertyValueFactory<>("object"));
		//subjectCol.setCellFactory(TextFieldTableCell.<Triple,Set<String>>forTableColumn(CollectionStringConverter.INSTANCE));
		objectCol.setMinWidth(400);

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
		
		table.getColumns().addAll(subjectCol,predicateCol,objectCol,removeCol);
	}

}

