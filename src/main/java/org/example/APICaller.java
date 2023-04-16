package SpotifyJavaDemoGit.src.main.java.org.example;

public class APICaller {
	public static void main(String[] args) throws Exception {

		//starts server on port 8888
		Server.startServer();
		//prints url to console, when you click, it will redirect to localhost:8888/callback and save the authCode to a file
		Methods.printURL();
// *--------------------------------- INSTRUCTIONS TO USE ------------------------------------*
//<-- PLACE DEBUG BREAKPOINT HERE, AND START DEBUGGING
// WAIT FOR SERVER TO START AND CLICK ON THE URL IN CONSOLE
// STEP OVER AFTER YOU LOG IN TO THE URL AND IT SAYS "AUTHORIZATION SUCCESSFUL"
// THEN YOU CAN RESUME THE CODE TILL THE END
// IT SHOULD PRINT A LIST OF YOUR TOP 10 MOST LISTENED ARTISTS IN THE "MEDIUM_TERM"
// CAN BE ALTERED FOR DIFFERENT TIME RANGES
//long_term (calculated from several years of data and including all new data as it becomes available)
//medium_term (approximately last 6 months)
//short_term (approximately last 4 weeks)
//		SpotifyApi spotifyApi = new SpotifyApi.Builder()
//				.setClientId(Methods.getClientId())
//				.setClientSecret(Methods.getClientSecret())
//				.setRedirectUri(Methods.getRedirectUri())
//				.build();
//
//		// reads authCode from the file
//		String authorizationCode = Methods.readCodeFromFile();
//		//utilizes authCode to make api requests
//		AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(authorizationCode).build();
//		try {
//			AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
//			spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
//		} catch (IOException | SpotifyWebApiException e) {
//			System.out.println("Error: " + e.getMessage());
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
//
//		// gets Spotify user profile info
//		GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
//		try {
//			String userId = getCurrentUsersProfileRequest.execute().getId();
//
//			// get top artists of that user over a medium term
//			GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
//					.time_range("medium_term")
//					.limit(10) // set number of top artists to retrieve
//					.build();
//			Paging<Artist> artists = getUsersTopArtistsRequest.execute();
//
//			// extract artist names and add to list
//			List<String> topArtists = new ArrayList<>();
//			for (Artist artist : artists.getItems()) {
//				topArtists.add(artist.getName());
//			}
//
//			// print top artists list
//			System.out.println("User " + userId + "'s top listened artists:");
//			for (int i = 0; i < topArtists.size(); i++) {
//				System.out.println((i + 1) + ". " + topArtists.get(i));
//			}
//
//
//			//Error handling
//		} catch (IOException | SpotifyWebApiException e) {
//			System.out.println("Error: " + e.getMessage());
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
	}
}
