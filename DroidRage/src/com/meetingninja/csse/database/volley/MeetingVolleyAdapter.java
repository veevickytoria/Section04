package com.meetingninja.csse.database.volley;

import objects.Meeting;

import com.android.volley.VolleyError;
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
							delegate.processFinish(parseMeeting(response,
									meetingID));
						} else {
							error.printStackTrace();
						}

					}

				});

		// add the request object to the queue to be executed
		addToRequestQueue(req);
	}
}
