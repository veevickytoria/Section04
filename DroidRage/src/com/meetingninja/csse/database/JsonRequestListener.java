package com.meetingninja.csse.database;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.spothero.volley.JacksonRequestListener;

public abstract class JsonRequestListener extends
		JacksonRequestListener<JsonNode> {
	/**
	 * Called when the network call has returned and the result has been parsed
	 * 
	 * @param response
	 *            The parsed response, or null if an error occurred
	 * @param statusCode
	 *            The status code of the response
	 * @param error
	 *            The error that occurred, or null if successful
	 */
	public abstract void onResponse(JsonNode response, int statusCode,
			VolleyError error);
	
	@Override
	public JavaType getReturnType() {
		return SimpleType.construct(JsonNode.class);
	}

	/**
	 * Optional method that is called on the networking thread used to further
	 * process responses before delivering them to the UI thread.
	 */
	public Response<JsonNode> onParseResponseComplete(
			Response<JsonNode> response) {
		return response;
	}
}
