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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonNodeRequest extends JsonRequest<JsonNode> {

	private ObjectMapper mMapper;

	public JsonNodeRequest(String url, JsonNode jsonRequest,
			Listener<JsonNode> listener, ErrorListener errorListener) {
		this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
				listener, errorListener);
	}

	public JsonNodeRequest(int method, String url, JsonNode jsonRequest,
			Listener<JsonNode> listener, ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest
				.toString(), listener, errorListener);
		mMapper = new ObjectMapper();

	}

	@Override
	protected Response<JsonNode> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(mMapper.readTree(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonProcessingException je) {
			return Response.error(new ParseError(je));
		} catch (IOException ioe) {
			return Response.error(new ParseError(ioe));
		}
	}
}
