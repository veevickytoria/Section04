package com.droidrage.meetingninja.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import objects.User;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserDatabaseAdapter extends DatabaseAdapter {
	public static User getUserInfo(String userID) throws IOException {
		// Server URL setup
		String method = "User";
		String _url = String.format("%s%s/%s", SERVER_NAME, method, userID);

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		User getUser = new User();
		JsonParser parser = JFACTORY.createParser(response);
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String name = parser.getCurrentName();
			if ("userID".equals(name)) {
				parser.nextToken();
				getUser.setUserID((parser.getText()));
			} else if ("name".equals(name)) {
				parser.nextToken();
				getUser.setDisplayName(parser.getText());
			} else if ("email".equals(name)) {
				parser.nextToken();
				getUser.setEmail(parser.getText());
			} else if ("phone".equals(name)) {
				parser.nextToken();
				getUser.setPhone(parser.getText());
			} else if ("company".equals(name)) {
				parser.nextToken();
				getUser.setCompany(parser.getText());
			} else if ("title".equals(name)) {
				parser.nextToken();
				getUser.setTitle(parser.getText());
			} else if ("location".equals(name)) {
				parser.nextToken();
				getUser.setLocation(parser.getText());
			}
		}

		conn.disconnect();
		return getUser;
	}

	/**
	 * Registers a User with the supplied fields
	 * 
	 * @param displayName
	 * @param email
	 * @param password
	 * @return "error" or the UserID for the registered user
	 * @throws IOException
	 */
	public static String register(String displayName, String email,
			String password) throws IOException {
		// Server URL setup
		String method = "User";
		String _url = String.format("%s%s/", SERVER_NAME, method);

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
		User createUser = new User();
		createUser.setDisplayName(displayName);
		createUser.setEmail(email);
		jgen.writeStartObject();
		jgen.writeStringField("name", createUser.getDisplayName());
		jgen.writeStringField("password", password);
		jgen.writeStringField("email", createUser.getEmail());
		jgen.writeStringField("phone", createUser.getPhone());
		jgen.writeStringField("company", createUser.getCompany());
		jgen.writeStringField("title", createUser.getTitle());
		jgen.writeStringField("location", createUser.getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		Map<String, String> responseMap = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();

		/*
		 * result should get valid={"userID":"##"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			responseMap = mapper.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey("userID")) {
				result = "duplicate email or username";
			} else
				result = responseMap.get("userID");
		}

		conn.disconnect();
		return result;
	}

	public static String login(String email, String pass) throws IOException {
		// Server URL setup
		String method = "User";
		String _url = String.format("%s%s/Login", SERVER_NAME, method);

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
		jgen.writeStringField("email", email);
		jgen.writeStringField("password", pass);
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		Map<String, String> responseMap = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();

		/*
		 * result should get valid={"userID":"##"}
		 * invalid={"errorID":"3","errorMessage":"invalid username or password"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			responseMap = mapper.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey("userID")) {
				result = "invalid username or password";
			} else
				result = responseMap.get("userID");
		}

		conn.disconnect();
		return result;

	}
}
