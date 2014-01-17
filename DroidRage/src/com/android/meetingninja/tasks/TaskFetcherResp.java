package com.android.meetingninja.tasks;

import java.io.IOException;
import java.util.List;

import com.android.meetingninja.ViewTaskActivity;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.TaskDatabaseAdapter;

import objects.Task;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;


public class TaskFetcherResp implements AsyncResponse<Void>{
	
	private Fragment frag;
	private TaskFetcherTask fetcher;
	private Task task;
	//private Task task;
	
	public TaskFetcherResp(Fragment frag){
		this.frag = frag;
		this.fetcher = new TaskFetcherTask(this);
		//this.task = task;
	}
	
	public void openTask(Task task){
		System.out.println("task id in fetcher:   " + task.getID());
		this.task = task;
		this.fetcher.execute(task);
	}
	@Override
	public void processFinish(Void result) {
		Intent viewTask = new Intent(this.frag.getActivity(),
				ViewTaskActivity.class);
		viewTask.putExtra("task", this.task);
		this.frag.startActivity(viewTask);
		
	}
	
}



class TaskFetcherTask extends AsyncTask<Task, Void, Void>{
	private AsyncResponse<Void> delegate;
	
	public TaskFetcherTask(AsyncResponse<Void> delegate){
		this.delegate = delegate;
	}

	@Override
	protected Void doInBackground(Task... params) {
		try{
			TaskDatabaseAdapter.getTask(params[0]);
		}catch(IOException e){
			Log.e("TaskFetch", "Error: Unable to get task info");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void v){
		super.onPostExecute(v);
		delegate.processFinish(v);
	}
	
}


