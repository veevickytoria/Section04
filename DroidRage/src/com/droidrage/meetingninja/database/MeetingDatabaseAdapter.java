package com.droidrage.meetingninja.database;

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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MeetingDatabaseAdapter extends DatabaseAdapter {

	public static List<Meeting> getMeetings(String username)
			throws JsonParseException, JsonMappingException, IOException {
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

	public static Meeting createMeeting(String userID, Meeting m)
			throws IOException, MalformedURLException {
		// Server URL setup
		String method = "Meeting";
		String _url = String.format("%s%s/", SERVER_NAME, method);

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
		jgen.writeStringField("title", m.getTitle());
		jgen.writeStringField("location", m.getLocation());
		jgen.writeStringField("datetime", m.getDatetime());
		jgen.writeArrayFieldStart("attendance");
		jgen.writeStartArray();
		// TODO:
		// for (UserAttendance attendee : m.getAttendees()) {
		// jgen.writeStartObject();
		// jgen.writeStringField(attendee.getUserName(),
		// attendee.isAttending().toString());
		// jgen.writeEndObject();
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

		Map<String, String> responseMap = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();

		/*
		 * result should get valid={"meetingID":"##"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			responseMap = mapper.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey("meetingID")) {
				result = "invalid";
			} else
				result = responseMap.get("meetingID");
		}

		if (!result.equalsIgnoreCase("invalid"))
			m.setID(result);

		conn.disconnect();
		return m;
	}
}
