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

import objects.User;
import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.MainActivity;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.extras.NinjaTextUtils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private static final String TAG = LoginActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		setupViews();

		// Set up the login form.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			boolean registerSuccess = extras.getBoolean("registerSuccess",
					false);
			if (registerSuccess) {
				Toast.makeText(LoginActivity.this, "Registration Successful",
						Toast.LENGTH_SHORT).show();
			}
			mEmail = extras.getString(Intent.EXTRA_EMAIL);
			mEmailView.setText(mEmail);
		}

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent register = new Intent(LoginActivity.this,
								RegisterActivity.class);
						if (!TextUtils.isEmpty(mEmail)) {
							register.putExtra(Intent.EXTRA_EMAIL, mEmail);
						}
						startActivityForResult(register, 0);
					}
				});
	}

	private void setupViews() {
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form_full);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); getMenuInflater().inflate(R.menu.login,
	 * menu); return true; }
	 */

	@Override
	public void onBackPressed() {
		if (mLoginStatusView.isShown()) {
			mAuthTask.cancel(true);
			showProgress(false);
		} else {
			this.finish();
			super.onBackPressed();
		}
	}

	@Override
	public void finish() {
		SessionManager.getInstance().clear();
		super.finish();
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString().trim();
		mPassword = mPasswordView.getText().toString().trim();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid username
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!NinjaTextUtils.isValidEmailAddress(mEmail)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		/*
		 * SessionManager session = new SessionManager(
		 * getApplicationContext()); session.clear();
		 * session.createLoginSession(mUsername); Intent main = new
		 * Intent(mLoginFormView.getContext(), MainActivity.class);
		 * main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * main.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		 *
		 * startActivityForResult(main, 0);
		 * overridePendingTransition(anim.fade_in, anim.fade_out);
		 */

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
//			if (ApplicationController.getInstance().isConnectedToBackend(LoginActivity.this)) {
//				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//				showProgress(true);
//				mAuthTask = new UserLoginTask();
//				mAuthTask.execute(); // runs on background thread
//			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					ParseUser.logOut();
					mEmailView.setText(data.getStringExtra(Intent.EXTRA_EMAIL));
				}
			}
		}
	}

	private void showProgress(final boolean show) {
		ApplicationController.getInstance().showProgress(show, mLoginStatusView, mLoginFormView);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private User logged;

		@Override
		protected Boolean doInBackground(Void... params) {
			String login_result = null;

			try {
				login_result = UserDatabaseAdapter.login(mEmail, mPassword);
				if (login_result.contains("invalid")) {
					Log.e(TAG, "Invalid email or password");
					return false;
				} else {
					SessionManager session = SessionManager.getInstance();
					session.clear();
					session.createLoginSession(login_result);
					logged = UserDatabaseAdapter.getUserInfo(login_result);
					session.createLoginSession(logged);
				}
				Thread.sleep(2000);
			} catch (Exception e) {
				Log.e(TAG + " Error", e.getLocalizedMessage());
				return false;
			}

			// return login_success;
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			// if successful login, start main activity
			if (success) {
				// get ParseUser
				ParseUser.logInInBackground(logged.getDisplayName(), mPassword,
						new LogInCallback() {
							@Override
							public void done(ParseUser user, ParseException e) {
								if (user != null) {
									// Hooray! The user is logged in.
									ParseInstallation installation = ParseInstallation
											.getCurrentInstallation();
									installation.put("user", user);
									installation.put("userId",
											user.getObjectId());
									installation.saveEventually();
								} else {
									Log.e(TAG, e.getLocalizedMessage());
								}

							}
						});
				Intent main = new Intent(LoginActivity.this, MainActivity.class);
				main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(main);
				overridePendingTransition(anim.fade_in, anim.fade_out);
			} else {
				mPasswordView
						.setError(getString(R.string.error_invalid_username_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}

	}
}
