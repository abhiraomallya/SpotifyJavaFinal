package org.example;

public class APICaller {
	public static void main(String[] args) throws Exception {
		//starts server on port 8888
		Server.startServer();
		System.out.println("The frontend is hosted here: \nhttps://getlucky13.github.io/SpotifyStats/");
	}
}