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
import objects.Note;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.extras.JsonUtils;

public class NotesDatabaseAdapter extends BaseDatabaseAdapter {

	public static String getBaseUrl() {
		return BASE_URL + "Note";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static Note getNote(final String noteID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(noteID).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);

		conn.getResponseCode();
		String response = getServerResponse(conn);

		return parseNote(MAPPER.readTree(response));
	}

	public static String createNote(Note create) throws Exception {
		// Server URL setup
		String _url = getBaseUri().build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.POST);
		// addRequestHeader(conn, true);

		// prepare POST payload
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);

		// Build JSON Object
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Note.CREATED_BY, create.getCreatedBy());
		jgen.writeStringField(Keys.Note.TITLE, create.getTitle());
		jgen.writeStringField(Keys.Note.DESC, create.getDescription());
		jgen.writeStringField(Keys.Note.CONTENT, create.getContent());
		jgen.writeStringField(Keys.Note.UPDATED, create.getDateCreated());
		jgen.writeEndObject();
		jgen.close();

		// Get JSON Object payload from print stream
		String payload = json.toString("UTF8");
		ps.close();

		sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		String _id = null;
		if (!response.isEmpty()) {
			JsonNode tree = MAPPER.readTree(response);
			if (!tree.hasNonNull(Keys.Note.ID))
				_id = "-1";
			else
				_id = tree.get(Keys.Note.ID).asText();
		}

		conn.disconnect();
		return _id;
	}

	public static Boolean deleteNote(String noteID) throws IOException {
		String _url = getBaseUri().appendPath(noteID).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.DELETE);
		addRequestHeader(conn, false);

		conn.getResponseCode();
		String response = getServerResponse(conn);

		boolean result = false;
		JsonNode tree = MAPPER.readTree(response);
		if (!response.isEmpty()) {
			if (!tree.has(Keys.DELETED)) {
				result = true;
			} else {
				logError("note.del.err", tree);
			}
		}

		conn.disconnect();
		return result;
	}

	public static Note parseNote(JsonNode node, String noteID) {
		Note n = new Note();
		n.setID(JsonUtils.getJSONValue(node, Keys.Note.ID));
		n.setTitle(JsonUtils.getJSONValue(node, Keys.Note.TITLE));
		n.setContent(JsonUtils.getJSONValue(node, Keys.Note.CONTENT));
		n.setDescription(JsonUtils.getJSONValue(node, Keys.Note.DESC));
		n.setCreatedBy(JsonUtils.getJSONValue(node, Keys.Note.CREATED_BY));
		n.setDateCreated(JsonUtils.getJSONValue(node, Keys.Note.UPDATED));
		return n;
	}

	public static Note parseNote(JsonNode node) {
		Note n = new Note();
		n.setID(JsonUtils.getJSONValue(node, Keys.Note.ID));
		n.setTitle(JsonUtils.getJSONValue(node, Keys.Note.TITLE));
		n.setContent(JsonUtils.getJSONValue(node, Keys.Note.CONTENT));
		n.setDescription(JsonUtils.getJSONValue(node, Keys.Note.DESC));
		n.setCreatedBy(JsonUtils.getJSONValue(node, Keys.Note.CREATED_BY));
		n.setDateCreated(JsonUtils.getJSONValue(node, Keys.Note.UPDATED));
		return n;
	}

	public static void updateNote(Note n) throws IOException {
		String _url = getBaseUri().build().toString();
		Log.d("updatenote", "update");
		String keyValue = Keys.Note.ID;

		String titlePayload = getEditPayload(n.getID(), Keys.Note.TITLE,n.getTitle(),keyValue);
		String descPayload = getEditPayload(n.getID(), Keys.Note.CONTENT,n.getContent(),keyValue);
		// Get server response
		sendSingleEdit(titlePayload,_url);
		sendSingleEdit(descPayload,_url);
	}

}
