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
import java.util.ArrayList;
import java.util.List;

import objects.Task;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class GetTaskListTask extends AsyncTask<String, Void, List<Task>> {

	private AsyncResponse<List<Task>> delegate;

	public GetTaskListTask(AsyncResponse<List<Task>> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected List<Task> doInBackground(String... params) {
		List<Task> dbTasks = new ArrayList<Task>();

		try {
			dbTasks = UserDatabaseAdapter.getTasks(params[0]);
		} catch (IOException e) {
			Log.e("TaskFetch", "Error: Unable to get tasks");
			Log.e("TASKS_ERR", e.getLocalizedMessage());
		}
		return dbTasks;
	}

	@Override
	protected void onPostExecute(List<Task> list) {
		super.onPostExecute(list);
		delegate.processFinish(list);
	}

}
