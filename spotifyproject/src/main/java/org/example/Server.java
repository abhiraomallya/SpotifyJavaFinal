package org.example;

import java.io.IOException;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class Server {
	private static final int PORT = 8888;

	public static void startServer() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/", new InitialHandler());
		server.createContext("/callback", new CallbackHandler());
		server.setExecutor(null);
		server.start();
		System.out.println("Server started at http://localhost:" + PORT);
	}

	// Handler for the initial server launch
	static class InitialHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String response = "<html><body><h1>Server launched!</h1></body></html>";
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	// Handler for the authorization response
	static class CallbackHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			URI requestURI = exchange.getRequestURI();
			String code = Methods.getAuthCode(requestURI);

			if (code != null) {
				Methods.saveAuthCode(code);
				String response = "<html><body><h1>Authorization successful.</h1></body></html>";
				exchange.sendResponseHeaders(200, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} else {
				String response = "<html><body><h1>Authorization failed.</h1></body></html>";
				exchange.sendResponseHeaders(400, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}
		}
	}
}

