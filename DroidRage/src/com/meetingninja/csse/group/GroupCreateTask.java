package com.meetingninja.csse.group;

import java.io.IOException;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.GroupDatabaseAdapter;
import com.meetingninja.csse.database.TaskDatabaseAdapter;

import objects.Group;
import android.os.AsyncTask;
import android.util.Log;

public class GroupCreateTask implements AsyncResponse<Void> {

	private GroupsFragment frag;
	private GroupCreator creator;

	public GroupCreateTask(GroupsFragment frag) {
		this.frag = frag;
		this.creator = new GroupCreator(this);
	}

	public void createGroup(Group group) {
		this.creator.execute(group);
	}

	@Override
	public void processFinish(Void result) {
		this.frag.notifyAdapter();
	}

	private class GroupCreator extends AsyncTask<Group, Void, Void> {

		private AsyncResponse<Void> delegate;

		public GroupCreator(AsyncResponse<Void> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected Void doInBackground(Group... params) {
			try {
				GroupDatabaseAdapter.createGroup(params[0]);
			} catch (IOException e) {
				Log.e("GroupCreator", "Error: Unable to create group");
				Log.e("GROUPS_ERR", e.getLocalizedMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			delegate.processFinish(v);
		}

	}
}
