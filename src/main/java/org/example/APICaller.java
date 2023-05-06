package org.example;

/**
 * Driver class
 */
public class APICaller {
	/** 
	 * Main method for server execution. Starts server and prints address of frontend to console.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Starts server on port 8888
		Server.startServer();

		// Prints link to frontend to console
		System.out.println("The frontend is hosted here: \nhttps://getlucky13.github.io/SpotifyStats/");
	}
}