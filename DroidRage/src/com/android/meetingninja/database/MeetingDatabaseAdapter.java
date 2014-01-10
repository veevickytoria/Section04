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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.Meeting;
import objects.Meeting.AttendeeWrapper;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class MeetingDatabaseAdapter extends DatabaseAdapter {

	private final static String SERVER_EXT = "Meetings";

	private final static String KEY_ID = "meetingID";
	private final static String KEY_TITLE = "title";
	private final static String KEY_LOCATION = "location";
	private final static String KEY_DATETIME = "datetime";
	private final static String KEY_START = "datetimeStart";
	private final static String KEY_END = "datetimeEnd";
	private final static String KEY_DESC = "description";
	private final static String KEY_ATTEND = "attendance";

	public static List<Meeting> getMeetings(String userID)
			throws JsonParseException, JsonMappingException, IOException {
		// Server URL setup
		String _url = BASE_URL + SERVER_EXT + "/" + userID;
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
		final JsonNode meetingsArray = MAPPER.readTree(response)
				.get("meetings");

		if (meetingsArray.isArray()) {
			for (final JsonNode meetingNode : meetingsArray) {

			}
		}

		conn.disconnect();
		return meetingsList;
	}

	public static List<Meeting> getSchedule(String userID)
			throws JsonParseException, JsonMappingException, IOException {
		// Server URL setup
		String _url = BASE_URL + SERVER_EXT + "/" + userID;
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
		String response = getMockMeetingJSON();

		// Initialize ObjectMapper
		List<Meeting> meetingsList = new ArrayList<Meeting>();
		final JsonNode scheduleArray = MAPPER.readTree(response)
				.get("schedule");

		if (scheduleArray.isArray()) {
			for (final JsonNode meetingNode : scheduleArray) {
				Meeting m = new Meeting();
				if (meetingNode.hasNonNull(KEY_TITLE)) {
					m.setTitle(meetingNode.get(KEY_TITLE).asText());
					m.setDescription(meetingNode.get(KEY_DESC).asText());
					m.setStartTime(meetingNode.get("datetimeStart").asText());
					m.setEndTime(meetingNode.get("datetimeEnd").asText());
				}
				meetingsList.add(m);
			}

		}

		conn.disconnect();
		return meetingsList;
	}

	public static Meeting createMeeting(String userID, Meeting m)
			throws IOException, MalformedURLException {
		// Server URL setup
		String _url = BASE_URL + SERVER_EXT;

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
		jgen.writeStringField("userID", userID);
		jgen.writeStringField(KEY_TITLE, m.getTitle());
		jgen.writeStringField(KEY_LOCATION, m.getLocation());
		jgen.writeStringField("datetime", m.getStartTime());
		jgen.writeArrayFieldStart("attendance");
		// TODO: Add attendees to meeting
		for (AttendeeWrapper attendee : m.getAttendance()) {
			if (attendee.isAttending()) {
				jgen.writeStartObject();
				jgen.writeStringField("userID", attendee.getID());
				jgen.writeEndObject();
			}
		}
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		// prepare to get the id of the created Meeting
		Map<String, String> responseMap = new HashMap<String, String>();
		Meeting created = new Meeting(m);

		/*
		 * result should get valid={"meetingID":"##"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			responseMap = MAPPER.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey("meetingID")) {
				result = "invalid";
			} else
				result = responseMap.get("meetingID");
		}

		if (!result.equalsIgnoreCase("invalid"))
			created.setID(result);

		conn.disconnect();
		return created;
	}

	private static String getMockMeetingJSON() {
		return "{\"schedule\":" + "[{\"title\":\"get the milk\","
				+ "\"description\":\"2% milk from kroger\", "
				+ "\"datetimeStart\":\"Monday, 15-Aug-15 23:59:59 UTC\", "
				+ "\"datetimeEnd\":\"Monday, 16-Aug-15 23:59:59 UTC\"}]}";
	}
}
