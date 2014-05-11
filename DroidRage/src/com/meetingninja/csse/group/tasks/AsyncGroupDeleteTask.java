package com.meetingninja.csse.group.tasks;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.database.GroupDatabaseAdapter;
import com.meetingninja.csse.extras.NinjaToastUtil;

public class AsyncGroupDeleteTask extends AsyncTask<String, Void, Boolean> {
	// private AsyncResponse<Group> delegate;

	public AsyncGroupDeleteTask() {
		// this.delegate = delegate;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		boolean success = false;
		try {
			success = GroupDatabaseAdapter.deleteGroup(params[0]);
		} catch (IOException e) {
			Log.e("TaskUpdate", "Error: Unable to delete Group");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return success;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Context app = ApplicationController.getInstance()
				.getApplicationContext();
		if (result) {
			NinjaToastUtil.show(app, "Group deleted");
		}
	}
}