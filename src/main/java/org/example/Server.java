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
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

/**
 * This class handles the backend server's functions.
 */
class Server {
	private static final int PORT = 8888;
	//private static boolean authorized = false;

	/**
	 * Method to start backend server. Creates an HttpServer object with various contexts to handle
	 * the various calls to the backend made by the frontend.
	 * @throws Exception
	 */
	public static void startServer() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/", new InitialHandler());
		server.createContext("/callback", new CallbackHandler());
		server.createContext("/top-artists-short", new TopArtistsHandler("short_term"));
		server.createContext("/top-artists", new TopArtistsHandler("medium_term"));
		server.createContext("/top-artists-long", new TopArtistsHandler("long_term"));
		server.createContext("/top-tracks-short", new TopTracksHandler("short_term"));
		server.createContext("/top-tracks", new TopTracksHandler("medium_term"));
		server.createContext("/top-tracks-long", new TopTracksHandler("long_term"));
		server.setExecutor(null);
		server.start();
		System.out.println("Server started at http://localhost:" + PORT);
	}

	/**
	 * Implementation of HttpHandler interface for handling initial context.
	 */
	static class InitialHandler implements HttpHandler {
		/**
		 * Overide of handle method for handling an exchange with the frontend. Takes an HttpExchange object as an 
		 * argument and sends a very simple html page to the front end with a message notifying user that the 
		 * server has launched correctly.
		 * @param exchange HttpExchange object sent from frontend
		 * @throws IOException
		 */
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String response = "<html><body><h1>Server launched!</h1></body></html>";
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}


	/**
	 * Implementation of HttpHandler interface to handle authorization response.
	 */
	static class CallbackHandler implements HttpHandler {
		/**
		 * Overide of handle method for handling response HttpExchange after user authorization.
		 * After user signs into Spotify account, they are redirected to /callback context. 
		 * This redirection includes the users auth code in the URL, necessary for communicating with the
		 * Spotify API. This method first extracts the users auth code from the url and saves it to a file
		 * before redirecting the user to the main page of the frontend.
		 * @param exchange HttpExchange returned from authorization server after user is authorized
		 * @throws IOException
		 */
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			URI requestURI = exchange.getRequestURI();
			String code = Methods.getAuthCode(requestURI);

			if (code != null) {
				Methods.saveAuthCode(code);
				String response = "<html><body><h1 id='verified'>Authorization successful.</h1></body>"+
				"<script>window.location.href='https://getlucky13.github.io/SpotifyStats/main.html'</script></html>";
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

		// get top artists of that user over a given term
		GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
				.time_range(timeRange)
				.limit(10) // set number of top artists to retrieve
				.build();
		Paging<Artist> artists = getUsersTopArtistsRequest.execute();

		// extract artist names and add to list
		List<String> topArtists = new ArrayList<>();
		for (Artist artist : artists.getItems()) {
			topArtists.add(artist.getName());
		}

		return topArtists;
	}


	static class TopArtistsHandler extends BaseHandler {
		public TopArtistsHandler(String timeRange) {
			super(timeRange);
		}

		@Override
		protected List<String> getData(String timeRange) throws Exception {
			return getTopArtists(timeRange);
		}
	}


	public static List<String> getTopTracks(String timeRange) throws Exception {
		SpotifyApi spotifyApi = Methods.getSpotifyApi();

		// Refresh the access token if necessary using the refresh token
		String refreshToken = Methods.readRefreshTokenFromFile();
		String newAccessToken = Methods.refreshAccessToken(refreshToken);
		spotifyApi.setAccessToken(newAccessToken);

		// Get the top tracks of the user over a given term
		GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks()
				.time_range(timeRange)
				.limit(10) // Set the number of top tracks to retrieve
				.build();
		Paging<Track> tracks = getUsersTopTracksRequest.execute();

		// Extract track names and add to list
		List<String> topTracks = new ArrayList<>();
		for (Track track : tracks.getItems()) {
			StringBuilder artistString = new StringBuilder();
			if (track.getArtists().length == 1) {
				ArtistSimplified[] artistArray = track.getArtists();
				artistString = new StringBuilder(artistArray[0].getName());
			} else {
				for (ArtistSimplified artist : track.getArtists()) {
					artistString.append(artist.getName()).append(", ");
				}
			}
			topTracks.add(track.getName() + " - " + artistString);
		}

		return topTracks;
	}

	static class TopTracksHandler extends BaseHandler {
		public TopTracksHandler(String timeRange) {
			super(timeRange);
		}

		@Override
		protected List<String> getData(String timeRange) throws Exception {
			return getTopTracks(timeRange);
		}
	}

	abstract static class BaseHandler implements HttpHandler {
		protected final String timeRange;

		public BaseHandler(String timeRange) {
			this.timeRange = timeRange;
		}

		protected abstract List<String> getData(String timeRange) throws Exception;

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if (!"GET".equals(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(405, -1);
				return;
			}

			try {
				List<String> data = getData(timeRange);

				// Convert data list to JSON
				Gson gson = new Gson();
				String dataJson = gson.toJson(data);

				// Set the response headers and send the JSON data
				exchange.getResponseHeaders().add("Content-Type", "application/json");
				exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
				exchange.sendResponseHeaders(200, dataJson.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				os.write(dataJson.getBytes());
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				exchange.sendResponseHeaders(500, -1);
			}
		}
	}

}

