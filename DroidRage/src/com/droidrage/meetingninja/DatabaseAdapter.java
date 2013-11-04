package com.droidrage.meetingninja;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.Note;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatabaseAdapter {

	private final static String SERVER_NAME = "http://csse371-04.csse.rose-hulman.edu/";
	private final static String USER_AGENT = "Mozilla/5.0";
	private final static String CONTENT_TYPE = "application/json";
	private final static String ACCEPT_TYPE = "application/json";
	private final static JsonFactory JFACTORY = new JsonFactory();

	/**
	 * Uses URL parsing to get login information from the database
	 * 
	 * @param username
	 * @return
	 */
	public static boolean urlLogin(String username) throws Exception {
		// Server URL setup
		String filename = "index.php";
		String server_method = "login";
		String _url = String.format("%s%s?method=%s&user=%s", SERVER_NAME,
				filename, server_method, username);
		boolean val = false;

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String s = getServerResponse(conn);
		val = Boolean.parseBoolean(s);

		conn.disconnect();
		return val;
	}

	/**
	 * Uses JSON parsing to get login information from the database Unused due
	 * to way backend is setup.
	 * 
	 * @param username
	 * @return true if username exists in database
	 * @deprecated Use urlLogin until backend fixed
	 */
	@Deprecated
	public static boolean jsonLogin(String username) throws Exception {
		// Server URL setup
		String filename = "index.php";
		String server_method = "login";
		String url = String.format("%s%s?method=%s&user=%s", SERVER_NAME,
				filename, server_method, username);

		URL connection = null;
		boolean val = false;
		JsonParser jParser;

		// fails. Parses (null), which is not of Boolean type
		connection = new URL(url);
		jParser = JFACTORY.createParser(connection);
		val = jParser.getBooleanValue();
		jParser.close();

		// try 2 fails. Tries to get 'true' but gets TRUE
		ObjectMapper mapper = new ObjectMapper(JFACTORY);
		val = mapper.readValue(connection, Boolean.class);

		return val;
	}

	public static void register(String username, String password) throws Exception {
		// Server URL setup
		String filename = "index.php";
		String server_method = "register";
		String _url = String.format("%s%s?method=%s", SERVER_NAME, filename,
				server_method);

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("POST");
		addRequestHeader(conn, true);

		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField("user", username);
		jgen.writeStringField("pass", password);
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int response = sendPostPayload(conn, payload);

		conn.disconnect();

	}

	public static List<Meeting> getMeetings(String username) throws Exception {
		// Server URL setup
		String filename = "Meeting.php";
		String server_method = "getMeetings";
		String _url = String.format("%s%s?method=%s&user=%s", SERVER_NAME,
				filename, server_method, username);

		// establish connection
		URL url = new URL(_url);

		// Initialize ObjectMapper
		ObjectMapper mapper = new ObjectMapper(JFACTORY);

		// Initialize an Object to be received
		List<Meeting> meetings = new ArrayList<Meeting>();

		// Use TypeReference to get a Generic Type, else use Type.class
		meetings = mapper.readValue(url, new TypeReference<List<Meeting>>() {
		});
		
		return meetings;
	}

	public static void createMeeting(String user, Meeting m) throws Exception {
		// Server URL setup
		String filename = "Meeting.php";
		String server_method = "createMeeting";
		String _url = String.format("%s%s?method=%s&user=%s", SERVER_NAME,
				filename, server_method, user);

		// establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		addRequestHeader(conn, true);

		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField("Title", m.getTitle());
		jgen.writeStringField("ID", String.format("%d", m.getId()));
		jgen.writeStringField("DateTime", m.getDatetime());
		jgen.writeStringField("Location", m.getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// send payload
		int response = sendPostPayload(conn, payload);
		
		conn.disconnect();
	}

	public static List<Note> getNotes(String user) throws Exception {
		// TODO Implement this method
		throw new Exception("getNotes: Unimplemented");
	}
	
	public static void createNote(String user, Note n) throws Exception {
		// TODO Implement this method
		throw new Exception("createNote: Unimplemented");
	}

	private static void addRequestHeader(URLConnection connection, boolean isPost) {
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept", ACCEPT_TYPE);
		if (isPost) {
			connection.setRequestProperty("Content-Type", CONTENT_TYPE);
			connection.setDoOutput(true);
		}
	}

	private static int sendPostPayload(URLConnection connection, String payload)
			throws Exception {
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();
		return ((HttpURLConnection) connection).getResponseCode();
	}

	private static String getServerResponse(URLConnection connection) throws Exception {
		// Read server response
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// return page contents
		return response.toString();
	}

}
