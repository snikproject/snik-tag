package eu.snik.tag;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;

public class JsonServer extends Server
{
	static final int PORT = 1234;  

	public JsonServer(JSONArray json)
	{
		super(PORT);
		setHandler(new JsonHandler(json));
	}
	
	public static class JsonHandler extends AbstractHandler
	{
		final JSONArray json;

		public JsonHandler(JSONArray json)
		{
			this.json = json;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
		{
			response.setContentType("application/json; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			try(PrintWriter out = response.getWriter();)
			{
				out.println(json);
			}
			baseRequest.setHandled(true);
		}

	}

	/** Server test method. Not using JUnit so that users can test it using the browser..*/
	public static void main(String[] args) throws Exception
	{
		System.out.println("test");
		Server server = new JsonServer(new JSONArray("['hello','world']"));
		server.start();		
	}

}
