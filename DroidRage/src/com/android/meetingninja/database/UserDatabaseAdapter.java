package com.android.meetingninja.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.User;

import com.android.meetingninja.extras.Utilities;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserDatabaseAdapter extends DatabaseAdapter {
	private static String SERVER_EXT = "User";

	public static User getUserInfo(String userID) throws IOException {
		// Server URL setup
		String _url = SERVER_NAME + SERVER_EXT + "/" + userID;

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
		parser.close();
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
		String _url = SERVER_NAME + SERVER_EXT;

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
		String _url = SERVER_NAME + SERVER_EXT + "/" + "Login";

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

	public static List<User> getAllUsers() throws IOException {
		String _url = SERVER_NAME + SERVER_EXT + "/" + "Users";
		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		List<User> userList = new ArrayList<User>();
		ObjectMapper mapper = new ObjectMapper(JFACTORY);
		final JsonNode userArray = mapper.readTree(response).get("users");

		if (userArray.isArray()) {
			for (final JsonNode userNode : userArray) {
				User u = new User(); // start parsing a user
				// if they at least have an email and name
				if (userNode.hasNonNull("email") && userNode.hasNonNull("name")) {
					String email = userNode.get("email").asText();
					// if their email is in a reasonable format
					if (Utilities.isValidEmailAddress(email)) {
						// set the required fields
						u.setEmail(email);
						u.setDisplayName(userNode.get("name").asText());
						u.setUserID(userNode.get("userID").asText());
						// check and set the optional fields
						if (userNode.hasNonNull("location"))
							u.setLocation(userNode.get("location").asText());
						if (userNode.hasNonNull("phone"))
							u.setPhone(userNode.get("phone").asText());
						if (userNode.hasNonNull("company"))
							u.setCompany(userNode.get("company").asText());
						if (userNode.hasNonNull("title"))
							u.setTitle(userNode.get("title").asText());
						userList.add(u);
					}
				}
			}
		}

		conn.disconnect();
		return userList;
	}

	public static String update(String userID, Map<String, String> key_values)
			throws JsonGenerationException, IOException, InterruptedException {
		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = new JsonFactory().createGenerator(ps,
				JsonEncoding.UTF8);
		for (String key : key_values.keySet()) {
			if (key.equals("email")
					&& !Utilities.isValidEmailAddress(key_values.get("email")))
				throw new IOException(
						"Error : [Update User] Incorrect email format");
			else {
				jgen.flush();
				// Build JSON Object
				jgen.writeStartObject();
				jgen.writeStringField("userID", userID);
				jgen.writeStringField("field", key);
				jgen.writeStringField("value", key_values.get(key));
				jgen.writeEndObject();
				jgen.writeRaw("\f");
			}
		}

		jgen.close();
		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();
		String[] payloads = payload.split("\f\\s*");

		Thread t = new Thread(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.getLocalizedMessage();
				}
			}
		}));
		String response = new String();
		for (String p : payloads) {
			t.run();
			response = updateHelper(p);
		}
		return response;
	}

	private static String updateHelper(String jsonPayload) throws IOException {
		// Server URL setup
		String _url = SERVER_NAME + SERVER_EXT;

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("PUT");
		addRequestHeader(conn, true);

		int responseCode = sendPostPayload(conn, jsonPayload);
		String response = getServerResponse(conn);
		conn.disconnect();
		return response;
	}
}
