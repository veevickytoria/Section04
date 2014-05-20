package com.meetingninja.csse.group;

import objects.Group;
import objects.User;
import objects.parcelable.UserParcel;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.group.tasks.AsyncGroupDeleteTask;
import com.meetingninja.csse.group.tasks.GroupUpdaterTask;
import com.meetingninja.csse.user.ProfileActivity;
import com.meetingninja.csse.user.adapters.UserArrayAdapter;

import de.timroes.android.listview.EnhancedListView;

public class ViewGroupActivity extends Activity {
	private static final String TAG = ViewGroupActivity.class.getSimpleName();
	public static final int REQUEST_CODE = 8;
	private Group displayedGroup;
	private UserArrayAdapter mUserAdapter;
	private TextView titleText;
	private EnhancedListView mListView;
	private int resultCode = Activity.RESULT_CANCELED;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_group);
		Bundle data = getIntent().getExtras();
		if (data != null){
			displayedGroup = data.getParcelable(Keys.Group.PARCEL);
		}else{
			Log.e(TAG, "Error: Unable to get group from parcel");
			displayedGroup = new Group();
		}
		titleText = (TextView) findViewById(R.id.group_view_title);
		mListView = (EnhancedListView) findViewById(R.id.group_list);
		mListView.setEmptyView(findViewById(android.R.id.empty));
		showGroup(displayedGroup);
		
		for (int k = 0; k < displayedGroup.getMembers().size(); k++) {
			User kthMember = displayedGroup.getMembers().get(k);
			if (kthMember.getDisplayName() == null|| kthMember.getDisplayName().isEmpty()) {
				loadUser(kthMember.getID());
				displayedGroup.getMembers().remove(k);
				k--;
			}
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				User clicked = mUserAdapter.getItem(position);
				Intent profileIntent = new Intent(v.getContext(),ProfileActivity.class);
				profileIntent.putExtra(Keys.User.PARCEL, new UserParcel(clicked));
				startActivity(profileIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_view_group, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit_item_group:
			editGroup();
			return true;
		case R.id.delete_item_group:
			AlertDialogUtil.deleteDialog(this, "group", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					delete(displayedGroup);
				}
			});
			return true;
		case android.R.id.home:
			setResult(resultCode);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void delete(Group group) {
		new AsyncGroupDeleteTask(new AsyncResponse<Boolean>() {

			@Override
			public void processFinish(Boolean result) {
				setResult(RESULT_OK);
				finish();				
			}
		}).execute(group.getID()); 	
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == EditGroupActivity.REQUEST_CODE) {
				displayedGroup = data.getParcelableExtra(Keys.Group.PARCEL);
				
				Log.d(TAG, "Showing group: " + displayedGroup.getID());
				GroupUpdaterTask updater = new GroupUpdaterTask();
				updater.updateGroup(displayedGroup);
				showGroup(displayedGroup);
			}
		}
	}

	private void showGroup(Group g) {
		titleText.setText(g.getTitle());
		mUserAdapter = new UserArrayAdapter(this, R.layout.list_item_user_reversed,g.getMembers());
		mListView.setAdapter(mUserAdapter);
	}

	private void editGroup() {
		resultCode = Activity.RESULT_OK;
		Intent i = new Intent(this, EditGroupActivity.class);
		i.putExtra(Keys.Group.PARCEL, displayedGroup);
		startActivityForResult(i, EditGroupActivity.REQUEST_CODE);
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
}
