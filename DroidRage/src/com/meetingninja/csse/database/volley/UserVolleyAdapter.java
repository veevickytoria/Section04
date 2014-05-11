package com.meetingninja.csse.database.volley;

import java.util.List;

import objects.User;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class UserVolleyAdapter extends UserDatabaseAdapter {
	protected static final String TAG = null;

	public static void fetchUserInfo(final String userID,
			final AsyncResponse<User> delegate) {
		String _url = getBaseUri().appendPath(userID).build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							User retUser = parseUser(response);
							if (retUser != null) {
								retUser.setID(userID);
								delegate.processFinish(retUser);
							}
						} else {
							error.printStackTrace();
						}

					}
				});

		addToRequestQueue(req);
	}

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
							VolleyLog.e("Error: ", error.getMessage());
					}
				});

		// add the request object to the queue to be executed
		addToRequestQueue(req);

	}

//	public static void deleteUser(String userID,
//			final AsyncResponse<Boolean> delegate) {
//		String url = UserDatabaseAdapter.getBaseUri().appendPath(userID)
//				.build().toString();
//
//		JsonNodeRequest del_req = new JsonNodeRequest(Request.Method.DELETE,
//				url, null, new JsonRequestListener() {
//
//					@Override
//					public void onResponse(JsonNode response, int statusCode,
//							VolleyError error) {
//						if (response != null) {
//							delegate.processFinish(response.get(Keys.DELETED)
//									.asBoolean(false));
//						} else {
//							error.printStackTrace();
//						}
//
//					}
//				});
//		addToRequestQueue(del_req);
//	}

}
