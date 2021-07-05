package eu.snik.tag.gui;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.jena.ext.com.google.common.collect.LinkedListMultimap;
import org.apache.jena.ext.com.google.common.collect.Multimap;
import eu.snik.tag.Clazz;
import eu.snik.tag.Subtop;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/** Uneditable HTML text area with the DOCX text and highlighted classes.*/
public class ClassTextPane extends ScrollPane
{
	private final TextFlow flow = new TextFlow();

	Multimap<Clazz, Text> texts = LinkedListMultimap.create();
	
	final State state;


	public void highlightSubject(Clazz old, Clazz neww)
	{
		if(old!=null) {texts.get(old).forEach(f->{f.getStyleClass().remove("subject");});}
		
		if(neww!=null) {texts.get(neww).forEach(f->{f.getStyleClass().add("subject");});}
		
		//System.out.println(texts.get(subject).stream().map(t->t.getStyleClass()).collect(Collectors.toSet()));
	}

	public void highlightObject(Clazz old, Clazz neww)
	{
		if(old!=null) {texts.get(old).forEach(f->{f.getStyleClass().remove("object");});}
		
		if(neww!=null) {texts.get(neww).forEach(f->{f.getStyleClass().add("object");});}

		//System.out.println(texts.get(object).stream().map(t->t.getStyleClass()).collect(Collectors.toSet()));
	}

	/** @param classes added or removed classes will automatically be shown. 
	 * @param relationPane */
	public ClassTextPane(State state, RelationPane relationPane)
	{
		this.state=state;
		this.setContent(flow);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setMinWidth(700);		
		flow.prefWidthProperty().bind(this.widthProperty());
		state.classes.addListener((ListChangeListener<Clazz>)(c)->{refresh();});
		state.text.addListener((obs,old,neww)->{refresh();});
		
		state.selectedSubject.addListener((ov,old,neww)->
		{
			highlightObjectCandidates(neww);
			highlightSubject(old,neww);
		});
		
		state.selectedObject.addListener((ov,old,neww)->
		{
			highlightObject(old,neww);
		});		
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
			t.setStyle(cssClass.get(subject.subtop()));
		});
	}

	/** Call when a class has changed its label. Also called automatically when a class is removed or added. */
	public void refresh()
	{
		flow.getChildren().clear();

		String restText = state.text.get();
		Set<Clazz> restClasses = new HashSet<>(state.classes);

		while(restText!=null&&!restText.isEmpty())
		{
			final String restTextFinal = restText;
			record ClassRef(Clazz clazz, Integer index, Integer length) {};
			
			Optional<ClassRef> minOpt = restClasses.stream().flatMap(c->(c.labels().stream()
					.map(l->new ClassRef(c,restTextFinal.indexOf(l),l.length()))))
					.filter(t->t.index()!=-1) // only with labels found in the text
					.min(Comparator.comparing(ClassRef::index) // minimize position
							.thenComparing(Comparator.comparing(ClassRef::length).reversed()) // tiebreaker: maximize label length, TODO: test					
							);

			if(minOpt.isEmpty()) {break;}
			var min = minOpt.get();

			if(min.length>0)
			{
				var context = new Text(restText.substring(0, min.index));
				context.getStyleClass().add("text-context");
				context.getStyleClass().add("highlighted");

				flow.getChildren().add(context);
			}

			Text classText = new Text(restText.substring(min.index,min.index+min.length));
			texts.put(min.clazz, classText);
			classText.getStyleClass().add("text-class");
			classText.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->
			{				
				if(e.getButton()==MouseButton.PRIMARY)
				{
					state.selectedSubject.set(min.clazz);
				}
				else
				{
					state.selectedObject.set(min.clazz);
				}
			});
			switch(min.clazz.subtop())
			{
				case Role: 						classText.getStyleClass().add("text-role");break;
				case Function:			classText.getStyleClass().add("text-function");break;
				case EntityType:	classText.getStyleClass().add("text-entity-type");break;
			}
			flow.getChildren().add(classText);
			//texts.put(first, classText);			
			restText = restText.substring(min.index+min.length);
		}
		
		Text rest = new Text(restText);
		rest.getStyleClass().add("text-class");
		if(restText!=null&&!restText.isBlank()) {flow.getChildren().add(rest);}
		
		highlightSubject(null, state.selectedSubject.get());
		highlightObjectCandidates(state.selectedSubject.get());
		highlightObject(null, state.selectedObject.get());
	}

}
