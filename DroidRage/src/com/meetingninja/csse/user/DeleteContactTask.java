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
import java.util.ArrayList;
import java.util.List;

import objects.User;
import android.os.AsyncTask;
import android.util.Log;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class DeleteContactTask implements AsyncResponse<Void> {

	private static final String TAG = DeleteContactTask.class.getSimpleName();
	private ContactDeleter deleter;
	
	public DeleteContactTask() {
		deleter = new ContactDeleter(this);
	}
	
	public void addContact(String relationID){
		this.deleter.execute(relationID);
	}

	@Override
	public void processFinish(Void result) {
	}
	
	
	private class ContactDeleter extends AsyncTask<String, Void, Void>{
		
		private AsyncResponse<Void> delegate;
		
		public ContactDeleter(AsyncResponse<Void> delegate){
			this.delegate = delegate;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				UserDatabaseAdapter.deleteContact(params[0]);
			} catch (IOException e) {
				Log.e("ContactAdder", "Error: Unable to add contact");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void v) {
			super.onPostExecute(v);
			delegate.processFinish(v);
		}
		
	}


//	@Override
//	protected void onCancelled() {
//		super.onCancelled();
//	}

}
