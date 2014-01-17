package com.android.meetingninja.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Task;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.TaskDatabaseAdapter;

import android.os.AsyncTask;
import android.util.Log;

public class TaskListFetcherTask extends AsyncTask<String, Void, List<Task>> {
	
	private AsyncResponse<List<Task>> delegate;
	
	public TaskListFetcherTask(AsyncResponse<List<Task>> delegate){
		this.delegate = delegate;
	}
	@Override
	protected List<Task> doInBackground(String... params) {
		List<Task> dbTasks = new ArrayList<Task>();
		
		try{
			dbTasks = TaskDatabaseAdapter.getTasks(params[0]);
		} catch(IOException e){
			Log.e("TaskFetch", "Error: Unable to get tasks");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return dbTasks;
	}
	
	@Override
	protected void onPostExecute(List<Task> list){
		super.onPostExecute(list);
		delegate.processFinish(list);
	}
	

}
