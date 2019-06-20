package eu.snik.tag;

public class Test
{

	public static void main(String[] args)
	{
		Clazz ceo = new Clazz("CEO", "Ceo", Subtop.ROLE);
		Clazz dostuff = new Clazz("do stuff", "DoStuff", Subtop.FUNCTION);
		
		ceo.addTriple(Relation.isReponsibleForFunction, dostuff);
		
		System.out.println(ceo);

	}

}
