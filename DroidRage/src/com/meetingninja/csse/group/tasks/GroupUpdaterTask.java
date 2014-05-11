package com.meetingninja.csse.group.tasks;

import java.io.IOException;

import objects.Group;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.GroupDatabaseAdapter;

public class GroupUpdaterTask implements AsyncResponse<Group> {
	private GroupUpdateTask updater;

	public GroupUpdaterTask() {
		this.updater = new GroupUpdateTask(this);
	}

	public void updateGroup(Group group) {
		this.updater.execute(group);
	}

	@Override
	public void processFinish(Group result) {
		// TODO Auto-generated method stub

	}

}

class GroupUpdateTask extends AsyncTask<Group, Void, Group> {

	private AsyncResponse<Group> delegate;

	public GroupUpdateTask(AsyncResponse<Group> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Group doInBackground(Group... params) {
		Group g = null;
		try {
			g = GroupDatabaseAdapter.updateGroup(params[0]);
		} catch (IOException e) {
			Log.e("TaskUpdate", "Error: Unable to update task info");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return g;
	}

	@Override
	protected void onPostExecute(Group g) {
		super.onPostExecute(g);
		delegate.processFinish(g);
	}

}
