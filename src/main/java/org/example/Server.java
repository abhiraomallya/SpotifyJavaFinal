package org.example;

import java.io.IOException;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

class Server {
	private static final int PORT = 8888;

	public static void startServer() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/", new InitialHandler());
		server.createContext("/callback", new CallbackHandler());
		server.createContext("/top-artists-short", new TopArtistsHandler("short_term"));
		server.createContext("/top-artists", new TopArtistsHandler("medium_term"));
		server.createContext("/top-artists-long", new TopArtistsHandler("long_term"));
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


	public static List<String> getTopArtists(String timeRange) throws Exception {
		SpotifyApi spotifyApi = Methods.getSpotifyApi();

		// reads authCode from the file
		String authorizationCode = Methods.readCodeFromFile();
		// utilizes authCode to make API requests
		AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authorizationCode).build();
		try {
			AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
			spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
			Methods.saveRefreshToken(authorizationCodeCredentials.getRefreshToken()); // Save the refresh token
		} catch (IOException | SpotifyWebApiException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		// Refresh the access token if necessary using the refresh token
		String refreshToken = Methods.readRefreshTokenFromFile();
		String newAccessToken = Methods.refreshAccessToken(refreshToken);
		spotifyApi.setAccessToken(newAccessToken);

		// get top artists of that user over a medium term
		GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
				.time_range(timeRange)
				.limit(15) // set number of top artists to retrieve
				.build();
		Paging<Artist> artists = getUsersTopArtistsRequest.execute();

		// extract artist names and add to list
		List<String> topArtists = new ArrayList<>();
		for (Artist artist : artists.getItems()) {
			topArtists.add(artist.getName());
		}

		return topArtists;
	}


	static class TopArtistsHandler implements HttpHandler {
		private final String timeRange;

		public TopArtistsHandler(String timeRange) {
			this.timeRange = timeRange;
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if (!"GET".equals(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
				return;
			}

			try {
				List<String> topArtists = getTopArtists(timeRange); // This method should fetch the top artists

				// Convert topArtists list to JSON
				Gson gson = new Gson();
				String topArtistsJson = gson.toJson(topArtists);

				// Set the response headers and send the JSON data
				exchange.getResponseHeaders().add("Content-Type", "application/json");
				exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Add CORS header
				exchange.sendResponseHeaders(200, topArtistsJson.length());
				OutputStream os = exchange.getResponseBody();
				os.write(topArtistsJson.getBytes());
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				exchange.sendResponseHeaders(500, -1); // 500 Internal Server Error
			}
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

