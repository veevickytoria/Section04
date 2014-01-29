/*******************************************************************************
 * Copyright (C) 2014 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.meetingninja.csse.tasks;

import java.io.IOException;

import objects.Task;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.TaskDatabaseAdapter;

public class TaskFetcherResp implements AsyncResponse<Void> {

	private TasksFragment frag;
	private TaskFetcherTask fetcher;
	private Task task;

	// private Task task;

	public TaskFetcherResp(TasksFragment frag) {
		this.frag = frag;
		this.fetcher = new TaskFetcherTask(this);
		// this.task = task;
	}

	public void loadTask(Task task) {
		this.task = task;
		this.fetcher.execute(task);
	}

	@Override
	public void processFinish(Void result) {
		// Intent viewTask = new Intent(this.frag.getActivity(),
		// ViewTaskActivity.class);
		// viewTask.putExtra("task", this.task);
		// this.frag.startActivityForResult(viewTask, 6);// (viewTask);
		this.frag.notifyAdapter();
	}

}

class TaskFetcherTask extends AsyncTask<Task, Void, Void> {
	private AsyncResponse<Void> delegate;

	public TaskFetcherTask(AsyncResponse<Void> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Void doInBackground(Task... params) {
		try {
			TaskDatabaseAdapter.getTask(params[0]);
		} catch (IOException e) {
			Log.e("TaskFetch", "Error: Unable to get task info");
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
