package com.koda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleServer {
	
	private static final int SUCCESS = 0;
	private static final int USER_EXISTS = 1;
	
	private static final String NEW_CONNECTION = "c/";
	private static final String LOG_IN = "l/";
	private static final String REGISTER = "r/";
	
	private int port;
	private HashMap<String, String> database = new HashMap<String, String>();
	
	public SimpleServer(int port) {
		this.port = port;
	}
	
	public void start() {
		try {
			ServerSocket server = new ServerSocket(port);
			System.out.println("Started server with port " + server.getLocalPort());
			String welcome = "<html><body><h1>Welcome to my server!</h1></body></html>\n";
			while (true) {
				//System.out.println("Waiting for a connection...");
				Socket connection = server.accept();
				//System.out.println("Connection received. Reading client message...");
				
				InputStream is = connection.getInputStream();
				byte[] input = new byte[1024];
				int amount = is.read(input);
				//System.out.println("Read " + amount + " bytes from client:");
				String request = new String(input);
				//System.out.println(request);
				
				parseRequest(request);
				
				OutputStream os = connection.getOutputStream();
				String response = "HTTP/1.1 200 OK\nContent-Type: text/html\nContent-Length: " + welcome.length() + "\n\n";
				response += welcome;
				//System.out.println("Sending response...");
				os.write(response.getBytes());
				//System.out.println("Done sending message");
				connection.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseRequest(String request) {
		int delim = request.indexOf("/");
		String prefix = request.substring(0, delim + 1);
		String message = request.substring(delim + 1);
		
		if (prefix.equals(LOG_IN)) {
			String[] parts = message.split(" ");
			System.out.println("Login attempt from " + parts[0] + "//" + parts[1]);
			
			int addUserResult = addUser(parts[0], parts[1]);
			if (addUserResult == USER_EXISTS) {
				System.out.println("Whoops, the user is already in the database!");
			} else if (addUserResult == SUCCESS) {
				System.out.println("User successfully registered");
			}
		}
	}
	
	public int addUser(String name, String pass) {
		if (database.containsKey(name)) {
			return USER_EXISTS;
		}
		
		database.put(name, pass);
		return SUCCESS;
	}
	
	public static void main(String[] args) {
		new SimpleServer(8000).start();
	}
}
