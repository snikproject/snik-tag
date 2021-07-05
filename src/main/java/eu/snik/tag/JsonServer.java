package eu.snik.tag;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;

public class JsonServer extends Server {

	//ports lower than 1024 can't be bound without root access in Linux, see https://serverfault.com/questions/268099/bind-to-ports-less-than-1024-without-root-access
	static AtomicInteger lastPort = new AtomicInteger(1100);
	public final int port;

	public JsonServer(JSONArray json, int port) {
		super(port);
		this.port = port;
		setHandler(new JsonHandler(json));
	}

	public JsonServer(JSONArray json) {
		this(json, lastPort.addAndGet(1));
	}

	public static class JsonHandler extends AbstractHandler {

		final JSONArray json;

		public JsonHandler(JSONArray json) {
			this.json = json;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			response.setContentType("application/json; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);

			try (PrintWriter out = response.getWriter();) {
				out.println(json);
			}
			baseRequest.setHandled(true);
		}
	}

	/** Server test method. Not using JUnit so that users can test it using the browser..*/
	public static void main(String[] args) throws Exception {
		System.out.println("test");
		Server server = new JsonServer(new JSONArray("['hello','world']"));
		server.start();
	}
}
