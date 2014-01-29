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
package com.meetingninja.csse.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import objects.Event;
import objects.Group;
import objects.Meeting;
import objects.MockObjectFactory;
import objects.Note;
import objects.Project;
import objects.Schedule;
import objects.Task;
import objects.User;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.extras.JsonUtils;
import com.meetingninja.csse.extras.Utilities;

public class UserDatabaseAdapter extends BaseDatabaseAdapter {

	private static final String TAG = UserDatabaseAdapter.class.getSimpleName();

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
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);
		JsonNode userNode = MAPPER.readTree(response);

		return parseUser(userNode);
	}

	public static void fetchUserInfo(String userID,
			final AsyncResponse<User> delegate) {
		String _url = getBaseUri().appendPath(userID).build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							delegate.processFinish(parseUser(response));
						} else {
							error.printStackTrace();
						}

					}
				});

		ApplicationController.getInstance().addToRequestQueue(req, "JSON");
	}

	public static List<User> getContacts(String userID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Contacts").appendPath(userID)
				.build().toString();
		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		List<User> contactsList = new ArrayList<User>();
		List<String> contactIds = new ArrayList<String>();
		final JsonNode contactsArray = MAPPER.readTree(response).get(
				Keys.User.CONTACTS);

		if (contactsArray.isArray()) {
			for (final JsonNode userNode : contactsArray) {
				// User u = parseSimpleUser(userNode);
				// if (u != null)
				contactIds.add(userNode.get(Keys.User.ID).asText());
			}
		}

		conn.disconnect();

		for (String id : contactIds) {
			User contact = getUserInfo(id);
			if (contact != null) {
				contactsList.add(contact);
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
		conn.setRequestMethod(IRequest.POST);
		addRequestHeader(conn, true);

		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		try {
			// hash the password
			pass = Utilities.computeHash(pass);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField(Keys.User.EMAIL, email);
		jgen.writeStringField("password", pass);
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		/*
		 * result should get valid={"userID":"##"}
		 * invalid={"errorID":"3","errorMessage":"invalid username or password"}
		 */
		String result = "";
		if (!response.isEmpty()) {
			JsonNode tree = MAPPER.readTree(response);
			if (!tree.has(Keys.User.ID)) {
				logError(TAG, tree);
				result = "invalid username or password";
			} else
				result = tree.get(Keys.User.ID).asText();
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
		conn.setRequestMethod(IRequest.GET);
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

	public static List<Meeting> getMeetings(String userID)
			throws JsonParseException, JsonMappingException, IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath("Meetings").appendPath(userID)
				.build().toString();

		// establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Meeting> meetingsList = new ArrayList<Meeting>();
		List<String> meetingIDList = new ArrayList<String>();
		final JsonNode meetingsArray = MAPPER.readTree(response).get(
				Keys.Meeting.LIST);

		if (meetingsArray.isArray()) {
			for (final JsonNode meetingNode : meetingsArray) {
				String _id = meetingNode.get(Keys._ID).asText();
				if (!meetingNode.get("type").asText().equals("MADE_MEETING")) {
					meetingIDList.add(_id);
				}
			}
		}

		conn.disconnect();
		for (String id : meetingIDList) {
			meetingsList.add(MeetingDatabaseAdapter.getMeetingInfo(id));
		}
		return meetingsList;
	}

	public static List<User> getAllUsers() throws IOException {
		String _url = getBaseUri().appendPath("Users").build().toString();
		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		List<User> userList = new ArrayList<User>();
		final JsonNode userArray = MAPPER.readTree(response)
				.get(Keys.User.LIST);

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
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Group> groupList = new ArrayList<Group>();
		final JsonNode groupArray = MAPPER.readTree(response).get(
				Keys.Group.LIST);

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
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Project> projectList = new ArrayList<Project>();
		final JsonNode projectArray = MAPPER.readTree(response).get(
				Keys.Project.LIST);

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
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Note> noteList = new ArrayList<Note>();
		final JsonNode noteArray = MAPPER.readTree(response)
				.get(Keys.Note.LIST);

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
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Task> taskList = new ArrayList<Task>();
		final JsonNode taskArray = MAPPER.readTree(response)
				.get(Keys.Task.LIST);

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
		for (Task t : taskList) {
			TaskDatabaseAdapter.getTask(t);
		}
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
		conn.setRequestMethod(IRequest.POST);
		addRequestHeader(conn, true);

		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		password = Utilities.computeHash(password);
		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField(Keys.User.NAME, registerMe.getDisplayName());
		jgen.writeStringField("password", password);
		jgen.writeStringField(Keys.User.EMAIL, registerMe.getEmail());
		jgen.writeStringField(Keys.User.PHONE, registerMe.getPhone());
		jgen.writeStringField(Keys.User.COMPANY, registerMe.getCompany());
		jgen.writeStringField(Keys.User.TITLE, registerMe.getTitle());
		jgen.writeStringField(Keys.User.LOCATION, registerMe.getLocation());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		User createUser = new User(registerMe);

		/*
		 * result should get valid={"userID":"##"}
		 */
		String result = "";
		if (!response.isEmpty()) {
			JsonNode tree = MAPPER.readTree(response);
			if (!tree.has(Keys.User.ID)) {

				result = "duplicate email or username";
			} else {
				result = tree.get(Keys.User.ID).asText();
				createUser.setID(result);
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
			if (key.equals(Keys.User.EMAIL)
					&& !Utilities.isValidEmailAddress(key_values
							.get(Keys.User.EMAIL)))
				throw new IOException(
						"Error : [Update User] Incorrect email format");
			else {
				jgen.flush();
				// Build JSON Object
				jgen.writeStartObject();
				jgen.writeStringField(Keys.User.ID, userID);
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

	private void deleteUser(String userID, final AsyncResponse<Boolean> delegate) {
		String url = UserDatabaseAdapter.getBaseUri().appendPath(userID)
				.build().toString();

		JsonNodeRequest del_req = new JsonNodeRequest(Request.Method.DELETE,
				url, null, new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							delegate.processFinish(response.get(Keys.DELETED)
									.asBoolean(false));
						} else {
							error.printStackTrace();
						}

					}
				});
		ApplicationController.getInstance().addToRequestQueue(del_req);

	}

	public static void fetchAllUsers(final AsyncResponse<List<User>> delegate) {
		String _url = getBaseUri().appendPath("Users").build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							// callback to UI thread
							delegate.processFinish(parseUserList(response));
						} else {
							error.printStackTrace();
						}
					}
				});
		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req, "JSON");

	}

	public static User parseUser(JsonNode node) {
		User u = new User(); // start parsing a user
		// if they at least have an id, email, and name
		if (node.hasNonNull(Keys.User.EMAIL) && node.hasNonNull(Keys.User.NAME)
		// && node.hasNonNull(KEY_ID)
		) {
			String email = node.get(Keys.User.EMAIL).asText();
			// if their email is in a reasonable format
			if (!TextUtils.isEmpty(node.get(Keys.User.NAME).asText())
					&& Utilities.isValidEmailAddress(email)) {
				// set the required fields
				if (node.hasNonNull(Keys.User.ID))
					u.setID(node.get(Keys.User.ID).asText());
				u.setDisplayName(node.get(Keys.User.NAME).asText());
				u.setEmail(email);

				// check and set the optional fields
				u.setLocation(JsonUtils.getJSONValue(node, Keys.User.LOCATION));
				u.setPhone(JsonUtils.getJSONValue(node, Keys.User.PHONE));
				u.setCompany(JsonUtils.getJSONValue(node, Keys.User.COMPANY));
				u.setTitle(JsonUtils.getJSONValue(node, Keys.User.TITLE));
			} else {
				// Log.w(TAG, "Parsed null user");
				return null;
			}
		} else {
			// Log.w(TAG, "Parsed null user");
			return null;
		}
		return u;
	}

	public static List<User> parseUserList(JsonNode node) {
		List<User> userList = new ArrayList<User>();
		final JsonNode userArray = node.get(Keys.User.LIST);

		if (userArray.isArray()) {
			for (final JsonNode userNode : userArray) {
				User u = parseUser(userNode);
				// assign and check null and do not add local user
				if (u != null
						&& !TextUtils.equals(u.getID(), SessionManager
								.getInstance().getUserID())) {
					userList.add(u);
				}
			}
		}

		return userList;
	}

	// public static SimpleUser parseSimpleUser(JsonNode node) {
	// SimpleUser u = new SimpleUser();
	// if (node.hasNonNull(Keys.User.NAME)) {
	// u.setUserID(node.get(Keys.User.ID).asText());
	// u.setDisplayName(node.get(Keys.User.NAME).asText());
	// } else {
	// Log.w(TAG, "Parsed null user");
	// return null;
	// }
	// return u;
	// }

	public static Schedule parseSchedule(JsonNode node) {
		// Initialize ObjectMapper
		Schedule sched = null;
		Event event = null; // task or meeting
		final JsonNode scheduleArray = node.get(Keys.User.SCHEDULE);

		JsonNode _id;
		if (scheduleArray.isArray()) {
			sched = new Schedule(); // start parsing a schedule
			for (final JsonNode meetingOrTaskNode : scheduleArray) {
				if ((_id = meetingOrTaskNode.get(Keys._ID)) != null) {
					// Get the type of event
					String type = JsonUtils.getJSONValue(meetingOrTaskNode,
							Keys.TYPE);

					if (TextUtils.equals(type, "meeting")) {
						event = new Meeting();
					} else if (TextUtils.equals(type, "task")) {
						event = new Task();
					}
					if (event != null) {
						event.setID(_id.asText());
						event.setTitle(JsonUtils.getJSONValue(
								meetingOrTaskNode, Keys.Meeting.TITLE));
						event.setDescription(JsonUtils.getJSONValue(
								meetingOrTaskNode, Keys.Meeting.DESC));
						event.setStartTime(JsonUtils.getJSONValue(
								meetingOrTaskNode, Keys.Meeting.START));
						event.setEndTime(JsonUtils.getJSONValue(
								meetingOrTaskNode, Keys.Meeting.END));

						// Add event to the schedule
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
