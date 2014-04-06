package com.meetingninja.csse.group;

import java.io.IOException;

import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.GroupDatabaseAdapter;

public class GroupDeleteTask implements AsyncResponse<Boolean> {
	private AsyncGroupDeleteTask deleter; 
	
	public GroupDeleteTask() {
		this.deleter = new AsyncGroupDeleteTask();
	}

	public void deleteGroup(String groupID) {
		this.deleter.execute(groupID);
	}

	@Override
	public void processFinish(Boolean result) {
		// TODO Auto-generated method stub
		
	}

}

class AsyncGroupDeleteTask extends AsyncTask<String, Void, Boolean> {
//	private AsyncResponse<Group> delegate;

	public AsyncGroupDeleteTask() {
//		this.delegate = delegate;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		boolean success=false;
		try {
			success = GroupDatabaseAdapter.deleteGroup(params[0]);
		} catch (IOException e) {
			Log.e("TaskUpdate", "Error: Unable to delete Group");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return success;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
//		delegate.processFinish(g);
	}

}
