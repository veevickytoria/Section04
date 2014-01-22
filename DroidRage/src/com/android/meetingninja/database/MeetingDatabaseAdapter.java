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
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class MeetingDatabaseAdapter extends BaseDatabaseAdapter {
	private final static String TAG = MeetingDatabaseAdapter.class
			.getSimpleName();

	public static String getBaseUrl() {
		return BASE_URL + "Meetings";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static List<Meeting> getMeetings(String userID)
			throws JsonParseException, JsonMappingException, IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(userID).build().toString();

		// establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		List<Meeting> meetingsList = new ArrayList<Meeting>();
		final JsonNode meetingsArray = MAPPER.readTree(response).get(
				Keys.Meeting.LIST);

		if (meetingsArray.isArray()) {
			for (final JsonNode meetingNode : meetingsArray) {

			}
		}

		conn.disconnect();
		return meetingsList;
	}

	public static Meeting createMeeting(String userID, Meeting m)
			throws IOException, MalformedURLException {
		// Server URL setup
		String _url = getBaseUri().build().toString();

		// establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod(IRequest.POST);
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
		jgen.writeStringField(Keys.Meeting.TITLE, m.getTitle());
		jgen.writeStringField(Keys.Meeting.LOCATION, m.getLocation());
		jgen.writeStringField(Keys.Meeting.DATETIME, m.getStartTime());
		jgen.writeArrayFieldStart(Keys.Meeting.ATTEND);
		// TODO: Add attendees to meeting
		for (String attendee : m.getAttendance()) {
			// if (attendee.isAttending()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.User.ID, attendee);
			jgen.writeEndObject();
			// }
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
		String result = "";
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

	public static Meeting parseMeeting(JsonNode node) {
		Meeting m = new Meeting();
		if (node.hasNonNull(Keys.Meeting.ID)) {
			m.setID(node.get(Keys.Meeting.ID).asText());
			m.setTitle(node.get(Keys.Meeting.TITLE).asText());
			m.setLocation(node.get(Keys.Meeting.LOCATION).asText());
			m.setStartTime(node.get(Keys.Meeting.START).asText());
			m.setEndTime(node.get(Keys.Meeting.END).asText());
			m.setDescription(node.get(Keys.Meeting.DESC).asText());
			JsonNode attendance = node.get(Keys.Meeting.ATTEND);
			if (attendance.isArray()) {
				for (final JsonNode attendeeNode : attendance) {
					m.addAttendee(attendeeNode.get(Keys.User.ID).asText());
				}
			}
		} else {
			Log.e(TAG, "Error: Meeting failed to parse");
			return null;
		}

		return m;
	}
}
