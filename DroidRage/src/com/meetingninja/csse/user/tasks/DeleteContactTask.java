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
import java.util.ArrayList;
import java.util.List;

import objects.Contact;
import objects.Meeting;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.ContactDatabaseAdapter;
import com.meetingninja.csse.user.ContactsFragment;

public class DeleteContactTask extends AsyncTask<String, Void, Boolean> {
	private AsyncResponse<Boolean> delegate;

	public DeleteContactTask(AsyncResponse<Boolean> delegate) {
		this.delegate = delegate;
	}
	@Override
	protected Boolean doInBackground(String... params) {
		Boolean success=false;
		try {
			success = ContactDatabaseAdapter.deleteContact(params[0]);
		} catch (IOException e) {
			Log.e("ContactDeleter", "Error: Unable delete contact");
			Log.e("ContactDeleter", e.getLocalizedMessage());
		}
		return success;
	}
	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
	}
}
