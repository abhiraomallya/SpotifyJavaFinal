package org.example;

import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class containing utility methods
 */
public class Methods {

	//ClientID, ClientSecret and RedirectURL are all accessible in the Spotify Developer Dashboard
	//*------------------------This is an implementation of Encapsulation------------------------*
	/**
	 * Required value provided by Spotify to authorize application to interact with API.
	 */
	private static final String CLIENT_ID = "564b169e25a74324b0ed5e5d1f2065fc";

	/**
	 * Required value provided by Spotify to authorize application to interact with API.
	 */
	private static final String CLIENT_SECRET = "8484636cd03b487cb3390069b660dd9f";

	/**
	 * URI used to redirect user after authorization.
	 */
	private static final URI REDIRECT_URI = URI.create("http://localhost:8888/callback");

	/**
	 * SpotifyApi object created using CLIENT_ID, CLIENT_SECRET, and REDIRECT_URI.
	 */
	private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setClientId(Methods.getClientId())
			.setClientSecret(Methods.getClientSecret())
			.setRedirectUri(Methods.getRedirectUri())
			.build();

	/** Getter for CLIENT_ID
	 * @return String stored in CLIENT_ID
	 */
	public static String getClientId() {
		return CLIENT_ID;
	}

	/** Getter for CLIENT_SECRET
	 * @return String stored in CLIENT_SECRET
	 */
	public static String getClientSecret() {
		return CLIENT_SECRET;
	}

	/** Getter for REDIRECT_URI
	 * @return URI stored in REDIRECT_URI
	 */
	public static URI getRedirectUri() {
		return REDIRECT_URI;
	}

	/** Getter for spotifyApi
	 * @return SpotifyApi object stored in spotifyApi
	 */
	public static SpotifyApi getSpotifyApi() {
		return spotifyApi;
	}

	
	/**Reads auth code from file
	 * @return String containing stored auth code
	 * @throws IOException
	 */
	public static String readCodeFromFile() throws IOException {
		File file = new File("authorization_code.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return reader.readLine();
		}
	}

	/** Builds URL that allows user to login to Spotify account
	 * @return String containing URL for user authorization
	 */
	public static String buildURL() {
		final String SCOPES = "user-read-private user-read-email user-top-read";
		return "https://accounts.spotify.com/authorize?" +
				"response_type=code" +
				"&client_id=" + CLIENT_ID +
				"&redirect_uri=" + URLEncoder.encode(REDIRECT_URI.toString(), StandardCharsets.UTF_8) +
				"&scope=" + URLEncoder.encode(SCOPES, StandardCharsets.UTF_8);
	}

	/**
	 * Builds URL and prints to console
	 */
	public static void printURL() {
		String authorizeUrl = Methods.buildURL();
		System.out.println("Visit this URL to authorize app:");
		System.out.println(authorizeUrl);
	}


	/** Extracts auth code from URI object passed as arg
	 * @param uri URI object returned from Spotify Authorization 
	 * @return String containing auth code extracted from argument
	 */
	public static String getAuthCode(URI uri) {
		Map<String, String> queryParams = Stream.of(uri.getQuery().split("&"))
				.map(pair -> pair.split("="))
				.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
		return queryParams.get("code");
	}

	/** Saves auth code to file on server
	 * @param code String representing the auth code to be saved
	 */
	public static void saveAuthCode(String code) {
		try {
			File file = new File("authorization_code.txt");
			FileWriter writer = new FileWriter(file);
			writer.write(code);
			writer.flush();
			writer.close();
			System.out.println("authorization_code saved at: " + file.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Error saving authorization_code: " + e.getMessage());
		}
	}

	/** Saves refresh code to file on server
	 * @param refreshToken String representing the refresh token to save
	 */
	public static void saveRefreshToken(String refreshToken) {
		try {
			File file = new File("refresh_token.txt");
			FileWriter writer = new FileWriter(file);
			writer.write(refreshToken);
			writer.flush();
			writer.close();
			System.out.println("refresh_token saved at: " + file.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Error saving refresh_token: " + e.getMessage());
		}
	}

	/** Reads stored refresh token from file 
	 * @return String representing the stored refresh token
	 * @throws IOException
	 */
	public static String readRefreshTokenFromFile() throws IOException {
		File file = new File("refresh_token.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return reader.readLine();
		}
	}

	/** Aquires a new access token from Spotify using the current refresh token
	 * @param refreshToken String representing the refresh token to be used in request for new access token
	 * @return String representing new access token
	 * @throws IOException
	 * @throws SpotifyWebApiException
	 * @throws ParseException
	 */
	public static String refreshAccessToken(String refreshToken) throws IOException, SpotifyWebApiException, ParseException {
		SpotifyApi spotifyApi = new SpotifyApi.Builder()
				.setClientId(getClientId())
				.setClientSecret(getClientSecret())
				.build();

		AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi
				.authorizationCodeRefresh()
				.refresh_token(refreshToken)
				.build();
		return authorizationCodeRefreshRequest.execute().getAccessToken();
	}
}

