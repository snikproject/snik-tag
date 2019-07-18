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
import eu.snik.tag.Subtop;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Uneditable HTML text area with the DOCX text and highlighted classes.*/
public class ClassTextPane extends ScrollPane
{

	private final ObservableList<Clazz> classes;
	//private final Map<Clazz,Text> texts = new HashMap<>(); needs to be multi map but is it actually needed?
	private final RelationPane relationPane;
	private final TextFlow flow = new TextFlow();

	private String text;

	/** @param classes added or removed classes will automatically be shown. 
	 * @param relationPane */
	public ClassTextPane(ObservableList<Clazz> classes, RelationPane relationPane)
	{
		this.setContent(flow);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.classes=classes;
		this.relationPane=relationPane;
		this.	setMinWidth(700);		
		flow.prefWidthProperty().bind(this.widthProperty());
		classes.addListener((ListChangeListener<Clazz>)(c)->{refresh();});
	}

	/** Keeps the classes and highlights them in a new text. */
	public void setText(String text)
	{
		this.text=text;		
		Platform.runLater(()->refresh());
	}

	private final Map<Subtop,String> cssClass = Map.of
			(
			Subtop.EntityType,"-entity-type-fill: red;", // entity type as subject can only connect to itself
			Subtop.Function,"-entity-type-fill: red; -function-fill: lightgreen;",
			Subtop.Role,"-entity-type-fill: red; -function-fill: green; -role-fill: blue;" // role can go everywhere
			); 
	
	public void highlightObjectCandidates(Clazz subject)
	{		
		var reset = "-entity-type-fill: darkred; -function-fill: darkgreen; -role-fill: darkblue;";
		flow.getChildren().stream().forEach(t->{t.setStyle(reset);});
		// applying the style to the flow or the scroll pane does not work
		// ~420 children in test case, in case of performance problems optimize this
		flow.getChildren().stream().forEach(t->{t.setStyle(cssClass.get(subject.subtop));});
	}

	/** Call when a class has changed its label. Also called automatically when a class is removed or added. */
	public void refresh()
	{
		flow.getChildren().clear();

		String restText = text;
		Set<Clazz> restClasses = new HashSet<>(classes);

		Map<Clazz,Integer> indices;
		int count = 0;
		while(restText!=null&&!restText.isEmpty())
		{
			final String restTextFinal = restText;
			indices = restClasses.stream().collect(Collectors.toMap(c->c,c->restTextFinal.indexOf(c.getLabel())));
			restClasses.retainAll(indices.entrySet().stream().filter(e->e.getValue()>-1).map(Entry::getKey).collect(Collectors.toSet()));
			Optional<Clazz> firstOpt = restClasses.stream().min(Comparator.comparing(indices::get));
			if(firstOpt.isEmpty()) {break;}
			Clazz first = firstOpt.get();			
			int pos = indices.get(first);

			if(pos>0)
			{
				var context = new Text(restText.substring(0, pos));
				context.getStyleClass().add("text-context");
				context.getStyleClass().add("highlighted");

				flow.getChildren().add(context);
			}

			Text classText = new Text(restText.substring(pos,pos+first.label.length()));
			classText.getStyleClass().add("text-class");
			classText.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->
			{
				if(e.getButton()==MouseButton.PRIMARY) {relationPane.setSubject(first);highlightObjectCandidates(first);}
				else																																				{relationPane.setObject(first);}
			});
			switch(first.subtop)
			{
				case Role: 						classText.getStyleClass().add("text-role");break;
				case Function:			classText.getStyleClass().add("text-function");break;
				case EntityType:	classText.getStyleClass().add("text-entity-type");break;
			}
			flow.getChildren().add(classText);
			//texts.put(first, classText);
			count++;
			restText = restText.substring(pos+first.label.length());
		}
		if(restText!=null&&!restText.isBlank()) {getChildren().add(new Text(restText));}		
	}

}
