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

import objects.Task;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

public class TaskDatabaseAdapter extends BaseDatabaseAdapter {
	private static final String TAG = UserDatabaseAdapter.class.getSimpleName();

	protected final static String KEY_ID_LIST = "id";
	protected final static String KEY_ID = "taskID";
	protected final static String KEY_TITLE = "title";
	protected final static String KEY_DESC = "description";
	protected final static String KEY_DEADLINE = "deadline";
	protected final static String KEY_DATECREATED = "dateCreated";
	protected final static String KEY_DATEASSIGNED = "dateAssigned";
	protected final static String KEY_COMPCRIT = "completionCriteria";
	protected final static String KEY_ASSIGNEDTO = "assignedTo";
	protected final static String KEY_ASSIGNEDFROM = "assignedFrom";
	protected final static String KEY_CREATEDBY = "createdBy";
	protected final static String KEY_ISCOMPLEATED = "isCompleted";
	protected final static String KEY_TYPE = "type";

	public static String getBaseUrl() {
		return BASE_URL + "Task";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static void getTask(Task t) throws JsonParseException,
			JsonMappingException, IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(t.getID()).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod("GET");
		addRequestHeader(conn, false);

		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		// Initialize ObjectMapper
		// List<Task> taskList = new ArrayList<Task>();
		final JsonNode taskNode = MAPPER.readTree(response);

		// if(taskArray.isArray()){
		// for(final JsonNode taskNode : taskArray){
		parseTask(taskNode, t);

		// if(t!=null){
		// taskList.add(t);
		// }
		// }
		// }
		conn.disconnect();
		// return void;

	}

	public static Boolean deleteTask(String taskID) throws IOException {
		String _url = getBaseUri().appendPath(taskID).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.DELETE);
		addRequestHeader(conn, false);
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);

		return MAPPER.readTree(response).get("deleted").asBoolean();

	}

	public static Task editTask(Task task) throws IOException {
		String titlePayload = getEditPayload(task.getID(), KEY_TITLE,
				task.getTitle());
		String descPayload = getEditPayload(task.getID(), KEY_DESC,
				task.getDescription());
		String isCompPayload = getEditPayload(task.getID(), KEY_ISCOMPLEATED,
				Boolean.toString(task.getIsCompleted()));
		String deadlinePayload = getEditPayload(task.getID(), KEY_DEADLINE,
				task.getEndTime());
		String compCritPayload = getEditPayload(task.getID(), KEY_COMPCRIT,
				task.getCompletionCriteria());
		String assignedToPayload = getEditPayload(task.getID(), KEY_ASSIGNEDTO,
				task.getAssignedTo());
		// Get server response
		System.out.println(task.getCompletionCriteria());
		sendSingleEdit(titlePayload);
		sendSingleEdit(descPayload);
		sendSingleEdit(isCompPayload);
		sendSingleEdit(deadlinePayload);
		sendSingleEdit(compCritPayload);
		String response = sendSingleEdit(assignedToPayload);
		final JsonNode taskNode = MAPPER.readTree(response);
		parseTaskNoCheck(taskNode, task);
		return task;
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

	private static String getEditPayload(String taskID, String field,
			String value) throws IOException {
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Title
		jgen.writeStartObject();
		jgen.writeStringField("taskID", taskID);
		jgen.writeStringField("field", field);
		jgen.writeStringField("value", value);
		jgen.writeEndObject();
		jgen.close();
		String payload = json.toString("UTF8");
		ps.close();
		return payload;
	}

	public static void parseTask(JsonNode node, Task t) {
		if (node.hasNonNull(KEY_ID)) {
			parseTaskNoCheck(node, t);

		} else {
			Log.w(TAG, "Parsed null");
		}
	}

	public static void parseTaskNoCheck(JsonNode node, Task t) {
		// start parsing a task

		t.setDescription(node.hasNonNull(KEY_DESC) ? node.get(KEY_DESC)
				.asText() : "");
		t.setTitle(node.hasNonNull(KEY_TITLE) ? node.get(KEY_TITLE).asText()
				: "");
		t.setEndTime(node.hasNonNull(KEY_DEADLINE) ? node.get(KEY_DEADLINE)
				.asText() : "");
		t.setDateCreated(node.hasNonNull(KEY_DATECREATED) ? node.get(
				KEY_DATECREATED).asText() : "");
		t.setDateAssigned(node.hasNonNull(KEY_DATEASSIGNED) ? node.get(
				KEY_DATEASSIGNED).asText() : "");
		t.setCompletionCriteria(node.hasNonNull(KEY_COMPCRIT) ? node.get(
				KEY_COMPCRIT).asText() : "");
		t.setAssignedTo(node.hasNonNull(KEY_ASSIGNEDTO) ? node.get(
				KEY_ASSIGNEDTO).asText() : "");
		t.setAssignedFrom(node.hasNonNull(KEY_ASSIGNEDFROM) ? node.get(
				KEY_ASSIGNEDFROM).asText() : "");
		t.setCreatedBy(node.hasNonNull(KEY_CREATEDBY) ? node.get(KEY_CREATEDBY)
				.asText() : "");
		t.setIsCompleted(node.hasNonNull(KEY_ISCOMPLEATED) ? node.get(
				KEY_ISCOMPLEATED).asBoolean() : false);

	}

	public static Task parseTasks(JsonNode node) {
		Task t = new Task(); // start parsing a task
		if (node.hasNonNull(KEY_ID_LIST)) {
			String id = node.get(KEY_ID_LIST).asText();
			t.setID(id);
			t.setTitle(node.hasNonNull(KEY_TITLE) ? node.get(KEY_TITLE)
					.asText() : "");
			t.setType(node.hasNonNull(KEY_TYPE) ? node.get(KEY_TYPE).asText()
					: "");
		} else {
			Log.w(TAG, "Parsed null");
			return null;
		}
		return t;

	}

}
