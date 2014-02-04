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

import objects.Task;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.extras.JsonUtils;

public class TaskDatabaseAdapter extends BaseDatabaseAdapter {
	private static final String TAG = TaskDatabaseAdapter.class.getSimpleName();

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
		conn.setRequestMethod(IRequest.GET);
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

	public static Task createTask(Task t) throws IOException {
		String _url = getBaseUri().build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.POST);
		addRequestHeader(conn, false);
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Title
		jgen.writeStartObject();
		jgen.writeStringField(Keys.Task.TITLE, t.getTitle());
		jgen.writeBooleanField(Keys.Task.COMPLETED, t.getIsCompleted());
		jgen.writeStringField(Keys.Task.DESC, t.getDescription());
		jgen.writeNumberField(Keys.Task.DEADLINE, t.getEndTimeInMillis());
		jgen.writeStringField(Keys.Task.DATE_CREATED, t.getDateCreated());
		jgen.writeStringField(Keys.Task.DATE_ASSIGNED, t.getDateAssigned());
		jgen.writeStringField(Keys.Task.CRITERIA, t.getCompletionCriteria());
		jgen.writeStringField(Keys.Task.ASSIGNED_TO, t.getAssignedTo());
		jgen.writeStringField(Keys.Task.ASSIGNED_FROM, t.getAssignedFrom());
		jgen.writeStringField(Keys.Task.CREATED_BY, t.getCreatedBy());
		jgen.writeEndObject();
		jgen.close();

		String payload = json.toString("UTF8");
		ps.close();
		// Get server response
		sendPostPayload(conn, payload);
		String response = getServerResponse(conn);
		Map<String, String> responseMap = new HashMap<String, String>();
		if (responseMap.containsKey(Keys.Task.ID)) {
			t.setID(responseMap.get(Keys.Task.ID));
		}
		return t;
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

		boolean result = false;
		JsonNode tree = MAPPER.readTree(response);
		if (!response.isEmpty()) {
			if (!tree.has(Keys.DELETED)) {
				result = true;
			} else {
				logError(TAG, tree);
			}
		}

		conn.disconnect();
		return result;

	}

	public static Task editTask(Task task) throws IOException {
		String titlePayload = getEditPayload(task.getID(), Keys.Task.TITLE,
				task.getTitle());
		String descPayload = getEditPayload(task.getID(), Keys.Task.DESC,
				task.getDescription());
		String isCompPayload = getEditPayload(task.getID(),
				Keys.Task.COMPLETED, Boolean.toString(task.getIsCompleted()));
		String deadlinePayload = getEditPayload(task.getID(),
				Keys.Task.DEADLINE, task.getEndTime());
		String compCritPayload = getEditPayload(task.getID(),
				Keys.Task.CRITERIA, task.getCompletionCriteria());
		String assignedToPayload = getEditPayload(task.getID(),
				Keys.Task.ASSIGNED_TO, task.getAssignedTo());
		// Get server response
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
		jgen.writeStringField(Keys.Task.ID, taskID);
		jgen.writeStringField("field", field);
		jgen.writeStringField("value", value);
		jgen.writeEndObject();
		jgen.close();
		String payload = json.toString("UTF8");
		ps.close();
		return payload;
	}

	public static void parseTask(JsonNode node, Task t) {
		if (node.hasNonNull(Keys.Task.ID)) {
			parseTaskNoCheck(node, t);

		} else {
			Log.w(TAG, "Parsed null");
		}
	}

	public static void parseTaskNoCheck(JsonNode node, Task t) {
		// start parsing a task

		t.setDescription(node.hasNonNull(Keys.Task.DESC) ? node.get(
				Keys.Task.DESC).asText() : "");
		t.setTitle(node.hasNonNull(Keys.Task.TITLE) ? node.get(Keys.Task.TITLE)
				.asText() : "");
		t.setEndTime(node.hasNonNull(Keys.Task.DEADLINE) ? node.get(
				Keys.Task.DEADLINE).asText() : "");
		t.setDateCreated(node.hasNonNull(Keys.Task.DATE_CREATED) ? node.get(
				Keys.Task.DATE_CREATED).asText() : "");
		t.setDateAssigned(node.hasNonNull(Keys.Task.DATE_ASSIGNED) ? node.get(
				Keys.Task.DATE_ASSIGNED).asText() : "");
		t.setCompletionCriteria(node.hasNonNull(Keys.Task.CRITERIA) ? node.get(
				Keys.Task.CRITERIA).asText() : "");
		t.setAssignedTo(node.hasNonNull(Keys.Task.ASSIGNED_TO) ? node.get(
				Keys.Task.ASSIGNED_TO).asText() : "");
		t.setAssignedFrom(node.hasNonNull(Keys.Task.ASSIGNED_FROM) ? node.get(
				Keys.Task.ASSIGNED_FROM).asText() : "");
		t.setCreatedBy(node.hasNonNull(Keys.Task.CREATED_BY) ? node.get(
				Keys.Task.CREATED_BY).asText() : "");
		t.setIsCompleted(node.hasNonNull(Keys.Task.COMPLETED) ? node.get(
				Keys.Task.COMPLETED).asBoolean() : false);

	}

	public static Task parseTasks(JsonNode node) {
		Task t = new Task(); // start parsing a task
		if (node.hasNonNull(Keys._ID)) {
			String id = node.get(Keys._ID).asText();
			t.setID(id);
			t.setTitle(JsonUtils.getJSONValue(node, Keys.Task.TITLE));
			t.setType(JsonUtils.getJSONValue(node, Keys.TYPE));
		} else {
			Log.w(TAG, "Parsed null task");
			return null;
		}
		return t;

	}
}
