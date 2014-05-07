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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.extras.JsonUtils;

public abstract class BaseDatabaseAdapter {

	protected final static String BASE_URL = "http://csse371-04.csse.rose-hulman.edu/";
	protected final static String USER_AGENT = "Mozilla/5.0";
	protected final static String CONTENT_TYPE = "application/json";
	protected final static String ACCEPT_TYPE = "application/json";
	protected final static ObjectMapper MAPPER = JsonUtils.getObjectMapper();
	protected final static JsonFactory JFACTORY = JsonUtils.getJsonFactory();

	protected interface IRequest {
		final String GET = "GET";
		final String POST = "POST";
		final String PUT = "PUT";
		final String DELETE = "DELETE";
	}

	public static String getBaseUrl() {
		return BASE_URL;
	}

	public static Uri.Builder getBaseUri() {
		return Uri.parse(getBaseUrl()).buildUpon();
	}

	protected static void addRequestHeader(URLConnection connection,boolean isPost) {
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept", ACCEPT_TYPE);
		connection.setDoOutput(isPost);
		if (isPost) {
			connection.setRequestProperty("Content-Type", CONTENT_TYPE);
		}
	}

	protected static int sendPostPayload(URLConnection connection,String payload) throws IOException {
		Log.d(IRequest.POST, "[URL] " + connection.getURL().toString());
		logPrint(payload);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();
		return ((HttpURLConnection) connection).getResponseCode();
	}

	protected static String getServerResponse(URLConnection connection)throws IOException {
		// Read server response
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// return page contents
		return response.toString();
	}

	protected static String updateHelper(String updateURL, String jsonPayload)throws IOException {
		URL url = new URL(updateURL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// add request header
		conn.setRequestMethod(IRequest.PUT);
		addRequestHeader(conn, true);

		String response = getServerResponse(conn);
		conn.disconnect();
		return response;
	}
	protected static String sendSingleEdit(String payload,String _url) throws IOException {
		URL url = new URL(_url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(IRequest.PUT);
		addRequestHeader(conn, false);
		sendPostPayload(conn, payload);
		return getServerResponse(conn);
	}
	protected static String getEditPayload(String objectID, String field,String value,String keyValue) throws IOException {
		ByteArrayOutputStream json = new ByteArrayOutputStream();
		// this type of print stream allows us to get a string easily
		PrintStream ps = new PrintStream(json);
		// Create a generator to build the JSON string
		JsonGenerator jgen = JFACTORY.createGenerator(ps, JsonEncoding.UTF8);
		// Build JSON Object for Title
		jgen.writeStartObject();
		jgen.writeStringField(keyValue, objectID);
		jgen.writeStringField("field", field);
		jgen.writeStringField("value", value);
		jgen.writeEndObject();
		jgen.close();
		String payload = json.toString("UTF8");
		ps.close();
		return payload;
	}

	protected static void addToRequestQueue(Request<?> req) {
		ApplicationController.getInstance().addToRequestQueue(req, "JSON");
	}

	protected static void logPrint(String payload) {
		Log.i("JSON", payload);
	}

	protected static void logError(final String tag, final JsonNode errorNode) {
		Log.e(tag, String.format("ErrorID: [%s] %s",
				errorNode.get(Keys.ERROR_ID).asText(),
				errorNode.get(Keys.ERROR_MESSAGE).asText()));
	}
}
