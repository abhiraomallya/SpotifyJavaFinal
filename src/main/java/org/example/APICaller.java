package org.example;

public class APICaller {
	public static void main(String[] args) throws Exception {
		//starts server on port 8888
		Server.startServer();
		//prints url to console, when you click, it will redirect to localhost:8888/callback and save the authCode to a file
		Methods.printURL();
	}
}