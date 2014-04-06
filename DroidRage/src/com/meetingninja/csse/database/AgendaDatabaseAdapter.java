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
import java.util.Map;

import objects.Agenda;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.extras.JsonUtils;
import com.meetingninja.csse.extras.SleeperThread;

public class AgendaDatabaseAdapter extends BaseDatabaseAdapter {
	private static final String TAG = UserDatabaseAdapter.class.getSimpleName();

	public static String getBaseUrl() {
		return BASE_URL + "Agenda";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static String createAgenda(Agenda create) throws IOException {
		Agenda newAgenda = new Agenda(create);

		String _url = getBaseUri().build().toString();

		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(IRequest.POST);
		addRequestHeader(conn, true);

		String payload = create.toJSON().toString();

		int responseCode = sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		// TODO : FIXME
		// newAgenda = parseAgenda(MAPPER.readTree(response));
		return getAgendaID(MAPPER.readTree(response));

	}

	public static JsonNode update(String agendaID,
			Map<String, String> key_values) throws JsonGenerationException,
			IOException, InterruptedException {

		ByteArrayOutputStream json = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(json);

		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		for (String key : key_values.keySet()) {
			jgen.flush();
			// Build JSON Object
			jgen.writeStartObject();
			jgen.writeStringField(Keys.Agenda.ID, agendaID);
			jgen.writeStringField("field", key);
			jgen.writeStringField("value", key_values.get(key));
			jgen.writeEndObject();
			jgen.writeRaw("\f"); // write a form-feed to separate the payloads
		}

		jgen.close();

		String payload = json.toString("UTF8");
		ps.close();

		String[] payloads = payload.split("\f\\s*"); // split at each form-feed

		String response = null;
		SleeperThread sleeper = new SleeperThread(500);
		for (String p : payloads) {
			sleeper.run();
			response = updateHelper(getBaseUri().build().toString(), p);
		}
		return MAPPER.readTree(response);
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

		boolean result = false;
		JsonNode tree = MAPPER.readTree(response);
		if (!response.isEmpty()) {
			if (!tree.hasNonNull(Keys.DELETED)) {
				result = true;
			} else {
				logError(TAG, tree);
			}
		}

		conn.disconnect();
		return result;

	}

	public static Agenda getAgenda(String agendaID) throws IOException {
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

	public static String getAgendaID(JsonNode agendaNode) {
		return JsonUtils.getJSONValue(agendaNode, Keys.Agenda.ID);
	}
}
