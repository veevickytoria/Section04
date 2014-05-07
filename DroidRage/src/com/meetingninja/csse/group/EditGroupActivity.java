package com.meetingninja.csse.group;

import java.util.ArrayList;
import java.util.List;

import objects.Contact;
import objects.Group;
import objects.SerializableUser;
import objects.User;
import objects.parcelable.UserParcel;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.extras.ContactTokenTextView;
import com.meetingninja.csse.user.AutoCompleteAdapter;
import com.meetingninja.csse.user.ProfileActivity;
import com.meetingninja.csse.user.UserArrayAdapter;
import com.meetingninja.csse.user.tasks.GetContactsTask;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

import de.timroes.android.listview.EnhancedListView;

public class EditGroupActivity extends Activity implements TokenListener {

	private static final String TAG = EditGroupActivity.class.getSimpleName();
	private Group displayedGroup;
	private UserArrayAdapter mUserAdapter;
	EditText titleText;
	EnhancedListView mListView;
	private ArrayList<User> allUsers = new ArrayList<User>();
	private List<User> bothUsers = new ArrayList<User>();

	private AutoCompleteAdapter autoAdapter;
	private Dialog dlg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_group);
		setupActionBar();
		Bundle data = getIntent().getExtras();
		if (data != null){
			displayedGroup = data.getParcelable(Keys.Group.PARCEL);
		}else{
			Log.e(TAG, "Error: Unable to get group from parcel");
		}
		setupTitle();
		keyboardCanHide();
		displayMembers();
		deleteMember();
		mListView.setRequireTouchBeforeDismiss(false);
		mListView.setUndoHideDelay(5000);
		clickingAndViewingAUser();
		enableSwiping();
		fetchUsers();
		fetchContacts();
		bothUsers.addAll(allUsers);
	}
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		
	}
	private void keyboardCanHide() {
		findViewById(R.id.group_edit_main_container).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});
	}
	
	private void displayMembers() {
		mUserAdapter = new UserArrayAdapter(this, R.layout.list_item_user,displayedGroup.getMembers());
		mListView = (EnhancedListView) findViewById(R.id.group_list);
		mListView.setAdapter(mUserAdapter);
		for (int k = 0; k < displayedGroup.getMembers().size(); k++) {
			User kthUser = displayedGroup.getMembers().get(k);
			if (TextUtils.isEmpty(kthUser.getDisplayName())) {
				loadUser(kthUser.getID());
				displayedGroup.getMembers().remove(k);
				k--;
			}
		}
	}

	private void setupTitle() {
		titleText = (EditText) findViewById(R.id.group_edit_title);
		titleText.setText(displayedGroup.getGroupTitle());
	}
	public void addMember(View view) {
		dlg = new Dialog(this);
		dlg.setTitle("Search by name or email:");
		View autocompleteView = getLayoutInflater().inflate(R.layout.fragment_autocomplete, null);
		final ContactTokenTextView input = (ContactTokenTextView) autocompleteView.findViewById(R.id.my_autocomplete);
		autoAdapter = new AutoCompleteAdapter(this, allUsers);
		input.setAdapter(autoAdapter);
		input.setTokenListener(this);
		dlg.setContentView(autocompleteView);
		dlg.show();
	}

	private void deleteMember() {
		mListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
			@Override
			public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {

				final User item = mUserAdapter.getItem(position);
				mUserAdapter.remove(item);
				return new EnhancedListView.Undoable() {
					@Override
					public void undo() {
						mUserAdapter.insert(item, position);
					}

					@Override
					public String getTitle() {
						return "Member deleted";
					}
				};
			}
		});
	}

	private void clickingAndViewingAUser() {
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				User clicked = mUserAdapter.getItem(position);
				Intent profileIntent = new Intent(v.getContext(),ProfileActivity.class);
				profileIntent.putExtra(Keys.User.PARCEL,new UserParcel(clicked));
				startActivity(profileIntent);

			}

		});
	}

	private void fetchUsers() {
		UserVolleyAdapter.fetchAllUsers(new AsyncResponse<List<User>>() {
			@Override
			public void processFinish(List<User> result) {
				allUsers = new ArrayList<User>(result);

			}
		});
	}

	private void fetchContacts() {
		new GetContactsTask(new AsyncResponse<List<Contact>>() {
			@Override
			public void processFinish(List<Contact> result) {
				// addContacts(result);
			}
		}).execute(SessionManager.getUserID());
	}

	private void enableSwiping() {
		mListView.enableSwipeToDismiss();
		mListView.setSwipingLayout(R.id.list_group_item_frame_1);

		mListView.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_edit_group, menu);
		return true;
	}

	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
		String title = titleText.getText().toString().trim();
		if (title.equals("")) {
			AlertDialogUtil.showErrorDialog(this, "Cannot have an empty title");
			return;
		}

		displayedGroup.setGroupTitle(title);
		Intent i = new Intent();
		i.putExtra(Keys.Group.PARCEL, displayedGroup);
		setResult(RESULT_OK, i);
		finish();
	}

	private void loadUser(String userID) {
		UserVolleyAdapter.fetchUserInfo(userID, new AsyncResponse<User>() {
			@Override
			public void processFinish(User result) {
				displayedGroup.addMember(result);
				mUserAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onTokenAdded(Object arg0) {
		SerializableUser added = null;
		if (arg0 instanceof SerializableUser){
			added = (SerializableUser) arg0;
		} else if (arg0 instanceof User){
			added = new SerializableUser((User) arg0);
		}

		if (added != null) {
			if(displayedGroup.getMembers().contains(added)){
				Toast.makeText(this, "This User is already added to this group", Toast.LENGTH_LONG).show();
			}else{
				displayedGroup.addMember(added);
				mUserAdapter.notifyDataSetChanged();
			}
			dlg.dismiss();
		}
	}

	@Override
	public void onTokenRemoved(Object arg0) {
		SerializableUser removed = null;
		if (arg0 instanceof SerializableUser)
			removed = (SerializableUser) arg0;
		else if (arg0 instanceof User)
			removed = new SerializableUser((User) arg0);

		if (removed != null) {
			System.out.println("token was removed");
		}
	}
}
