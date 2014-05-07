package com.meetingninja.csse.group;

import objects.Group;
import objects.User;
import objects.parcelable.UserParcel;
import android.app.Activity;
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
import com.meetingninja.csse.user.ProfileActivity;
import com.meetingninja.csse.user.UserArrayAdapter;

import de.timroes.android.listview.EnhancedListView;

public class ViewGroupActivity extends Activity {
	private static final String TAG = ViewGroupActivity.class.getSimpleName();
	private Group group;
	private UserArrayAdapter mUserAdapter;
	TextView titleText;
	EnhancedListView mListView;
	private int resultCode = Activity.RESULT_CANCELED;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_group);
		Bundle data = getIntent().getExtras();
		if (data != null){
			group = data.getParcelable(Keys.Group.PARCEL);
		}else{
			Log.e(TAG, "Error: Unable to get group from parcel");
		}
		titleText = (TextView) findViewById(R.id.group_view_title);
		mListView = (EnhancedListView) findViewById(R.id.group_list);
		setGroup();
		for (int k = 0; k < group.getMembers().size(); k++) {
			if (group.getMembers().get(k).getDisplayName() == null|| group.getMembers().get(k).getDisplayName().isEmpty()) {
				loadUser(group.getMembers().get(k).getID());
				group.getMembers().remove(k);
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
			new AsyncGroupDeleteTask(){
				@Override
				protected void onPostExecute(Boolean success) {
					if(success){
						setResult(RESULT_OK);
						finish();
					}
				}
			}.execute(group.getID());
		case android.R.id.home:
			setResult(resultCode);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 8) {
				group = data.getParcelableExtra(Keys.Group.PARCEL);
				System.out.println("test the id in view mode");
				System.out.println(group.getID());
				GroupUpdaterTask updater = new GroupUpdaterTask();
				updater.updateGroup(group);
				setGroup();
			}
		}
	}

	private void setGroup() {
		titleText.setText(group.getGroupTitle());
		mUserAdapter = new UserArrayAdapter(this, R.layout.list_item_user,group.getMembers());
		mListView.setAdapter(mUserAdapter);
	}

	private void editGroup() {
		resultCode = Activity.RESULT_OK;
		Intent i = new Intent(this, EditGroupActivity.class);
		i.putExtra(Keys.Group.PARCEL, group);
		startActivityForResult(i, 8);
	}

	private void loadUser(String userID) {
		UserVolleyAdapter.fetchUserInfo(userID, new AsyncResponse<User>() {
			@Override
			public void processFinish(User result) {
				group.addMember(result);
				mUserAdapter.notifyDataSetChanged();
			}
		});
	}
}
