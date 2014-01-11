package com.android.meetingninja.user;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.android.meetingninja.database.UserDatabaseAdapter;
import com.fasterxml.jackson.core.JsonGenerationException;

import android.os.AsyncTask;
import android.util.Log;

public class UserUpdateTask extends AsyncTask<String, Void, Void> {

	private static final String TAG = UserUpdateTask.class.getSimpleName();
	private Map<String, String> key_values = new LinkedHashMap<String, String>();
	
	public UserUpdateTask(Map<String, String> values) {
		this.key_values  = values;
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
