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
import java.util.ArrayList;
import java.util.List;
import objects.Contact;
import objects.User;
import android.net.Uri;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.SessionManager;

public class ContactDatabaseAdapter extends BaseDatabaseAdapter {

	private static final String TAG = ContactDatabaseAdapter.class.getSimpleName();

	public static String getBaseUrl() {
		return BASE_URL + "Contact";
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	public static List<Contact> getContacts(String userID) throws IOException {
		// Server URL setup
		String _url = getBaseUri().appendPath(userID).build().toString();
		// Establish connection
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// add request header

		conn.setRequestMethod(IRequest.GET);
		addRequestHeader(conn, false);
		// Get server response
		int responseCode = conn.getResponseCode();
		String response = getServerResponse(conn);
		List<Contact> contacts = new ArrayList<Contact>();
		List<String> contactIds = new ArrayList<String>();
		List<String> relationIds = new ArrayList<String>();
		final JsonNode contactsArray = MAPPER.readTree(response).get(
				Keys.User.CONTACTS);
		if (contactsArray.isArray()) {
			for (final JsonNode userNode : contactsArray) {
				relationIds.add(userNode.get(Keys.User.RELATIONID).asText());
				contactIds.add(userNode.get(Keys.User.CONTACTID).asText());
			}
		}

		conn.disconnect();
		for (int i = 0; i < contactIds.size(); i++) {
			
			User contact = UserDatabaseAdapter.getUserInfo(contactIds.get(i));
			if (contact != null) {
				Contact oneContact = new Contact(contact, relationIds.get(i));
				contacts.add(oneContact);
			}
		}
		return contacts;
	}

	public static List<Contact> addContact(String contactUserID)
			throws IOException {

		String _url = getBaseUri().build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(IRequest.PUT);
		addRequestHeader(conn, false);
		SessionManager.getInstance();
		String userID = SessionManager.getUserID();
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Title
		jgen.writeStartObject();
		jgen.writeStringField(Keys.User.ID, userID);
		jgen.writeArrayFieldStart(Keys.User.CONTACTS);
		jgen.writeStartObject();
		jgen.writeStringField(Keys.User.CONTACTID, contactUserID);
		jgen.writeEndObject();
		jgen.writeEndArray();
		jgen.writeEndObject();
		jgen.close();
		String payload = json.toString("UTF8");
		ps.close();

		sendPostPayload(conn, payload);
		String response = getServerResponse(conn);

		// TODO: put add useful check here
		// User userContact=null;
		// String relationID=null;
		// String result = new String();
		// if (!response.isEmpty()) {
		// JsonNode contactNode = MAPPER.readTree(response);
		// if (!contactNode.has(Keys.User.ID)) {
		// result = "invalid";
		// } else {
		// result = contactNode.get(Keys.User.ID).asText();
		// userContact = getUserInfo(result);
		// relationID = contactNode.get(Keys.User.RELATIONID).asText();
		// }
		// }

		// if (!result.equalsIgnoreCase("invalid"))
		// g.setID(result);
		conn.disconnect();

		// Contact contact = new Contact(userContact,relationID);
		List<Contact> contacts = new ArrayList<Contact>();
		contacts = getContacts(userID);
		return contacts;
	}

	public static List<Contact> deleteContact(String relationID) throws IOException {

		String _url = getBaseUri().appendPath("Relations")
				.appendPath(relationID).build().toString();
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
		SessionManager.getInstance();
		List<Contact> contacts = getContacts(SessionManager.getUserID());
		return contacts;
	}
}
