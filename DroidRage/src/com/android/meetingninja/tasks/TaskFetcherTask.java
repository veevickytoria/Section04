package com.android.meetingninja.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.Task;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.TaskDatabaseAdapter;

import android.os.AsyncTask;
import android.util.Log;

public class TaskFetcherTask extends AsyncTask<String, Void, List<Task>> {
	
	private AsyncResponse<List<Task>> delegate;
	
	public TaskFetcherTask(AsyncResponse<List<Task>> delegate){
		this.delegate = delegate;
	}
	@Override
	protected List<Task> doInBackground(String... params) {
		List<Task> dbTasks = new ArrayList<Task>();
		
		try{
			dbTasks = TaskDatabaseAdapter.getTask(params[0]);
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
