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
import java.util.List;

import objects.Meeting;
import objects.Note;
import objects.Project;
import objects.User;

import android.net.Uri;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;

public class ProjectDatabaseAdapter extends BaseDatabaseAdapter {

	public static String getBaseUrl() {
		return BASE_URL + "Project";
	}
	
	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static Project getProject(Project p) throws IOException{

		// Server URL setup
		String _url = getBaseUri().appendPath(p.getProjectID()).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		conn.getResponseCode();
		String response = getServerResponse(conn);
		JsonNode projectNode = MAPPER.readTree(response);
			
		return parseProject(projectNode, p);
	}


	public static Project createProject(Project p) throws IOException,
	MalformedURLException {
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
		jgen.writeStringField(Keys.Project.TITLE, p.getProjectTitle());
		jgen.writeArrayFieldStart(Keys.Project.MEETINGS);
		for (Meeting meeting : p.getMeetings()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.Meeting.ID, meeting.getID());
			jgen.writeEndObject();

		}
		jgen.writeEndArray();
		jgen.writeArrayFieldStart(Keys.Project.NOTES);
		for (Note note : p.getNotes()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.Note.ID, note.getID());
			jgen.writeEndObject();

		}
		jgen.writeEndArray();
		jgen.writeArrayFieldStart(Keys.Project.MEMBERS);
		for (User member : p.getMembers()) {
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
		sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		// prepare to get the id of the created Meeting
		// Map<String, String> responseMap = new HashMap<String, String>();

		/*
		 * result should get valid={"meetingID":"##"}
		 */
		String result = new String();
		if (!response.isEmpty()) {
			// responseMap = MAPPER.readValue(response,
			// new TypeReference<HashMap<String, String>>() {
			// });
			JsonNode projectNode = MAPPER.readTree(response);
			if (!projectNode.has(Keys.Project.ID)) {
				result = "invalid";
			} else
				result =  projectNode.get(Keys.Project.ID).asText();
		}

		if (!result.equalsIgnoreCase("invalid"))
			p.setProjectID(result);

		conn.disconnect();
		return p;
	}


	public static void updateProject(Project p) throws IOException {
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Title
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Project.ID, p.getProjectID());
		jgen.writeStringField("field", Keys.Project.TITLE);
		jgen.writeStringField("value", p.getProjectTitle());
		jgen.writeEndObject();
		jgen.close();
		String payloadTitle = json.toString("UTF8");
		ps.close();

		json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		ps = new PrintStream(json);
		jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Project members
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Project.ID, p.getProjectID());
		jgen.writeStringField("field", Keys.Project.MEETINGS);
		jgen.writeArrayFieldStart("value");
		for (Meeting meeting : p.getMeetings()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.Meeting.ID, meeting.getID());
			jgen.writeEndObject();

		}
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();
		String payloadMeetings = json.toString("UTF8");
		ps.close();
		
		
		json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		ps = new PrintStream(json);
		jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Project members
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Project.ID, p.getProjectID());
		jgen.writeStringField("field", Keys.Project.NOTES);
		jgen.writeArrayFieldStart("value");
		for (Note note : p.getNotes()) {
			jgen.writeStartObject();
			jgen.writeStringField(Keys.Note.ID, note.getID());
			jgen.writeEndObject();

		}
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();
		String payloadNotes = json.toString("UTF8");
		ps.close();
		
		json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		ps = new PrintStream(json);
		jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Project members
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Project.ID, p.getProjectID());
		jgen.writeStringField("field", Keys.Project.MEMBERS);
		jgen.writeArrayFieldStart("value");
		for (User member : p.getMembers()) {
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
		updateHelper(payloadTitle);
		updateHelper(payloadMeetings);
		updateHelper(payloadNotes);
		System.out.println(updateHelper(payloadMembers));
		
	}
	
	protected static String updateHelper(String jsonPayload) throws IOException {
		// Server URL setup
		String _url = getBaseUri().build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.PUT);
		addRequestHeader(conn, true);

		sendPostPayload(conn, jsonPayload);
		String response = getServerResponse(conn);
		conn.disconnect();
		return response;
	}
	

	
	public static void deleteProject(Project p) throws IOException{

		// Server URL setup
		String _url = getBaseUri().appendPath(p.getProjectID()).build().toString();

		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("DELETE");
		addRequestHeader(conn, false);

		// Get server response
		conn.getResponseCode();
		getServerResponse(conn);

	}
	
	public static Project parseProject(JsonNode projectNode, Project p) throws IOException {
		String projectID = projectNode.get(Keys.Project.ID).asText();
		List<Meeting> meetinglist = new ArrayList<Meeting>();
		List<Note> notelist = new ArrayList<Note>();
		List<User> userlist = new ArrayList<User>();
		p.setMeetings(meetinglist);
		p.setMembers(userlist);
		p.setNotes(notelist);
		if(projectID != null){
			p.setProjectID(projectID);
			p.setProjectTitle(projectNode.get(Keys.Project.TITLE).asText());
			JsonNode meetings = projectNode.get(Keys.Project.MEETINGS);
			if (meetings != null && meetings.isArray()) {
				for (final JsonNode meetingNode : meetings) {
					Meeting meeting = new Meeting();
					meeting.setID(meetingNode.get(Keys.Meeting.ID).asText());
					meeting = MeetingDatabaseAdapter.getMeetingInfo(meeting.getID());
					p.addMeeting(meeting);
				}
			}
			
			JsonNode notes = projectNode.get(Keys.Project.NOTES);
			if (notes != null && notes.isArray()) {
				for (final JsonNode noteNode : notes) {
					Note note = new Note();
					note.setID(noteNode.get(Keys.Note.ID).asText());
//					NotesDatabaseAdapter.get //TODO: lkjaslfdkjsad;lkfj
					p.addNote(note);
				}
			}
			
			JsonNode members = projectNode.get(Keys.Project.MEMBERS);
			if (members != null && members.isArray()) {
				for (final JsonNode memberNode : members) {
					User user = new User();
					user.setID(memberNode.get(Keys.User.ID).asText());
					user = UserDatabaseAdapter.getUserInfo(user.getID());
					p.addMember(user);
				}
			}
		}

		return p;
	}

}
