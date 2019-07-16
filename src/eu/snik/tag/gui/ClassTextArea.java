package eu.snik.tag.gui;

import eu.snik.tag.Clazz;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.web.HTMLEditor;

public class ClassTextArea extends HTMLEditor
{
	
	final ObservableList<Clazz> classes;
	String text;

	public ClassTextArea(ObservableList<Clazz> classes)
	{
		this.setDisable(true);
		this.classes=classes;
		this.	setMinSize(900, 1000);
		classes.addListener((ListChangeListener<Clazz>)(c)->{refresh();});
	}

	public void setText(String text)
	{
		this.text=text;
		refresh();
	}

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
