package com.android.meetingninja.user;

import java.io.IOException;

import objects.User;
import android.os.AsyncTask;
import android.util.Log;

import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.UserDatabaseAdapter;

public class UserInfoFetcher extends AsyncTask<String, Void, User> {

	private AsyncResponse<User> delegate;

	public UserInfoFetcher(AsyncResponse<User> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected User doInBackground(String... params) {
		User user = new User();

		try {
			// params = userID
			user = UserDatabaseAdapter.getUserInfo(params[0]);
		} catch (IOException e) {
			Log.e("UserInfoFetch", "Error: Unable to get userinfo");
			Log.e("USER_INFO_ERR", e.getLocalizedMessage());
		}

		return user;
	}

	@Override
	protected void onPostExecute(User user) {
		super.onPostExecute(user);
		delegate.processFinish(user);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}
