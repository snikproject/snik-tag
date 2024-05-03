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

public class JsonServer extends Server {

	// ports lower than 1024 can't be bound without root access in Linux, see
	// https://serverfault.com/questions/268099/bind-to-ports-less-than-1024-without-root-access
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
