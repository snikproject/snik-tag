package eu.snik.tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class State
{
	public final String text;
	public final ArrayList<Clazz> classes;	
	
	@SuppressWarnings("unchecked")
	public State(InputStream in)
	{
		try(var oin = new ObjectInputStream(in))
		{
			this.text = (String) oin.readObject();
			this.classes = (ArrayList<Clazz>)oin.readObject();
		}
		catch(IOException|ClassNotFoundException e) {throw new RuntimeException(e);}
	}	
	
	public State(String text, Collection<Clazz> classes)
	{
		this.text = text;
		this.classes = new ArrayList<>(classes);
	}
	
	public void save(OutputStream out)
	{
		try(var oout = new ObjectOutputStream(out))
		{
			oout.writeObject(text);
			oout.writeObject(classes);
		}
		catch(IOException e) {throw new RuntimeException(e);}
	}

}
