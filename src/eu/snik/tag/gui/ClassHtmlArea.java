package eu.snik.tag.gui;

import eu.snik.tag.Clazz;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.web.HTMLEditor;

/** Uneditable HTML text area with the DOCX text and highlighted classes.*/
@Deprecated
public class ClassHtmlArea extends HTMLEditor
{
	
	final ObservableList<Clazz> classes;
	String text;

	/** @param classes added or removed classes will automatically be shown. */
	public ClassHtmlArea(ObservableList<Clazz> classes)
	{
		this.setDisable(true);
		this.classes=classes;
		this.	setMinSize(900, 1000);
		classes.addListener((ListChangeListener<Clazz>)(c)->{refresh();});
	}

	/** Keeps the classes and highlights them in a new text. */
	public void setText(String text)
	{
		this.text=text;
		refresh();
	}

	/** Call when a class has changed its label. Also called automatically when a class is removed or added. */
	public void refresh()
	{
		String html = "<html><body contentEditable=\"false\">"+text+"</body></html>";
		for(var clazz: classes)
		{
			html = html.replace(clazz.label, clazz.subtop.htmlTagOpen+clazz.label+clazz.subtop.htmlTagClosed);
		}
		setHtmlText(html);
		
	}

}
