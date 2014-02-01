package com.meetingninja.csse.database.volley;

import java.util.ArrayList;
import java.util.List;

import objects.SerializableUser;
import objects.User;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class UserVolleyAdapter extends UserDatabaseAdapter {
	public static void fetchUserInfo(String userID,
			final AsyncResponse<User> delegate) {
		String _url = getBaseUri().appendPath(userID).build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							delegate.processFinish(parseUser(response));
						} else {
							error.printStackTrace();
						}

					}
				});

		addToRequestQueue(req);
	}

	// public static void fetchAllUsers(
	// final AsyncResponse<ArrayList<User>> delegate) {
	// String _url = getBaseUri().appendPath("Users").build().toString();
	//
	// JsonNodeRequest req = new JsonNodeRequest(_url, null,
	// new JsonRequestListener() {
	// @Override
	// public void onResponse(JsonNode response, int statusCode,
	// VolleyError error) {
	// if (response != null) {
	// // callback to UI thread
	// delegate.processFinish(parseUserList(response));
	// } else {
	// error.printStackTrace();
	// }
	// }
	// });
	// // add the request object to the queue to be executed
	// addToRequestQueue(req);
	//
	// }

	public static void fetchAllUsers(final AsyncResponse<List<User>> delegate) {
		String _url = getBaseUri().appendPath("Users").build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							delegate.processFinish(parseUserList(response));
						} else
							error.printStackTrace();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());

					}
				});

		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req, "JSON");

	}

	public static void deleteUser(String userID,
			final AsyncResponse<Boolean> delegate) {
		String url = UserDatabaseAdapter.getBaseUri().appendPath(userID)
				.build().toString();

		JsonNodeRequest del_req = new JsonNodeRequest(Request.Method.DELETE,
				url, null, new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							delegate.processFinish(response.get(Keys.DELETED)
									.asBoolean(false));
						} else {
							error.printStackTrace();
						}

					}
				});
		addToRequestQueue(del_req);

	}
}
