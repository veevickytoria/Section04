package com.android.meetingninja.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import objects.Agenda;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class AgendaDatabaseAdapter extends AbstractDatabaseAdapter {
	private static final String TAG = UserDatabaseAdapter.class.getSimpleName();

	protected static final String KEY_ID = "agendaID";
	protected static final String KEY_TITLE = "title";
	protected static final String KEY_CONTENT = "content";
	protected static final String KEY_SUBTOPIC = "subtopic";
	protected static final String KEY_TOPIC = "topic";
	protected static final String KEY_TIME = "time";
	protected static final String KEY_DESC = "description";

	public static String getBaseUrl() {
		return BASE_URL + "Agenda";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static Agenda getAgenda(String agendaID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(agendaID).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);
		JsonNode agendaNode = MAPPER.readTree(response);

		return parseAgenda(agendaNode);
	}

	public static Agenda parseAgenda(JsonNode agendaNode)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(agendaNode.toString(), Agenda.class);
	}

	public static Agenda createAgenda(Agenda ag) throws IOException {
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
		jgen.writeStringField(KEY_TITLE, ag.getTitle());
		jgen.writeStringField("meeting", ag.getAttachedMeetingID());
		jgen.writeArrayFieldStart(KEY_CONTENT);
		MAPPER.writeValue(jgen, ag.getTopics());
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);
		JsonNode agendaNode = MAPPER.readTree(response);
		
		return parseAgenda(agendaNode);
	}
}
