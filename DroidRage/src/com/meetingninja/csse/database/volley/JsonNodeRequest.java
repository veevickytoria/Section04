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
package com.meetingninja.csse.database.volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetingninja.csse.extras.JsonUtils;
import com.spothero.volley.JacksonRequestListener;

/**
 * Volley Jackson-JsonNode request based on SpotHero's volley-jackson-extension.
 * 
 * @see <a
 *      href="https://github.com/spothero/volley-jackson-extension/blob/master/Library/src/com/spothero/volley/JacksonRequest.java">volley-jackson-extension</a>
 * 
 * @author moorejm
 * 
 */
public class JsonNodeRequest extends Request<JsonNode> {

	private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds
	private static final ObjectMapper OBJECT_MAPPER = JsonUtils
			.getObjectMapper();

	private Map<String, String> mParams;
	private List<Integer> mAcceptedStatusCodes;
	private final JacksonRequestListener<JsonNode> mListener;

	public JsonNodeRequest(String url, JsonNode jsonPOST,
			JsonRequestListener listener) {
		this(jsonPOST == null ? Method.GET : Method.POST, url, jsonPOST,
				listener);
	}

	public JsonNodeRequest(int method, String url, JsonNode jsonPOST,
			JsonRequestListener listener) {
		this(DEFAULT_TIMEOUT, method, url, jsonPOST, listener);
	}

	public JsonNodeRequest(int timeout, int method, String url,
			JsonNode payload, JsonRequestListener listener) {
		super(method, url, null);

		setShouldCache(false);

		mListener = listener;

		mAcceptedStatusCodes = new ArrayList<Integer>();
		mAcceptedStatusCodes.add(HttpStatus.SC_OK);
		mAcceptedStatusCodes.add(HttpStatus.SC_NO_CONTENT);

		setRetryPolicy(new DefaultRetryPolicy(timeout, 1, 1));

		if (method == Method.POST || method == Method.PUT) {
			mParams = new HashMap<String, String>();
			mParams.put("payload", payload.toString());
		}
	}

	@Override
	protected Response<JsonNode> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(OBJECT_MAPPER.readTree(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			VolleyLog.e(e, "An error occurred while parsing network response:");
			return Response.error(new ParseError(e));
		}
	}

	/**
	 * Allows you to add additional status codes (besides 200 and 204) that will
	 * be parsed.
	 * 
	 * @param statusCodes
	 *            An array of additional status codes to parse network responses
	 *            for
	 */
	public void addAcceptedStatusCodes(int[] statusCodes) {
		for (int statusCode : statusCodes) {
			mAcceptedStatusCodes.add(statusCode);
		}
	}

	/**
	 * Gets all status codes that will be parsed as successful (Note: some
	 * {@link com.android.volley.toolbox.HttpStack} implementations, including
	 * the default, may not allow certain status codes to be parsed. To get
	 * around this limitation, use a custom HttpStack, such as the one provided
	 * with the excellent OkHttp library
	 * 
	 * @return A list of all status codes that will be counted as successful
	 */
	public List<Integer> getAcceptedStatusCodes() {
		return mAcceptedStatusCodes;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");

		if (getMethod() == Method.POST || getMethod() == Method.PUT) {
			headers.put("Content-Type",
					"application/x-www-form-urlencoded; charset=utf8");
		}

		return headers;
	}

	@Override
	public Map<String, String> getParams() {
		return mParams;
	}

	@Override
	protected void deliverResponse(JsonNode response) {
		mListener.onResponse(response, HttpStatus.SC_OK, null);
	}

	@Override
	public void deliverError(VolleyError error) {
		int statusCode;
		if (error != null && error.networkResponse != null) {
			statusCode = error.networkResponse.statusCode;
		} else {
			statusCode = 0;
		}

		mListener.onResponse(null, statusCode, error);
	}

}
