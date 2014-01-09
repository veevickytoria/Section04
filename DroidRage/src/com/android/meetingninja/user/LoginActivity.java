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
package com.android.meetingninja.user;

import java.io.IOException;

import objects.User;
import android.R.anim;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.meetingninja.MainActivity;
import com.android.meetingninja.R;
import com.android.meetingninja.database.UserDatabaseAdapter;
import com.android.meetingninja.extras.Utilities;

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
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

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
		} else if (!Utilities.isValidEmailAddress(mEmail)) {
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

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					mEmailView.setText(data.getStringExtra(Intent.EXTRA_EMAIL));
				}
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			String login_result = "";

			try {
				login_result = UserDatabaseAdapter.login(mEmail, mPassword);
				if (login_result.contains("invalid")) {
					Log.e(TAG, mEmail + " does not exist");
					return false;
				} else {
					SessionManager session = SessionManager.getInstance();
					session.clear();
					session.createLoginSession(login_result);
					User loggedIn = UserDatabaseAdapter
							.getUserInfo(login_result);
					session.createLoginSession(loggedIn);
				}
				Thread.sleep(2000);
			} catch (IOException e) {
				Log.e(TAG + " Error", e.getLocalizedMessage());
				return false;
			} catch (InterruptedException e) {
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
				// SessionManager session = new SessionManager(
				// getApplicationContext());
				// session.clear();
				// session.createLoginSession(mEmail);
				Intent main = new Intent(mLoginFormView.getContext(),
						MainActivity.class);
				main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				main.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

				startActivityForResult(main, 0);
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
