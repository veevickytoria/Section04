package com.android.meetingninja.tasks;

import java.io.IOException;

import objects.Task;

import android.os.AsyncTask;
import android.util.Log;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.TaskDatabaseAdapter;

public class TaskUpdater implements AsyncResponse<Task> {
	private TaskUpdateTask updater;
	private Task task;

	public TaskUpdater() {
		this.updater = new TaskUpdateTask(this);
	}

	public void updateTask(Task task) {
		this.updater.execute(task);
	}

	@Override
	public void processFinish(Task result) {
		// TODO Auto-generated method stub

	}

}

class TaskUpdateTask extends AsyncTask<Task, Void, Task> {

	private AsyncResponse<Task> delegate;

	public TaskUpdateTask(AsyncResponse<Task> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Task doInBackground(Task... params) {
		Task t = null;
		try {
			t = TaskDatabaseAdapter.editTask(params[0]);
		} catch (IOException e) {
			Log.e("TaskUpdate", "Error: Unable to update task info");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return t;
	}

	@Override
	protected void onPostExecute(Task t) {
		super.onPostExecute(t);
		delegate.processFinish(t);
	}

}