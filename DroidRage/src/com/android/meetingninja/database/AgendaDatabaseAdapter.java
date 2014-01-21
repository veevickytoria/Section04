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
import java.util.HashMap;
import java.util.Map;

import objects.Agenda;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class AgendaDatabaseAdapter extends BaseDatabaseAdapter {
	private static final String TAG = UserDatabaseAdapter.class.getSimpleName();

	protected static final String KEY_ID = "agendaID";
	protected static final String KEY_TITLE = "title";
	protected static final String KEY_MEETING = "meeting";
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

	public static Agenda createAgenda(Agenda create) throws IOException {
		Agenda newAgenda = new Agenda(create);
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

		// Build JSON Object
		jgen.writeStartObject(); // start agenda
		jgen.writeStringField(KEY_TITLE, create.getTitle());
		jgen.writeStringField(KEY_MEETING, create.getAttachedMeetingID());
		jgen.writeArrayFieldStart(KEY_CONTENT); // start topics
		MAPPER.writeValue(jgen, create.getTopics()); // recursively does
														// subtopics
		jgen.writeEndArray(); // end topics
		jgen.writeEndObject(); // end agenda
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		// Send payload
		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		newAgenda = parseAgenda(MAPPER.readTree(response));

		return newAgenda;
	}

	public static String update(String agendaID, Map<String, String> key_values)
			throws JsonGenerationException, IOException, InterruptedException {
		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);

		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		for (String key : key_values.keySet()) {
			jgen.flush();
			// Build JSON Object
			jgen.writeStartObject();
			jgen.writeStringField(KEY_ID, agendaID);
			jgen.writeStringField("field", key);
			jgen.writeStringField("value", key_values.get(key));
			jgen.writeEndObject();
			jgen.writeRaw("\f"); // write a form-feed to separate the payloads
		}

		jgen.close();
		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();
		// The backend can only update a single field at a time
		String[] payloads = payload.split("\f\\s*"); // split at each form-feed
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
		return response;
	}

	public static boolean deleteAgenda(String agendaID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(agendaID).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.DELETE);
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		/*
		 * result should get valid={"userID":"##"}
		 * invalid={"errorID":"##","errorMessage":"error"}
		 */
		HashMap<String, String> responseMap;
		boolean result = false;
		if (!response.isEmpty()) {
			responseMap = MAPPER.readValue(response,
					new TypeReference<HashMap<String, String>>() {
					});
			if (!responseMap.containsKey("deleted")) {
				result = true;
			} else {
				Log.e(TAG,
						String.format("ErrorID: [%s] %s",
								responseMap.get(ERROR_ID),
								responseMap.get(ERROR_MESSAGE)));
			}
		}

		conn.disconnect();
		return result;

	}

	public static Agenda get(String agendaID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(agendaID).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.GET);
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
}
