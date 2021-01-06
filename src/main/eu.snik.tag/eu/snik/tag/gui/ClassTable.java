package eu.snik.tag.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.text.CaseUtils;
import eu.snik.tag.Clazz;
import eu.snik.tag.Relation;
import eu.snik.tag.Subtop;
import eu.snik.tag.Triple;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
	final Runnable createRestorePoint;
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
							createRestorePoint.run();
							classOperation.accept(clazz);
						});
			}
		});
		return removeCol;
	}

	/** Merges all selected classes minus if included the class where the button is clicked (the mergees) into the specified one (the merger).
	 *  @param merger the target class which will still exist at the end*/
	private void merge(Clazz merger)
	{
		createRestorePoint.run();
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
		// select and focus merger
		table.requestFocus();
		table.getSelectionModel().clearSelection();
		table.getSelectionModel().select(merger);
	}

	private void split(Clazz splitter)
	{
		if(splitter.labels.size()<2)
		{
			Log.warn("Only "+splitter.labels.size()+" labels. Nothing to split. Aborting.", this.getScene().getWindow());
			System.out.println(splitter.labels);
			return;
		}
		createRestorePoint.run();
		var splitees = new HashSet<Clazz>();
		for(String label: splitter.labels)
		{
			var name = CaseUtils.toCamelCase(label,true, new char[] {' ','-','_','.'}).replaceAll("[^A-Za-z0-9]", "");
			Clazz splitee = new Clazz(label, name, splitter.subtop);
			splitees.add(splitee);
		}

		state.classes.removeAll(splitter);
		state.classes.addAll(splitees);
		// select and focus splitees
		table.requestFocus();
		table.getSelectionModel().clearSelection();

	}

	/** @param classes may still be empty at constructor call time
	 * @param createRestorePoint	 callback that is run when the user changes a class.
	 * This is necessary because an observable list's change listeners only fire when a class is added or removed, not changed.*/
	public ClassTable(final State state,final Runnable createRestorePoint)
	{
		this.state = state;
		this.createRestorePoint = createRestorePoint;
		this.table = new TableView<Clazz>();

		table.setEditable(true);
		table.setMinHeight(1000);
		table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.getChildren().addAll(filterField,table);

		final var filteredClasses = new FilteredList<Clazz>(state.classes);
		final var sortedClasses = new SortedList<>(filteredClasses);
		sortedClasses.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedClasses);
		//table.setItems(state.classes);
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
			if(e.getOldValue().equals(e.getNewValue())) {return;}
			e.getRowValue().labels.clear();
			e.getRowValue().labels.addAll(e.getNewValue());
			createRestorePoint.run();
		});

		var localNameCol = new TableColumn<Clazz,String>("Local Name");
		localNameCol.setCellValueFactory(new PropertyValueFactory<>("localName"));
		localNameCol.setCellFactory(TextFieldTableCell.<Clazz>forTableColumn());
		localNameCol.setMinWidth(350);
		localNameCol.setOnEditCommit(e->
		{
			if(e.getOldValue().equals(e.getNewValue())) {return;}
			createRestorePoint.run();

			Clazz newClass = e.getRowValue().replaceLocalName(e.getNewValue());
			Clazz oldClass = e.getRowValue();
			state.triples.filtered((t)->t.subject==oldClass).forEach(t->{state.triples.add(t.replaceSubject(newClass));});
			state.triples.filtered((t)->t.object==oldClass).forEach(t->{state.triples.add(t.replaceObject(newClass));});			
			state.classes.remove(oldClass); // deletes the old triples
			state.classes.add(e.getTablePosition().getRow(),newClass);
			table.getSelectionModel().clearAndSelect(e.getTablePosition().getRow());
			table.getFocusModel().focus(e.getTablePosition().getRow());

			createRestorePoint.run();
		});


		var subtopCol = new TableColumn<Clazz,Subtop>("Type");
		subtopCol.setCellValueFactory(new PropertyValueFactory<>("subtop"));
		subtopCol.setCellFactory(ComboBoxTableCell.forTableColumn(Subtop.values()));
		subtopCol.setMinWidth(300);
		subtopCol.setOnEditCommit(e->
		{
			if(e.getOldValue().equals(e.getNewValue())) {return;}
			createRestorePoint.run();
			Clazz newClass = e.getRowValue().replaceSubtop(e.getNewValue());			
			Clazz oldClass = e.getRowValue();

			var remove = new ArrayList<Triple>();
			var add = new ArrayList<Triple>();

			for(Triple t: state.triples)
			{
				boolean changed = false;
				Clazz subject = t.subject;
				Clazz object = t.object;
				Relation predicate = t.predicate;

				if(t.subject==oldClass) {changed=true;subject=newClass;}
				if(t.object==oldClass) {changed=true;object=newClass;}
				if(changed&&!(predicate.domain.contains(subject.subtop)&&predicate.range.contains(object.subtop)))
				{
					Log.warn("Relation "+t.predicate+" had to be changed to isAssociatedWith in triple "+t+" due to subtop change of class "+oldClass+" to "+e.getNewValue(),this.getScene().getWindow());
					predicate = Relation.isAssociatedWith;
				}
				if(changed)
				{
					remove.add(t);
					add.add(new Triple(subject,predicate,object));					
				}
			}

			state.triples.removeAll(remove);
			state.classes.remove(oldClass);
			state.classes.add(e.getTablePosition().getRow(),newClass);
			table.getSelectionModel().clearAndSelect(e.getTablePosition().getRow());
			table.getFocusModel().focus(e.getTablePosition().getRow());
			state.triples.addAll(add);			
		});

		var removeCol = buttonCol("Entfernen", "x", state.classes::remove);

		var mergeCol = buttonCol("Zusammenführen", "Zusammenführen", this::merge);
		mergeCol.setMinWidth(150);

		var splitCol = buttonCol("Trennen", "Trennen", this::split);
		splitCol.setMinWidth(120);

		table.getColumns().addAll(labelCol,localNameCol,subtopCol,removeCol,mergeCol,splitCol);		
		this.table.getSortOrder().addAll(subtopCol,localNameCol,labelCol);
	}

}
