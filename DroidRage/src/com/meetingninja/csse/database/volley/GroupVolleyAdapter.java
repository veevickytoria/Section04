package com.meetingninja.csse.database.volley;

import objects.Group;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.GroupDatabaseAdapter;

public class GroupVolleyAdapter extends GroupDatabaseAdapter {

	public static void fetchGroupInfo(final String groupID,	final AsyncResponse<Group> delegate) {
		String _url = getBaseUri().appendPath(groupID).build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,new JsonRequestListener() {

			@Override
			public void onResponse(JsonNode response, int statusCode,
					VolleyError error) {
				if (response != null) {
					Group retGroup = parseGroup(response, new Group());
					retGroup.setID(groupID);
					delegate.processFinish(retGroup);
				} else {
					error.printStackTrace();
				}

			}
		});

		addToRequestQueue(req);
	}

}
