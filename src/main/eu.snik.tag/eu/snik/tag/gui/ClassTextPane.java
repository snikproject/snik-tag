package eu.snik.tag.gui;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.jena.ext.com.google.common.collect.LinkedListMultimap;
import org.apache.jena.ext.com.google.common.collect.Multimap;
import org.javatuples.Triplet;
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

	private Clazz subject = null;
	private Clazz object = null;

	Multimap<Clazz, Text> texts = LinkedListMultimap.create();


	public void highlightSubject(Clazz clazz)
	{
		if(subject!=null)
		{
			texts.get(subject).forEach(f->f.getStyleClass().remove("subject"));
		}
		subject = clazz;

		texts.get(subject).forEach(f->f.getStyleClass().add("subject"));
		//System.out.println(texts.get(subject).stream().map(t->t.getStyleClass()).collect(Collectors.toSet()));
	}

	public void highlightObject(Clazz clazz)
	{
		if(object!=null)
		{
			texts.get(object).forEach(f->f.getStyleClass().remove("object"));
		}
		object = clazz;

		texts.get(object).forEach(f->f.getStyleClass().add("object"));
		//System.out.println(texts.get(object).stream().map(t->t.getStyleClass()).collect(Collectors.toSet()));
	}

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
		this.relationPane.classTextPane=this;
		this.setMinWidth(700);		
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
		if(subject==null) {return;} // occurs when resetting after an added triple
		// applying the style to the flow or the scroll pane does not work
		// ~420 children in test case, in case of performance problems optimize this
		flow.getChildren().stream().forEach(t->
		{
			t.setStyle(cssClass.get(subject.subtop));
		});
	}

	void setSubject(Clazz clazz, Object caller)
	{
		if(this.subject==clazz) {return;} // no change
		if(caller!=relationPane) {relationPane.setSubject(clazz);} // prevent infinite loop
		highlightObjectCandidates(clazz);
		highlightSubject(clazz);
	}

	void setObject(Clazz clazz, Object caller)
	{
		if(this.object==clazz) {return;}
		if(caller!=relationPane) {relationPane.setObject(clazz);} // prevent infinite loop
		highlightObject(clazz);
	}


	/** Call when a class has changed its label. Also called automatically when a class is removed or added. */
	public void refresh()
	{
		flow.getChildren().clear();

		String restText = text;
		Set<Clazz> restClasses = new HashSet<>(classes);

		while(restText!=null&&!restText.isEmpty())
		{
			final String restTextFinal = restText;

			var minOpt = restClasses.stream().flatMap(c->(c.labels.stream()
					.map(l->new Triplet<Clazz,Integer,Integer>(c,restTextFinal.indexOf(l),l.length()))))
					.filter(t->t.getValue1()!=-1) // only with labels found in the text
					.min(Comparator.comparing(t->((Triplet<Clazz,Integer,Integer>)t).getValue1()) // minimize position
							.thenComparing(t->-((Triplet<Clazz,Integer,Integer>)t).getValue2()) // tiebreaker: maximize label length 
							);


			//restClasses.retainAll(indices.entrySet().stream().filter(e->e.getValue()>-1).map(Entry::getKey).collect(Collectors.toSet()));
			// minimize position, maximize label length if tied. for example choose "hospital's" over "hospital"
			//Optional<Clazz> firstOpt = restClasses.stream().min(Comparator.comparing(indices::get).thenComparing(c->-((Clazz)c).label.length()));
			if(minOpt.isEmpty()) {break;}
			var min = minOpt.get();
			var clazz = min.getValue0();
			int pos = min.getValue1();
			int labelLength = min.getValue2(); 


			if(pos>0)
			{
				var context = new Text(restText.substring(0, pos));
				context.getStyleClass().add("text-context");
				context.getStyleClass().add("highlighted");

				flow.getChildren().add(context);
			}

			Text classText = new Text(restText.substring(pos,pos+labelLength));
			texts.put(clazz, classText);
			classText.getStyleClass().add("text-class");
			classText.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->
			{				
				if(e.getButton()==MouseButton.PRIMARY)
				{
					setSubject(clazz,this);
				}
				else
				{
					setObject(clazz,this);
				}
			});
			switch(clazz.subtop)
			{
				case Role: 						classText.getStyleClass().add("text-role");break;
				case Function:			classText.getStyleClass().add("text-function");break;
				case EntityType:	classText.getStyleClass().add("text-entity-type");break;
			}
			flow.getChildren().add(classText);
			//texts.put(first, classText);			
			restText = restText.substring(pos+labelLength);
		}
		
		Text rest = new Text(restText);
		rest.getStyleClass().add("text-class");
		if(restText!=null&&!restText.isBlank()) {flow.getChildren().add(rest);}		
	}

}
