package com.meetingninja.csse.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Group;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class GroupFetcherTask extends AsyncTask<String, Void, List<Group>> {
	private AsyncResponse<List<Group>> delegate;

	public GroupFetcherTask(AsyncResponse<List<Group>> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected List<Group> doInBackground(String... params) {
		List<Group> dbGroups = new ArrayList<Group>();

		try {
			dbGroups = UserDatabaseAdapter.getGroups(params[0]);
		} catch (IOException e) {
			Log.e("GroupFetch", "Error: Unable to get groups");
			Log.e("GROUPS_ERR", e.getLocalizedMessage());
		}

		return dbGroups;
	}

	@Override
	protected void onPostExecute(List<Group> list) {
		super.onPostExecute(list);
		delegate.processFinish(list);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
