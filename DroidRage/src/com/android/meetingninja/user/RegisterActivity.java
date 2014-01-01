package com.android.meetingninja.user;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.database.UserDatabaseAdapter;

public class RegisterActivity extends Activity implements
		AsyncResponse<Boolean> {

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
		emailText.setError(null);
		passwordText.setError(null);
		confirmPasswordText.setError(null);

		// Store values
		String name = nameText.getText().toString();
		String email = emailText.getText().toString();
		String pass = passwordText.getText().toString();
		String confPass = confirmPasswordText.getText().toString();

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

			Intent goLogin = new Intent(this, LoginActivity.class);
			goLogin.putExtra(LoginActivity.EXTRA_EMAIL, email);
			Toast.makeText(getApplicationContext(), "Registration Successful",
					Toast.LENGTH_LONG).show();
			startActivity(goLogin);
			finish();
		}

	}

	@Override
	public void processFinish(Boolean result) {
		mRegisterSuccess = result;
	}

	private class RegisterTask extends AsyncTask<String, Void, Boolean> {

		private AsyncResponse<Boolean> delegate;

		public RegisterTask(AsyncResponse<Boolean> del) {
			this.delegate = del;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean success = false;
			try {
				// name, email, password
				UserDatabaseAdapter.register(params[0], params[1], params[2]);
				success = true;
			} catch (IOException e) {
				Log.e("DB Adapter", "Error: Register failed");
				Log.e("REGISTER_ERR", e.toString());
			}
			return success;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			delegate.processFinish(result);
		}
	}

}
