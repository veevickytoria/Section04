package com.meetingninja.csse.user;

import objects.User;
import objects.parcelable.ParcelDataFactory;
import objects.parcelable.UserParcel;
import android.R.menu;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.meetingninja.csse.user.tasks.UpdateUserTask;

public class EditProfileActivity extends Activity {
	private static final String TAG = EditProfileActivity.class.getSimpleName();

	public static final int REQUEST_CODE = 7;

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
			displayedUser = new ParcelDataFactory(extras).getUser();
		} else {
			Log.v(TAG, "Problem getting user info");
			// displayedUser.setID(session.getUserID());
		}

		if (displayedUser != null) {
			setUser(displayedUser);
		}

		// fetchUserInfo(displayedUser.getID());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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
		View actionBarButtons = inflater.inflate(R.layout.actionbar_ok_cancel,new LinearLayout(this), false);

		View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
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
		mName.setError(null);
		mEmail.setError(null);
		
		if (TextUtils.isEmpty(mName.getText())) {
			mName.setError("Field cannot be empty");
			return;
		}
		if (TextUtils.isEmpty(mEmail.getText())) {
			mEmail.setError("Field cannot be empty");
			return;
		}

		displayedUser.setDisplayName(mName.getText().toString());
//		mUserImage = (SmartImageView) findViewById(R.id.view_prof_pic);
		displayedUser.setCompany(mCompany.getText().toString());
		displayedUser.setTitle(mTitle.getText().toString());
		displayedUser.setLocation(mLocation.getText().toString());
		displayedUser.setEmail(mEmail.getText().toString());
		displayedUser.setPhone(mPhone.getText().toString());
		saveUser();
		session.createLoginSession(displayedUser);
		Intent i = new Intent();
		i.putExtra(Keys.User.PARCEL, new UserParcel(displayedUser));
		setResult(RESULT_OK, i);
		finish();
	}

	private void saveUser() {
		new UpdateUserTask(){
			@Override
			protected void onPostExecute(User user) {
				setUser(user);
			}
		}.execute(displayedUser);
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
			mName.setText(user.getDisplayName());
			mEmail.setText(user.getEmail());
			mTitle.setText(user.getTitle());
			mCompany.setText(user.getCompany());
			mLocation.setText(user.getLocation());
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
