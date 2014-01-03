/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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

import objects.SimpleUser;
import objects.User;

import com.android.meetingninja.extras.Utilities;
import com.android.meetingninja.user.SessionManager;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class UserDatabaseAdapter extends DatabaseAdapter {
	private static String SERVER_EXT = "User";
	private static String KEY_ID = "userID";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_PHONE = "phone";
	private static String KEY_COMPANY = "company";
	private static String KEY_TITLE = "title";
	private static String KEY_LOCATION = "location";

	public static User nodeToUser(JsonNode node) {
		User u = new User(); // start parsing a user
		// if they at least have an email and name
		if (node.hasNonNull(KEY_EMAIL) && node.hasNonNull(KEY_NAME)) {
			String email = node.get(KEY_EMAIL).asText();
			// if their email is in a reasonable format
			if (Utilities.isValidEmailAddress(email)) {
				// set the required fields
				u.setUserID(node.get(KEY_ID).asText());
				u.setDisplayName(node.get(KEY_NAME).asText());
				u.setEmail(email);
				// check and set the optional fields
				if (node.hasNonNull(KEY_LOCATION))
					u.setLocation(node.get(KEY_LOCATION).asText());
				if (node.hasNonNull(KEY_PHONE))
					u.setPhone(node.get(KEY_PHONE).asText());
				if (node.hasNonNull(KEY_COMPANY))
					u.setCompany(node.get(KEY_COMPANY).asText());
				if (node.hasNonNull(KEY_TITLE))
					u.setTitle(node.get(KEY_TITLE).asText());
			}
		} else {
			return null;
		}
		return u;
	}

	public static SimpleUser nodeToSimpleUser(JsonNode node) {
		SimpleUser u = new SimpleUser();
		if (node.hasNonNull(KEY_NAME)) {
			u.setUserID(node.get(KEY_ID).asText());
			u.setDisplayName(node.get(KEY_NAME).asText());
		} else {
			return null;
		}
		return u;
	}

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
		JsonNode userNode = MAPPER.readTree(response);

		return nodeToUser(userNode);
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
		jgen.writeStringField(KEY_NAME, createUser.getDisplayName());
		jgen.writeStringField("password", password);
		jgen.writeStringField(KEY_EMAIL, createUser.getEmail());
		jgen.writeStringField(KEY_PHONE, createUser.getPhone());
		jgen.writeStringField(KEY_COMPANY, createUser.getCompany());
		jgen.writeStringField(KEY_TITLE, createUser.getTitle());
		jgen.writeStringField(KEY_LOCATION, createUser.getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		Map<String, String> responseMap = new HashMap<String, String>();

		/*
		 * result should get valid={"userID":"##"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			responseMap = MAPPER.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey(KEY_ID)) {
				result = "duplicate email or username";
			} else
				result = responseMap.get(KEY_ID);
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
		jgen.writeStringField(KEY_EMAIL, email);
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

		/*
		 * result should get valid={"userID":"##"}
		 * invalid={"errorID":"3","errorMessage":"invalid username or password"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			responseMap = MAPPER.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey(KEY_ID)) {
				result = "invalid username or password";
			} else
				result = responseMap.get(KEY_ID);
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
		final JsonNode userArray = MAPPER.readTree(response).get("users");

		if (userArray.isArray()) {
			for (final JsonNode userNode : userArray) {
				User u = null;
				// assign and check null and do not add local user
				if ((u = nodeToUser(userNode)) != null
						&& !u.getUserID().equals(
								SessionManager.getInstance().getUserID()))
					userList.add(u);
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
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		for (String key : key_values.keySet()) {
			if (key.equals(KEY_EMAIL)
					&& !Utilities
							.isValidEmailAddress(key_values.get(KEY_EMAIL)))
				throw new IOException(
						"Error : [Update User] Incorrect email format");
			else {
				jgen.flush();
				// Build JSON Object
				jgen.writeStartObject();
				jgen.writeStringField(KEY_ID, userID);
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
