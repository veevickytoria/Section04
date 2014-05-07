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
package com.meetingninja.csse.user.tasks;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import objects.User;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class UpdateUserTask extends AsyncTask<User, Void, User> {

	private static final String TAG = UpdateUserTask.class.getSimpleName();

	@Override
	protected User doInBackground(User... params) {
		User u = new User();
		try {
			u=UserDatabaseAdapter.update(params[0]);
		} catch (JsonGenerationException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage());
		} catch (InterruptedException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
		return u;
	}
	@Override
	protected void onPostExecute(User user) {
		super.onPostExecute(user);
	}

}
