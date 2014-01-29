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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	protected static void addRequestHeader(URLConnection connection,
			boolean isPost) {
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept", ACCEPT_TYPE);
		connection.setDoOutput(isPost);
		if (isPost) {
			connection.setRequestProperty("Content-Type", CONTENT_TYPE);
		}
	}

	protected static int sendPostPayload(URLConnection connection,
			String payload) throws IOException {
		Log.d(IRequest.POST, "[URL] " + connection.getURL().toString());
		logPrint(payload);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();
		return ((HttpURLConnection) connection).getResponseCode();
	}

	protected static String getServerResponse(URLConnection connection)
			throws IOException {
		// Read server response
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// return page contents
		return response.toString();
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

		int responseCode = sendPostPayload(conn, jsonPayload);
		String response = getServerResponse(conn);
		conn.disconnect();
		return response;
	}

	protected static void logPrint(String payload) throws IOException {
		Log.i("JSON", payload);
	}

	protected static void logError(final String tag, final JsonNode errorNode) {
		Log.e(tag, String.format("ErrorID: [%s] %s",
				errorNode.get(Keys.ERROR_ID).asText(),
				errorNode.get(Keys.ERROR_MESSAGE).asText()));
	}
}
