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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import android.net.Uri;
import android.net.Uri.Builder;

import com.android.meetingninja.ApplicationController;
import com.android.volley.RequestQueue;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractDatabaseAdapter {

	protected final static String BASE_URL = "http://csse371-04.csse.rose-hulman.edu/";
	protected final static String USER_AGENT = "Mozilla/5.0";
	protected final static String CONTENT_TYPE = "application/json";
	protected final static String ACCEPT_TYPE = "application/json";
	protected final static JsonFactory JFACTORY = new JsonFactory();
	protected final static ObjectMapper MAPPER = new ObjectMapper(JFACTORY);
	
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

}
