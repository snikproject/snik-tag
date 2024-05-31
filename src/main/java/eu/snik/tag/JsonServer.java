package eu.snik.tag;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;
import org.json.JSONArray;

/**
 * JSON Server for Cytoscape visualisation.
 * The <a href="https://www.snik.eu/graph/">SNIK graph</a> is able to download JSON data from a server when provided an IP address to connect to.
 * This makes this possible, so the user's input data can be visualised using SNIK graph and cytoscape.
 * There is a button in the Menu for that.
 */
public class JsonServer extends Server {

	// ports lower than 1024 can't be bound without root access in Linux, see
	// https://serverfault.com/questions/268099/bind-to-ports-less-than-1024-without-root-access
	static AtomicInteger lastPort = new AtomicInteger(1100);
	/** Port this server is accessible at. */
	public final int port;

	/**
	 * Creates a new JSON server for Cytoscape visualisation.
	 * @param json JSON data to be served
	 * @param port Port to be accessible at
	 * @see Clazz#cytoscapeNode()
	 * @see Triple#cytoscapeEdge()
	 */
	public JsonServer(JSONArray json, int port) {
		super(port);
		this.port = port;
		setHandler(new JsonHandler(json));
	}

	/**
	 * Creates a new JSON server for Cytoscape visualisation at the default port (1100).
	 * @param json JSON data to be served
	 */
	public JsonServer(JSONArray json) {
		this(json, lastPort.addAndGet(1));
	}

	/**
	 * Handler to handle incoming JSON requests.
	 */
	public static class JsonHandler extends Handler.Abstract {

		final JSONArray json;

		public JsonHandler(JSONArray json) {
			this.json = json;
		}

		@Override
		public boolean handle(Request request, Response response, Callback callback) {
			response.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json; charset=utf-8");

			response.setStatus(200);

			response.write(false, null, callback);
			try (PrintWriter out = new PrintWriter(Content.Sink.asOutputStream(response))) {
				out.println(json);
			}
			return true;
		}
	}

	/**
	 * Server test method. Not using JUnit so that users can test it using the
	 * browser..
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) {
		System.out.println("test");
		Server server = new JsonServer(new JSONArray("['hello','world']"));
		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
