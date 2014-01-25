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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import objects.Meeting;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class MeetingDatabaseAdapter extends BaseDatabaseAdapter {
	private final static String TAG = MeetingDatabaseAdapter.class
			.getSimpleName();

	public static String getBaseUrl() {
		return BASE_URL + "Meeting";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static Meeting getMeetingInfo(String meetingID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(meetingID).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);
		JsonNode meetingNode = MAPPER.readTree(response);

		return parseMeeting(meetingNode, meetingID);
	}

	public static Meeting createMeeting(String userID, Meeting m)
			throws IOException, MalformedURLException {
		// Server URL setup
		String _url = getBaseUri().appendPath(userID).build().toString();

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
		jgen.writeStringField(Keys.User.ID, userID);
		jgen.writeStringField(Keys.Meeting.TITLE, m.getTitle());
		jgen.writeStringField(Keys.Meeting.LOCATION, m.getLocation());
		jgen.writeStringField(Keys.Meeting.DATETIME, m.getStartTime());
		jgen.writeStringField("endDatetime", m.getEndTime());
		jgen.writeStringField(Keys.Meeting.DESC, m.getDescription());
		jgen.writeArrayFieldStart(Keys.Meeting.ATTEND);
		// TODO: Add attendees to meeting
		// for (String attendee : m.getAttendance()) {
		// if (attendee.isAttending()) {
		jgen.writeStartObject();
		jgen.writeStringField(Keys.User.ID, userID);
		jgen.writeEndObject();
		// }
		// }
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
		m.setID(404);
		Meeting created = new Meeting(m);

		/*
		 * result should get valid={"meetingID":"##"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			responseMap = MAPPER.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey(Keys.Meeting.ID)) {
				result = "invalid";
			} else
				result = responseMap.get(Keys.Meeting.ID);
		}

		if (!result.equalsIgnoreCase("invalid"))
			created.setID(result);

		conn.disconnect();
		return created;
	}

	public static Meeting parseMeeting(JsonNode node, String meetingID) {
		Meeting m = new Meeting();
		if (meetingID != null) {
			m.setID(meetingID);
			// if (m.getID().isEmpty())
			// m.setID(node.get(KEY_ID).asText());
			m.setTitle(node.get(Keys.Meeting.TITLE).asText());
			m.setLocation(node.get(Keys.Meeting.LOCATION).asText());
			m.setStartTime(node.get(Keys.Meeting.DATETIME).asText());
			m.setEndTime(node.get("endDatetime").asText());
			m.setDescription(node.get(Keys.Meeting.DESC).asText());
			// JsonNode attendance = node.get(KEY_ATTEND);
			// if (attendance.isArray()) {
			// for (final JsonNode attendeeNode : attendance) {
			// m.addAttendee(attendeeNode.get("userID").asText());
			// }
			// }
		}

		return m;
	}
}
