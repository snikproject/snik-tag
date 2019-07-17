package eu.snik.tag.gui;

import java.util.Arrays;
import java.util.stream.Collectors;
import eu.snik.tag.Clazz;
import eu.snik.tag.Relation;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Lets the user add relations that are allowed by the meta model between any two classes.
 * Two instances of relation pane are used in different tabs (alone and together with the text).
 * There are subtop combinations with no viable relation in that direction. */
public class RelationPane extends VBox
{
	private final ComboBox<Clazz> subjectBox = new ComboBox<>();
	private final ComboBox<Clazz> objectBox = new ComboBox<>();
	private final ComboBox<Relation> predicateBox = new ComboBox<>();
	
	/** 
	 * @param classes selected classes that are removed will automatically be deselected. Added classes are automatically added.
	 * Modified classes are not updated.  
	 * @param addCallback will be called when the user adds a new relation. No parameters.
	 */ 
	RelationPane(ObservableList<Clazz> classes, Runnable addCallback)
	{
		setAlignment(Pos.CENTER);
		Label l = new Label("Wählen Sie bitte zwei Klassen und eine passende Relation aus.");			

		subjectBox.setItems(classes);
		objectBox.setItems(classes);		
		
		Button addButton = new Button("Verbindung hinzufügen");
		{
			subjectBox.setPromptText("Subjekt auswählen");
			objectBox.setPromptText("Objekt auswählen");
			predicateBox.setPromptText("Verbindung auswählen");

			ChangeListener<Clazz> classListener = (ov,old,neww)->
			{
				if(objectBox.getValue()==null||subjectBox.getValue()==null)
				{
					predicateBox.getItems().clear();
					return;
				}
				predicateBox.setItems(FXCollections.observableArrayList(
						Arrays
						.stream(Relation.values())
						.filter(r->r.domain==subjectBox.getValue().subtop&&r.range==objectBox.getValue().subtop)
						.collect(Collectors.toList())
						));
			};
			
			subjectBox.valueProperty().addListener(classListener);
			objectBox.valueProperty().addListener(classListener);

			addButton.setOnAction(e->
			{
				if(subjectBox.getValue()==null||objectBox.getValue()==null||predicateBox.getValue()==null) {return;}
				subjectBox.getValue().addTriple(predicateBox.getValue(), objectBox.getValue());
				addCallback.run();
				subjectBox.setValue(null);
				objectBox.setValue(null);
				predicateBox.setValue(null);
			});
		}
		getChildren().addAll(l,subjectBox,objectBox,predicateBox,addButton);
	}

	public void setSubject(Clazz first)	{subjectBox.setValue(first);}
	public void setObject(Clazz first)	{objectBox.setValue(first);}

}
