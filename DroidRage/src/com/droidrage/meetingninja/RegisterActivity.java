package com.droidrage.meetingninja;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class RegisterActivity extends Activity implements AsyncResponse<Void> {

	EditText usernameText;
	EditText passwordText;
	EditText confirmPasswordText;
	public static final String ARG_USERNAME = "user";

	AlertDialog.Builder builder;

	AlertDialog.Builder passCheck;
	private RegisterTask registerTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		// Show the Up button in the action bar.
		setupActionBar();

		usernameText = ((EditText) findViewById(R.id.usernameText));
		passwordText = ((EditText) findViewById(R.id.passwordText));
		confirmPasswordText = ((EditText) findViewById(R.id.confirmPassword));
		
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
						register();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void register() {
		// Reset errors
		usernameText.setError(null);
		passwordText.setError(null);
		confirmPasswordText.setError(null);

		// Store values
		String user = usernameText.getText().toString();
		String pass = passwordText.getText().toString();
		String confPass = confirmPasswordText.getText().toString();

		Log.d("Registering", user + " : " + pass + " : " + confPass);

		boolean cancel = false;
		View focusView = null;

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
			registerTask.execute(user, pass);
			Intent goLogin = new Intent(this, LoginActivity.class);

			goLogin.putExtra(LoginActivity.EXTRA_USERNAME, user);
			startActivity(goLogin);
		}

	}

	@Override
	public void processFinish(Void result) {
		return;
	}

	private class RegisterTask extends AsyncTask<String, Void, Void> {

		private AsyncResponse<Void> delegate;

		public RegisterTask(AsyncResponse<Void> del) {
			this.delegate = del;
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				DatabaseAdapter.register(params[0], params[1]);
			} catch (Exception e) {
				Log.e("DB Adapter", "Register failed");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

}
