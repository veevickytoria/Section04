package com.android.meetingninja.tasks;

import java.io.IOException;

import objects.Task;
import android.os.AsyncTask;
import android.util.Log;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.TaskDatabaseAdapter;

public class TaskDeleter implements AsyncResponse<Boolean>{
	
	private TaskDeleterTask deleter = null;
	public TaskDeleter(){
		this.deleter = new TaskDeleterTask(this);
	}
	
	public void deleteTask(String taskID){
		this.deleter.execute(taskID);
	}
	@Override
	public void processFinish(Boolean result) {
		if(!result){
			//do something?
		}
		
	}

}



class TaskDeleterTask extends AsyncTask<String, Void, Boolean>{
	private AsyncResponse<Boolean> delegate;
	
	public TaskDeleterTask(AsyncResponse<Boolean> delegate){
		this.delegate = delegate;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try{
			return TaskDatabaseAdapter.deleteTask(params[0]);
		}catch(IOException e){
			Log.e("TaskDelete", "Error: Unable to delete task");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean b){
		super.onPostExecute(b);
		delegate.processFinish(b);
	}
	
}

