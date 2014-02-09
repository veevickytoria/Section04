package com.meetingninja.csse.user;

import java.util.HashMap;

import objects.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.loopj.android.image.SmartImageView;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.Keys;

public class EditProfileActivity extends Activity {
	private static final String TAG = EditProfileActivity.class.getSimpleName();

	private EditText mTitle, mCompany, mName, mPhone, mEmail, mLocation;
	private SessionManager session;
	private User displayedUser;
	private SmartImageView mUserImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		setupViews();
		setupActionBar();
		session = SessionManager.getInstance();

		Bundle extras = getIntent().getExtras();
		displayedUser = new User();

		if (extras != null && extras.containsKey(Keys.User.PARCEL)) {
			displayedUser = (User) extras.getParcelable(Keys.User.PARCEL);
		} else {
			Log.v(TAG, "Problem getting user info");
			// displayedUser.setID(session.getUserID());
		}
		setUser(displayedUser);

		// fetchUserInfo(displayedUser.getID());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
		return true;
	}

	private final View.OnClickListener gActionBarListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onActionBarItemSelected(v);
		}
	};

	private void setupActionBar() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Make an Ok/Cancel ActionBar
		View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel,
				new LinearLayout(this), false);

		View cancelActionView = actionBarButtons
				.findViewById(R.id.action_cancel);
		cancelActionView.setOnClickListener(gActionBarListener);

		View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(gActionBarListener);

		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(actionBarButtons);
		// end Ok-Cancel ActionBar

	}

	private boolean onActionBarItemSelected(View v) {
		switch (v.getId()) {
		case R.id.action_done:
			save();
			break;
		case R.id.action_cancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
		return true;
	}

	private void save() {
		if (mName.getText() == null || mName.getText().toString().isEmpty()) {
			// TODO: stuff
			return;
		}
		if (mEmail.getText() == null || mEmail.getText().toString().isEmpty()) {
			// TODO: stuff
			return;
		}
		displayedUser.setDisplayName(mName.getText().toString());// mUserImage =
																	// (SmartImageView)
																	// findViewById(R.id.view_prof_pic);
		displayedUser.setCompany(mCompany.getText().toString());
		displayedUser.setTitle(mTitle.getText().toString());
		displayedUser.setLocation(mLocation.getText().toString());
		displayedUser.setEmail(mEmail.getText().toString());
		displayedUser.setPhone(mPhone.getText().toString());
		dbSave();
		session.createLoginSession(displayedUser);
		Intent i = new Intent();
		i.putExtra(Keys.User.PARCEL, displayedUser);
		setResult(RESULT_OK, i);
		finish();
	}

	private void dbSave() {
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put(Keys.User.NAME, displayedUser.getDisplayName());
		fields.put(Keys.User.COMPANY, displayedUser.getCompany());
		fields.put(Keys.User.LOCATION, displayedUser.getLocation());
		// fields.put(Keys.User.EMAIL, displayedUser.getEmail());
		fields.put(Keys.User.PHONE, displayedUser.getPhone());
		UserUpdateTask updater = new UserUpdateTask(fields);
		updater.execute(displayedUser.getID());
	}

	private void setupViews() {
		// informationView = findViewById(R.id.profile_container);
		// emptyView = findViewById(android.R.id.empty);

		mUserImage = (SmartImageView) findViewById(R.id.view_prof_pic);

		mName = (EditText) findViewById(R.id.profile_name);
		mTitle = (EditText) findViewById(R.id.profile_title);
		mCompany = (EditText) findViewById(R.id.profile_company);
		mLocation = (EditText) findViewById(R.id.profile_location);

		mEmail = (EditText) findViewById(R.id.profile_email);
		mEmail.setEnabled(false);

		mPhone = (EditText) findViewById(R.id.profile_phone);

	}

	private void setUser(User user) {
		this.displayedUser = user;
		if (user != null) {
			// set display name
			mName.setText(user.getDisplayName());

			// set email
			mEmail.setText(user.getEmail());

			// set title & company
			mTitle.setText(user.getTitle());

			mCompany.setText(user.getCompany());
			// StringBuilder sb = new StringBuilder();
			// if (!(user.getTitle().isEmpty() || user.getCompany().isEmpty()))
			// {
			// if (!user.getTitle().isEmpty())
			// sb.append(user.getTitle());
			// if (!user.getTitle().isEmpty())
			// sb.append(", " + user.getCompany());
			// mTitleCompany.setText(sb);
			// mTitleCompany.setVisibility(View.VISIBLE);
			// } else {
			// mTitleCompany.setVisibility(View.GONE);
			// }

			// set location
			mLocation.setText(user.getLocation());

			// set phone

			mPhone.setText(user.getPhone());

		} else {
			mTitle.setVisibility(View.GONE);
			mCompany.setVisibility(View.GONE);
			mLocation.setVisibility(View.GONE);
			findViewById(R.id.profile_phone_row).setVisibility(View.GONE);

		}

		// Swap visibility after loading information
		// emptyView.setVisibility(View.GONE);
		// informationView.setVisibility(View.VISIBLE);
	}

}
