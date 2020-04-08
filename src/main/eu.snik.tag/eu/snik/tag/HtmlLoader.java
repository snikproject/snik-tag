package eu.snik.tag;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.lang3.StringUtils;

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
		//System.out.println(doc.html());
		Object[][] tagClasses = {{"i","Entity Type",Subtop.EntityType},{"b","Role",Subtop.Role},{"u","Function",Subtop.Function}};
		var processedLabels = new HashSet<String>();
		
		for(var tc: tagClasses)
		{
			String tagName = (String)tc[0];
			Elements eles = doc.select(tagName);
			//System.out.println(eles);
			for(Element ele: eles)
			{				
				String text = ele.text();
				String label = StringUtils.strip(text,"., ");
				//label = label.replaceAll("[^A-Za-z0-9 ]", ""); // removing non-alphanumerical characters leads to missing matches in the text tab

				String filterLabel = label.replaceAll("[^A-Za-z0-9 ]", "").replaceAll("(the)|(and)|(or)",""); 
				if(filterLabel.length()<4&&!filterLabel.matches("[A-Z]{3}")) {continue;} // abbreviations with 3 letters are OK
				if(filterLabel.length()<3) {continue;} // abbreviations

				Clazz clazz = new Clazz(label,labelToLocalName(label),((Subtop)tc[2]));
				if(processedLabels.contains(label))
				{
					classes.stream().filter(c->c.subtop!=clazz.subtop).findAny().ifPresent(c->
					{
						System.err.println("Typenkonflikt. Klasse \""+label+"\" ist getagged als sowohl "+c.subtop+" als auch "+clazz.subtop+". "
								+"Ignoriere "+clazz.subtop+". Bitte beheben Sie den Konflikt im Dokument.");
					});					
					continue;
				}
				classes.add(clazz);
				processedLabels.add(label);
			}
		}		
		
		System.out.println(classes.size()+" classes extracted.");
		return classes;
	}
	
}
