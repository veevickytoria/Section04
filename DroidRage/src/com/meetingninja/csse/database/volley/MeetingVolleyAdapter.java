package com.meetingninja.csse.database.volley;

import objects.Meeting;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.MeetingDatabaseAdapter;

public class MeetingVolleyAdapter extends MeetingDatabaseAdapter {

	public static void fetchMeetingInfo(final String meetingID,
			final AsyncResponse<Meeting> delegate) {
		String _url = getBaseUri().appendPath(meetingID).build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new JsonRequestListener() {

					@Override
					public void onResponse(JsonNode response, int statusCode,
							VolleyError error) {
						if (response != null) {
							Meeting ret = parseMeeting(response);
							ret.setID(meetingID);
							delegate.processFinish(ret);
						} else {
							VolleyLog.e("Error:%n %s",
									error.getLocalizedMessage());
						}

					}

				});

		// add the request object to the queue to be executed
		addToRequestQueue(req);
	}

	public static void deleteMeeting(String meetingID) {
		String url = MeetingDatabaseAdapter.getBaseUri().appendPath(meetingID)
				.build().toString();
		StringRequest dr = new StringRequest(Request.Method.DELETE, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error:%n %s", error);

					}
				});

		ApplicationController.getInstance().addToRequestQueue(dr);
	}
}
