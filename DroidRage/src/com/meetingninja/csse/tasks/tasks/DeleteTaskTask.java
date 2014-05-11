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
package com.meetingninja.csse.tasks.tasks;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.TaskDatabaseAdapter;
import com.meetingninja.csse.extras.NinjaToastUtil;

public class DeleteTaskTask implements AsyncResponse<Boolean> {

	private TaskDeleterTask deleter = null;

	public DeleteTaskTask() {
		this.deleter = new TaskDeleterTask(this);
	}

	public void deleteTask(String taskID) {
		this.deleter.execute(taskID);
	}

	@Override
	public void processFinish(Boolean result) {
		Context app = ApplicationController.getInstance().getApplicationContext();
		if (result) {
			NinjaToastUtil.show(app, "Task deleted");
		}

	}

}

class TaskDeleterTask extends AsyncTask<String, Void, Boolean> {
	private AsyncResponse<Boolean> delegate;

	public TaskDeleterTask(AsyncResponse<Boolean> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			return TaskDatabaseAdapter.deleteTask(params[0]);
		} catch (IOException e) {
			Log.e("TaskDelete", "Error: Unable to delete task");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean b) {
		super.onPostExecute(b);
		delegate.processFinish(b);
	}

}
