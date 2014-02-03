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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import objects.Group;
import objects.User;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.database.BaseDatabaseAdapter.IRequest;

public class GroupDatabaseAdapter extends BaseDatabaseAdapter {

	public static String getBaseUrl() {
		return BASE_URL + "Group";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}
	
	public static Group getGroup(String groupID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(groupID).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);
		JsonNode groupNode = MAPPER.readTree(response);

		return parseGroup(groupNode, new Group());
	}

	public static Group createGroup(Group g)
			throws IOException, MalformedURLException {
		// Server URL setup
		String _url = getBaseUri().build().toString();

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
		jgen.writeStringField(Keys.Group.TITLE, g.getGroupTitle());
		jgen.writeArrayFieldStart(Keys.Group.MEMBERS);
		for (User member : g.getMembers()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.User.ID, member.getID());
			jgen.writeEndObject();

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
//		Map<String, String> responseMap = new HashMap<String, String>();

		/*
		 * result should get valid={"meetingID":"##"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
//			responseMap = MAPPER.readValue(response,
//					new TypeReference<HashMap<String, String>>() {
//					});
			JsonNode groupNode = MAPPER.readTree(response);
			if (!groupNode.has(Keys.Group.ID)) {
				result = "invalid";
			} else
				result = groupNode.get(Keys.Group.ID).asText();
		}

		if (!result.equalsIgnoreCase("invalid"))
			g.setID(result);

		conn.disconnect();
		return g;
	}


	public static Group updateGroup(Group group) throws IOException {
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Title
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Group.ID, group.getGroupID());
		jgen.writeStringField("field", Keys.Group.TITLE);
		jgen.writeStringField("value", group.getGroupTitle());
		jgen.writeEndObject();
		jgen.close();
		String payloadTitle = json.toString("UTF8");
		ps.close();
		
		json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		ps = new PrintStream(json);
		jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Group members
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Group.ID, group.getGroupID());
		jgen.writeStringField("field", Keys.Group.MEMBERS);
		jgen.writeArrayFieldStart("value");
		for (User member : group.getMembers()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.User.ID, member.getID());
			jgen.writeEndObject();

		}
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();
		String payloadMembers = json.toString("UTF8");
		ps.close();
		// Establish connection
		sendSingleEdit(payloadTitle);
		String response = sendSingleEdit(payloadMembers);
		JsonNode groupNode = MAPPER.readTree(response);

		return parseGroup(groupNode, new Group());
	}
	
	private static String sendSingleEdit(String payload) throws IOException {
		String _url = getBaseUri().build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(IRequest.PUT);
		addRequestHeader(conn, false);
		sendPostPayload(conn, payload);
		return getServerResponse(conn);

	}
	
	public static Group parseGroup(JsonNode groupNode, Group g) {
//		Group g = new Group();
		String groupID = groupNode.get(Keys.Group.ID).asText();
		if (groupID != null) {
			g.setID(groupID);
			g.setGroupTitle(groupNode.get(Keys.Group.TITLE).asText());
			JsonNode members = groupNode.get(Keys.Group.MEMBERS);
			if (members != null && members.isArray()) {
				for (final JsonNode memberNode : members) {
					User user = new User();
					user.setID(memberNode.get(Keys.User.ID).asText());
					g.addMember(user);
				}
			}
		}

		return g;
	}

}
