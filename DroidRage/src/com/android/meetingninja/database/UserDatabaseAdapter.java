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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.Event;
import objects.Group;
import objects.Meeting;
import objects.MockObjectFactory;
import objects.Note;
import objects.Project;
import objects.Schedule;
import objects.SimpleUser;
import objects.Task;
import objects.User;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.text.style.SuperscriptSpan;
import android.util.Log;

import com.android.meetingninja.ApplicationController;
import com.android.meetingninja.extras.Utilities;
import com.android.meetingninja.user.SessionManager;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class UserDatabaseAdapter extends BaseDatabaseAdapter {

	private static final String TAG = UserDatabaseAdapter.class.getSimpleName();

	protected static final String KEY_ID = "userID";
	protected static final String KEY_NAME = "name";
	protected static final String KEY_EMAIL = "email";
	protected static final String KEY_PHONE = "phone";
	protected static final String KEY_COMPANY = "company";
	protected static final String KEY_TITLE = "title";
	protected static final String KEY_LOCATION = "location";

	public static String getBaseUrl() {
		return BASE_URL + "User";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static User getUserInfo(String userID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(userID).build().toString();

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

		return parseUser(userNode);
	}

	public static List<SimpleUser> getContacts(String userID)
			throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Contacts").appendPath(userID)
				.build().toString();
		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		List<SimpleUser> contactsList = new ArrayList<SimpleUser>();
		final JsonNode contactsArray = MAPPER.readTree(response)
				.get("contacts");

		if (contactsArray.isArray()) {
			for (final JsonNode userNode : contactsArray) {
				SimpleUser u = parseSimpleUser(userNode);
				if (u != null)
					contactsList.add(u);
			}
		}

		return contactsList;
	}

	public static String login(String email, String pass) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Login").build().toString();
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
		String result = "";
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

	public static Schedule getSchedule(String userID)
			throws JsonParseException, JsonMappingException, IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Schedule").appendPath(userID)
				.build().toString();
		// establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		// TODO: Uncomment this later
		// int responseCode = conn.getResponseCode();
		// String response = getServerResponse(conn);
		String response = MockObjectFactory.getMockSchedule();

		Schedule sched = parseSchedule(MAPPER.readTree(response));

		conn.disconnect();
		return sched;
	}

	public static List<User> getAllUsers() throws IOException {
		String _url = getBaseUri().appendPath("Users").build().toString();
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
				User u = parseUser(userNode);
				// assign and check null and do not add local user
				if (u != null) {
					userList.add(u);
				}
			}
		}

		conn.disconnect();
		return userList;
	}

	public static List<Group> getGroups(String userID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Groups").appendPath(userID)
				.build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Group> groupList = new ArrayList<Group>();
		final JsonNode groupArray = MAPPER.readTree(response).get("groups");

		if (groupArray.isArray()) {
			for (final JsonNode groupNode : groupArray) {
				Group g = GroupDatabaseAdapter.parseGroup(groupNode);
				if (g != null) {
					groupList.add(g);
				}
			}
		} else {
			Log.e(TAG, "Error parsing user's group list");
		}

		conn.disconnect();
		return groupList;
	}

	public static List<Project> getProject(String userID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Projects").appendPath(userID)
				.build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Project> projectList = new ArrayList<Project>();
		final JsonNode projectArray = MAPPER.readTree(response).get("projects");

		if (projectArray.isArray()) {
			for (final JsonNode projectNode : projectArray) {
				Project p = ProjectDatabaseAdapter.parseProject(projectNode);
				if (p != null) {
					projectList.add(p);
				}
			}
		} else {
			Log.e(TAG, "Error parsing user's project list");
		}

		conn.disconnect();
		return projectList;
	}

	public static List<Note> getNotes(String userID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Notes").appendPath(userID)
				.build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Note> noteList = new ArrayList<Note>();
		final JsonNode noteArray = MAPPER.readTree(response).get("notes");

		if (noteArray.isArray()) {
			for (final JsonNode noteNode : noteArray) {
				Note n = NotesDatabaseAdapter.parseNote(noteNode);
				if (n != null) {
					noteList.add(n);
				}
			}
		} else {
			Log.e(TAG, "Error parsing user's project list");
		}

		conn.disconnect();
		return noteList;
	}

	public static List<Task> getTasks(String userID) throws JsonParseException,
			JsonMappingException, IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Tasks").appendPath(userID)
				.build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Task> taskList = new ArrayList<Task>();
		final JsonNode taskArray = MAPPER.readTree(response).get("tasks");

		if (taskArray.isArray()) {
			for (final JsonNode taskNode : taskArray) {
				Task t = TaskDatabaseAdapter.parseTasks(taskNode);
				if (t != null) {
					taskList.add(t);
				}
			}
		} else {
			Log.e(TAG, "Error parsing user's task list");
		}

		conn.disconnect();
		return taskList;
	}

	/**
	 * Registers a passed in User and returns that user with an assigned UserID
	 * 
	 * @param registerMe
	 * @param password
	 * @return the passed-in user with an assigned ID by the server
	 * @throws Exception
	 */
	public static User register(User registerMe, String password)
			throws Exception {
		// Server URL setup
		String _url = getBaseUri().build().toString();

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
		jgen.writeStringField(KEY_NAME, registerMe.getDisplayName());
		jgen.writeStringField("password", password);
		jgen.writeStringField(KEY_EMAIL, registerMe.getEmail());
		jgen.writeStringField(KEY_PHONE, registerMe.getPhone());
		jgen.writeStringField(KEY_COMPANY, registerMe.getCompany());
		jgen.writeStringField(KEY_TITLE, registerMe.getTitle());
		jgen.writeStringField(KEY_LOCATION, registerMe.getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		Map<String, String> responseMap = new HashMap<String, String>();
		User createUser = new User(registerMe);

		/*
		 * result should get valid={"userID":"##"}
		 */
		String result = "";
		if (!response.isEmpty()) {
			responseMap = MAPPER.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey(KEY_ID)) {
				result = "duplicate email or username";
				throw new Exception(result);
			} else {
				result = responseMap.get(KEY_ID);
				createUser.setUserID(result);
			}
		}

		conn.disconnect();
		return createUser;
	}

	public static User update(String userID, Map<String, String> key_values)
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
		String response = "";
		for (String p : payloads) {
			t.run();
			response = updateHelper(p);
		}
		return parseUser(MAPPER.readTree(response));
	}

	public static Boolean deleteUser(String userID) throws IOException {
		String _url = getBaseUri().appendPath(userID).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("DELETE");
		addRequestHeader(conn, false);
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		return MAPPER.readTree(response).get("deleted").asBoolean();

	}

	public static User parseUser(JsonNode node) {
		User u = new User(); // start parsing a user
		// if they at least have an id, email, and name
		if (node.hasNonNull(KEY_EMAIL) && node.has(KEY_NAME)
		// && node.hasNonNull(KEY_ID)
		) {
			String email = node.get(KEY_EMAIL).asText();
			// if their email is in a reasonable format
			if (!TextUtils.isEmpty(node.get(KEY_NAME).asText())
					&& Utilities.isValidEmailAddress(email)) {
				Log.d(TAG, email);
				// set the required fields
				// u.setUserID(node.get(KEY_ID).asText());
				u.setDisplayName(node.get(KEY_NAME).asText());
				u.setEmail(email);
				// check and set the optional fields
				u.setLocation(node.hasNonNull(KEY_LOCATION) ? node.get(
						KEY_LOCATION).asText() : "");
				u.setPhone(node.hasNonNull(KEY_PHONE) ? node.get(KEY_PHONE)
						.asText() : "");
				u.setCompany(node.hasNonNull(KEY_COMPANY) ? node.get(
						KEY_COMPANY).asText() : "");
				u.setTitle(node.hasNonNull(KEY_TITLE) ? node.get(KEY_TITLE)
						.asText() : "");
			} else {
				Log.w(TAG, "Parsed null. NAME = " + node.get(KEY_NAME).asText());
				return null;
			}
		} else {
			Log.w(TAG, "Parsed null");
			return null;
		}
		return u;
	}

	public static List<User> parseUserList(JsonNode node) {
		List<User> userList = new ArrayList<User>();
		final JsonNode userArray = node.get("users");

		if (userArray.isArray()) {
			for (final JsonNode userNode : userArray) {
				User u = parseUser(userNode);
				// assign and check null and do not add local user
				if (u != null
						&& !TextUtils.equals(u.getUserID(), SessionManager
								.getInstance().getUserID())) {
					userList.add(u);
				}
			}
		}

		return userList;
	}

	public static SimpleUser parseSimpleUser(JsonNode node) {
		SimpleUser u = new SimpleUser();
		if (node.hasNonNull(KEY_NAME)) {
			u.setUserID(node.get(KEY_ID).asText());
			u.setDisplayName(node.get(KEY_NAME).asText());
		} else {
			return null;
		}
		return u;
	}

	public static Schedule parseSchedule(JsonNode node) {
		// Initialize ObjectMapper
		Schedule sched = null;
		Event event = null;
		final JsonNode scheduleArray = node.get("schedule");

		JsonNode _id;
		if (scheduleArray.isArray()) {
			sched = new Schedule();
			for (final JsonNode meetingOrTaskNode : scheduleArray) {
				if ((_id = meetingOrTaskNode.get("id")) != null) {
					String type = meetingOrTaskNode.hasNonNull("type") ? meetingOrTaskNode
							.get("type").asText() : null;
					if (TextUtils.equals(type, "meeting")) {
						event = new Meeting();
					} else if (TextUtils.equals(type, "task")) {
						event = new Task();
					}
					if (event != null) {
						event.setID(_id.asText());
						event.setTitle(meetingOrTaskNode.get(
								MeetingDatabaseAdapter.KEY_TITLE).asText());
						event.setDescription(meetingOrTaskNode.get(
								MeetingDatabaseAdapter.KEY_DESC).asText());
						event.setStartTime(meetingOrTaskNode.get(
								MeetingDatabaseAdapter.KEY_START).asText());
						event.setEndTime(meetingOrTaskNode.get(
								MeetingDatabaseAdapter.KEY_END).asText());
						if (event instanceof Meeting)
							sched.addMeeting((Meeting) event);
						else if (event instanceof Task)
							sched.addTask((Task) event);
						else
							Log.w(TAG + "> getSchedule", "Event cast failure");
					}
				}
			} // end for

		}

		return sched;
	}
}
