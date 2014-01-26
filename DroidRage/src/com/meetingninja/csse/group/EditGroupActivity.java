package com.meetingninja.csse.group;

import objects.Group;
import objects.User;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.user.ProfileActivity;
import com.meetingninja.csse.user.UserArrayAdapter;
import com.meetingninja.csse.user.UserInfoFetcher;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.user.UserArrayAdapter;
import com.meetingninja.csse.user.UserInfoFetcher;

public class EditGroupActivity extends SwipeListViewActivity {

	private Group group;
	private UserArrayAdapter mUserAdapter;
	EditText titleText;
	ListView l;
	RetUserObj fetcher = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		setupActionBar();

		Intent i = getIntent();
		group = i.getParcelableExtra(Keys.Group.PARCEL);

		titleText = (EditText) findViewById(R.id.group_edit_title);
		titleText.setText(group.getGroupTitle());

		// allows keyboard to hide when not editing text
		findViewById(R.id.group_edit_main_container).setOnTouchListener(
				new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						hideKeyboard();
						return false;
					}
				});

		// allows keyboard to hide when not editing text
		findViewById(R.id.group_edit_main_container).setOnTouchListener(
				new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						hideKeyboard();
						return false;
					}
				});

		mUserAdapter = new UserArrayAdapter(this, R.layout.list_item_user,
				group.getMembers());
		View v = findViewById(R.id.group_edit_user_list);
		l = (ListView) v.findViewById(android.R.id.list);
		l.setAdapter(mUserAdapter);

	}

	@Override
	public void getSwipeItem(boolean isRight, int position) {
		group.getMembers().remove(position);
		mUserAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_group, menu);
		return true;
	}

	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
				.getWindowToken(), 0);
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
			if (titleText.getText().equals(null)) {
				titleText.setText("");
			}
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
		if (titleText.getText().toString().isEmpty()) {
			AlertDialogUtil.displayDialog(this, "Error",
					"Cannot have an empty title", "OK");
			return;
		}

		group.setGroupTitle(titleText.getText().toString());
		Intent i = new Intent();
		i.putExtra(Keys.Group.PARCEL, group);
		setResult(RESULT_OK, i);
		finish();

	}

	public void addMember(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter member ID");
		final EditText input = new EditText(this);
		builder.setView(input);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadUser(input.getText().toString());
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	private void loadUser(String userID) {
		fetcher = new RetUserObj();
		fetcher.execute(userID);
	}

	@Override
	public ListView getListView() {
		return l;
	}

	@Override
	public void onItemClickListener(ListAdapter adapter, int position) {
		User clicked = mUserAdapter.getItem(position);
		Intent profileIntent = new Intent(this, ProfileActivity.class);
		profileIntent.putExtra(Keys.User.PARCEL, clicked);
		startActivity(profileIntent);
	}

	final class RetUserObj implements AsyncResponse<User> {

		private UserInfoFetcher infoFetcher;

		public RetUserObj() {
			infoFetcher = new UserInfoFetcher(this);
		}

		public void execute(String userID) {
			infoFetcher.execute(userID);
		}

		@Override
		public void processFinish(User result) {
			group.addMember(result);
			mUserAdapter.notifyDataSetChanged();
		}
	}
}
