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

class Methods {

	//ClientID, ClientSecret and RedirectURL are all accessible in the Spotify Developer Dashboard
	//*------------------------This is an implementation of Encapsulation------------------------*
	private static final String CLIENT_ID = "564b169e25a74324b0ed5e5d1f2065fc";
	private static final String CLIENT_SECRET = "8484636cd03b487cb3390069b660dd9f";
	private static final URI REDIRECT_URI = URI.create("http://localhost:8888/callback");
	private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setClientId(Methods.getClientId())
			.setClientSecret(Methods.getClientSecret())
			.setRedirectUri(Methods.getRedirectUri())
			.build();

	public static String getClientId() {
		return CLIENT_ID;
	}

	public static String getClientSecret() {
		return CLIENT_SECRET;
	}

	public static URI getRedirectUri() {
		return REDIRECT_URI;
	}
	public static SpotifyApi getSpotifyApi() {
		return spotifyApi;
	}

	//Method that reads file and extracts authCode to a string
	static String readCodeFromFile() throws IOException {
		File file = new File("authorization_code.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return reader.readLine();
		}
	}

	//Builds URL that lets user login and access authCode
	static String buildURL() {
		final String SCOPES = "user-read-private user-read-email user-top-read";
		return "https://accounts.spotify.com/authorize?" +
				"response_type=code" +
				"&client_id=" + CLIENT_ID +
				"&redirect_uri=" + URLEncoder.encode(REDIRECT_URI.toString(), StandardCharsets.UTF_8) +
				"&scope=" + URLEncoder.encode(SCOPES, StandardCharsets.UTF_8);
	}

	//Builds URL and prints it to console
	static void printURL() {
		String authorizeUrl = Methods.buildURL();
		System.out.println("Visit this URL to authorize app:");
		System.out.println(authorizeUrl);
	}

	//Getter for authCode
	static String getAuthCode(URI uri) {
		Map<String, String> queryParams = Stream.of(uri.getQuery().split("&"))
				.map(pair -> pair.split("="))
				.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
		return queryParams.get("code");
	}

	//Saves authCode to file
	static void saveAuthCode(String code) {
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

	//Saves refreshToken to file
	static void saveRefreshToken(String refreshToken) {
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

	//Reads refreshToken from the file
	static String readRefreshTokenFromFile() throws IOException {
		File file = new File("refresh_token.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return reader.readLine();
		}
	}

	//Refresh the accessToken using refreshToken
	static String refreshAccessToken(String refreshToken) throws IOException, SpotifyWebApiException, ParseException {
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

