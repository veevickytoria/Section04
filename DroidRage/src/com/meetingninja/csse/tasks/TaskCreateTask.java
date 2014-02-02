package com.meetingninja.csse.tasks;

import java.io.IOException;

import objects.Task;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.TaskDatabaseAdapter;

public class TaskCreateTask implements AsyncResponse<Void>{
	
	private TasksFragment frag;
	private TaskCreator creator;
	
	public TaskCreateTask(TasksFragment frag){
		this.frag = frag;
		this.creator = new TaskCreator(this);
	}
	
	public void createTask(Task task){
		this.creator.execute(task);
	}

	@Override
	public void processFinish(Void result) {
		this.frag.refreshTasks();
	}
	
	private class TaskCreator extends AsyncTask<Task, Void, Void>{
		
		private AsyncResponse<Void> delegate;
		
		public TaskCreator(AsyncResponse<Void> delegate){
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


