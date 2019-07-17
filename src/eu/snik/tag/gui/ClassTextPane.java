package eu.snik.tag.gui;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import eu.snik.tag.Clazz;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Uneditable HTML text area with the DOCX text and highlighted classes.*/
public class ClassTextPane extends TextFlow
{

	final ObservableList<Clazz> classes;
	private final Map<Clazz,Text> texts = new HashMap<>();
	private final RelationPane relationPane;
	String text;

	/** @param classes added or removed classes will automatically be shown. 
	 * @param relationPane */
	public ClassTextPane(ObservableList<Clazz> classes, RelationPane relationPane)
	{
		this.classes=classes;
		this.relationPane=relationPane;
		this.	setMinSize(900, 1000);
		classes.addListener((ListChangeListener<Clazz>)(c)->{refresh();});
	}

	/** Keeps the classes and highlights them in a new text. */
	public void setText(String text)
	{
		this.text=text;		
		Platform.runLater(()->refresh());
	}

	/** Call when a class has changed its label. Also called automatically when a class is removed or added. */
	public void refresh()
	{
		getChildren().clear();

		String restText = text;
		Set<Clazz> restClasses = new HashSet<>(classes);

		Map<Clazz,Integer> indices;

		while(restText!=null&&!restText.isEmpty())
		{
			final String restTextFinal = restText;
			indices = restClasses.stream().collect(Collectors.toMap(c->c,c->restTextFinal.indexOf(c.getLabel())));
			restClasses.retainAll(indices.entrySet().stream().filter(e->e.getValue()>-1).map(Entry::getKey).collect(Collectors.toSet()));			
			Optional<Clazz> firstOpt = restClasses.stream().min(Comparator.comparing(indices::get));
			if(firstOpt.isEmpty()) {break;}
			Clazz first = firstOpt.get();			
			int pos = indices.get(first);

			if(pos>0) {getChildren().add(new Text(restText.substring(0, pos)));}
			
			Text classText = new Text(restText.substring(pos,pos+first.label.length()));
			classText.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->
			{
				if(e.getButton()==MouseButton.PRIMARY) {relationPane.setSubject(first);}
				else																																				{relationPane.setObject(first);}
			});
			switch(first.subtop)
			{
				case Role: 						classText.setFont(Font.font("Helvetica",FontWeight.BOLD,15));break;
				case Function:			classText.setFont(Font.font("Helvetica",FontPosture.ITALIC,15));break;
				case EntityType:	classText.setFont(Font.font(15));classText.setUnderline(true); break;
			}
			getChildren().add(classText);
			texts.put(first, classText);

			restText = restText.substring(pos+first.label.length());

		}
		if(restText!=null&&!restText.isBlank()) {getChildren().add(new Text(restText));}
	}

}
