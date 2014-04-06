package com.meetingninja.csse.database.volley;

import objects.Task;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.TaskDatabaseAdapter;

public class TaskVolleyAdapter extends TaskDatabaseAdapter {

	public static void getTaskInfo(final String taskID, final AsyncResponse<Task> delegate) {
		String _url = getBaseUri().appendPath(taskID).build().toString();
		
		JsonNodeRequest req = new JsonNodeRequest(_url, null, new JsonRequestListener() {
					@Override
					public void onResponse(JsonNode response, int statusCode, VolleyError error) {
						if (response != null) {
							Task t = new Task();
							t.setID(taskID);
							parseTask(response, t);
							delegate.processFinish(t);
						} else {
							error.printStackTrace();
						}

					}
				});

		addToRequestQueue(req);
	}
}
