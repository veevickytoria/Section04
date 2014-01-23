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
package com.meetingninja.csse.user;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class UserUpdateTask extends AsyncTask<String, Void, Void> {

	private static final String TAG = UserUpdateTask.class.getSimpleName();
	private Map<String, String> key_values = new LinkedHashMap<String, String>();

	public UserUpdateTask(Map<String, String> values) {
		this.key_values = values;
	}

	@Override
	protected Void doInBackground(String... params) {
		try {
			UserDatabaseAdapter.update(params[0], key_values);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getLocalizedMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getLocalizedMessage());
		}
		return null;
	}

}
