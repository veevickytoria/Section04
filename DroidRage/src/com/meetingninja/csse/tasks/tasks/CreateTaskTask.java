package com.meetingninja.csse.tasks.tasks;

import java.io.IOException;

import objects.Task;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.TaskDatabaseAdapter;
import com.meetingninja.csse.tasks.TasksFragment;

public class CreateTaskTask implements AsyncResponse<Void> {

	private TasksFragment frag;
	private TaskCreator creator;

	public CreateTaskTask(TasksFragment frag) {
		this.frag = frag;
	}

	public void createTask(Task task) {
		this.creator = new TaskCreator(this);
		this.creator.execute(task);
	}

	@Override
	public void processFinish(Void result) {
		this.frag.refreshTasks();
	}

	private class TaskCreator extends AsyncTask<Task, Void, Void> {

		private AsyncResponse<Void> delegate;

		public TaskCreator(AsyncResponse<Void> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected Void doInBackground(Task... params) {
			try {
				TaskDatabaseAdapter.createTask(params[0]);
			} catch (IOException e) {
				Log.e("TaskCreator", "Error: Unable to create task");
				Log.e("TASKS_ERR", e.getLocalizedMessage());
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
