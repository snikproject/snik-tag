package eu.snik.tag;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** Extracts SNIK classes from a tagged DOCX file. */
public class HtmlLoader extends Loader
{
	private final Document doc; 
	
	public HtmlLoader(InputStream in) throws IOException
	{
		super(in);
		this.doc  = Jsoup.parse(in(), "UTF-8", "");
	};

	@Override
	public String getText()
	{
		return doc.root().text(); // slightly easier with HTML than DOCX... throw out DocxLoader and just convert?		
	}

	@Override
	public Collection<Clazz> getClasses()
	{
		var classes = new LinkedHashSet<Clazz>();
		System.out.println(doc.html());
		Object[][] tagClasses = {{"i","Entity Type",Subtop.EntityType},{"b","Role",Subtop.Role},{"u","Function",Subtop.Function}};
		
		System.out.println(classes.size()+" classes extracted.");
		return classes;
	}
	
}
