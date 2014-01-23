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

import objects.User;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;

public class RegisterActivity extends Activity implements AsyncResponse<User> {

	EditText nameText;
	EditText emailText;
	EditText passwordText;
	EditText confirmPasswordText;
	public static final String ARG_USERNAME = "user";

	AlertDialog.Builder builder;

	AlertDialog.Builder passCheck;
	private RegisterTask registerTask;
	private boolean mRegisterSuccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		// Show the Up button in the action bar.
		setupActionBar();

		nameText = ((EditText) findViewById(R.id.register_name));
		emailText = ((EditText) findViewById(R.id.register_email));
		passwordText = ((EditText) findViewById(R.id.register_password));
		confirmPasswordText = ((EditText) findViewById(R.id.register_confirm));

		// builder = new AlertDialog.Builder(RegisterActivity.this);
		//
		// builder.setMessage(
		// "The username "
		// + usernameText.getText().toString()
		// + " is already taken.\nPlease enter a different username.")
		// .setTitle("Username Already in Use");
		//
		// builder.setPositiveButton("OK", null);
		//
		// passCheck = new AlertDialog.Builder(RegisterActivity.this);
		//
		// passCheck
		// .setMessage(
		// "Your password and confirmation password were different.\nPlease re-enter password")
		// .setTitle("Password Mismatch");
		//
		// passCheck.setPositiveButton("OK", null);

		findViewById(R.id.register_action_button).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						tryRegister();
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			Intent upIntent = new Intent(this, LoginActivity.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is NOT part of this app's task, so create a new
				// task
				// when navigating up, with a synthesized back stack.
				TaskStackBuilder.create(this)
				// Add all of this activity's parents to the back stack
						.addNextIntent(upIntent)
						// Navigate up to the closest parent
						.startActivities();
			} else {
				// This activity is part of this app's task, so simply
				// navigate up to the logical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent upIntent = new Intent(this, LoginActivity.class);
		if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
			// This activity is NOT part of this app's task, so create a new
			// task
			// when navigating up, with a synthesized back stack.
			TaskStackBuilder.create(this)
			// Add all of this activity's parents to the back stack
					.addNextIntent(upIntent)
					// Navigate up to the closest parent
					.startActivities();
		} else {
			// This activity is part of this app's task, so simply
			// navigate up to the logical parent activity.
			NavUtils.navigateUpTo(this, upIntent);
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	private void tryRegister() {
		// Reset errors
		emailText.setError(null);
		passwordText.setError(null);
		confirmPasswordText.setError(null);

		// Store values
		String name = nameText.getText().toString().trim();
		String email = emailText.getText().toString().trim();
		String pass = passwordText.getText().toString().trim();
		String confPass = confirmPasswordText.getText().toString().trim();

		Log.d("Registering", name + " : " + email + " : " + pass + " : "
				+ confPass);

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(name)) {
			passwordText.setError(getString(R.string.error_field_required));
			focusView = nameText;
			cancel = true;
		}
		if (TextUtils.isEmpty(email)) {
			passwordText.setError(getString(R.string.error_field_required));
			focusView = emailText;
			cancel = true;
		}
		if (TextUtils.isEmpty(pass)) {
			passwordText.setError(getString(R.string.error_field_required));
			focusView = passwordText;
			cancel = true;
		}
		if (TextUtils.isEmpty(confPass)) {
			confirmPasswordText
					.setError(getString(R.string.error_field_required));
			focusView = confirmPasswordText;
			cancel = true;
		} else if (!confPass.equals(pass)) {
			Log.e("PASS_MISMATCH", "error");
			confirmPasswordText
					.setError(getString(R.string.error_mismatch_password));
			focusView = confirmPasswordText;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			registerTask = new RegisterTask(this);
			registerTask.execute(name, email, pass);
			// go to process finish
		}

	}

	@Override
	public void processFinish(User result) {
		mRegisterSuccess = (result != null);

		if (!mRegisterSuccess) {
			Toast.makeText(getApplicationContext(),
					"Registration Unsuccessful", Toast.LENGTH_LONG).show();
		} else {
			Intent goLogin = new Intent();
			goLogin.putExtra(Intent.EXTRA_EMAIL, result.getEmail());
			goLogin.putExtra("registerSuccess", mRegisterSuccess);
			setResult(RESULT_OK, goLogin);
			finish();
		}

	}

	private class RegisterTask extends AsyncTask<String, Void, User> {

		private AsyncResponse<User> delegate;

		public RegisterTask(AsyncResponse<User> del) {
			this.delegate = del;
		}

		@Override
		protected User doInBackground(String... params) {
			try {
				// name, email, password
				User registerMe = new User();
				registerMe.setDisplayName(params[0]);
				registerMe.setEmail(params[1]);
				return UserDatabaseAdapter.register(registerMe, params[2]);
			} catch (IOException e) {
				Log.e("DB Adapter", "Error: Register failed");
				Log.e("REGISTER_ERR", e.toString());
			} catch (Exception e) {
				Log.e("REGISTER_ERR", e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(User result) {
			super.onPostExecute(result);
			delegate.processFinish(result);
		}
	}

}
