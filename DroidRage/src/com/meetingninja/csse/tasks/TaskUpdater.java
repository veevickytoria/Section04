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
