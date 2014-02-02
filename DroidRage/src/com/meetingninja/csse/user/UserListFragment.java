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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import objects.Group;
import objects.SerializableUser;
import objects.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteUserAdapter;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;
import com.meetingninja.csse.extras.ContactTokenTextView;



import objects.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteUserAdapter;
import com.meetingninja.csse.extras.ContactTokenTextView;
import com.meetingninja.csse.group.EditGroupActivity;

public class UserListFragment extends ListFragment implements AsyncResponse<List<User>>, TokenListener{
	
	private SQLiteUserAdapter dbHelper;
	private UserArrayAdapter mUserAdapter;
	private List<User> users = new ArrayList<User>();
	RetContactsObj fetcher = null;
	private AutoCompleteAdapter autoAdapter;
	private ArrayList<User> allUsers = new ArrayList<User>();
	private ArrayList<User> addedUsers = new ArrayList<User>();

	public UserListFragment() {
		// Required empty public constructor
	}
	//to add users. change db adapter to ContactArrayAdapter change list_item_user to list_item_contact and make it show all users not just contacts
	//TODO: change to enhanced listview and check if user does want to eliminate contact
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		setHasOptionsMenu(true);
		View v = inflater.inflate(R.layout.fragment_userlist, container, false);

		dbHelper = new SQLiteUserAdapter(getActivity());

		mUserAdapter = new UserArrayAdapter(getActivity(),R.layout.list_item_user, users);
		setListAdapter(mUserAdapter);

		populateList(); // uses async-task

		return v;
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(getTag(), "Clicked this one");
		User clicked = mUserAdapter.getItem(position);
		Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
		profileIntent.putExtra(Keys.User.PARCEL, clicked);
		startActivity(profileIntent);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_new_and_refresh, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			populateList();
			return true;
		case R.id.action_new:
			addContactsOption();
//			Intent i = new Intent(getActivity(), EditGroupActivity.class);
//			Group g = new Group();
//			i.putExtra(Keys.User.PARCEL, g);
			//startActivityForResult(i, 7);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	//TODO: is addedusers even useful?
	public void addContactsOption() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Search by name or email:");
		//TODO: change to users not in contacts or something of the sort
		UserVolleyAdapter.fetchAllUsers(new AsyncResponse<List<User>>() {
			@Override
			public void processFinish(List<User> result) {
				allUsers = new ArrayList<User>(result);
			}
		});
		
		View autocompleteView = getActivity().getLayoutInflater().inflate(R.layout.fragment_autocomplete, null);
		final ContactTokenTextView input = (ContactTokenTextView) autocompleteView.findViewById(R.id.my_autocomplete);
		autoAdapter = new AutoCompleteAdapter(getActivity(), allUsers);
		input.setAdapter(autoAdapter);
		
		input.setTokenListener(this);
		builder.setView(autocompleteView);
		builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				group.getMembers().addAll(addedUsers);
				addedUsers.clear();
				mUserAdapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("Cancel",	new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.show();
	}
	@Override
	public void onTokenAdded(Object arg0) {
		SerializableUser added = null;
		if (arg0 instanceof SerializableUser)
			added = (SerializableUser) arg0;
		else if (arg0 instanceof User)
			added = new SerializableUser((User) arg0);

		if (added != null) {
			addedUsers.add(added);
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
			addedUsers.remove(removed);
		}

	}


	@Override
	public void onPause() {
		dbHelper.close();
		super.onPause();
	}

	private void populateList(){
//		UserVolleyAdapter.fetchAllUsers(this);
		SessionManager session = SessionManager.getInstance();
		fetcher = new RetContactsObj();
		fetcher.execute(session.getUserID());
	}
	final class RetContactsObj implements AsyncResponse<List<User>> {

		private ContactsFetcher contactsFetcher;

		public RetContactsObj() {
			contactsFetcher = new ContactsFetcher(this);
		}

		public void execute(String userID) {
			contactsFetcher.execute(userID);
		}

		@Override
		public void processFinish(List<User> result) {
			users.addAll(result);
			mUserAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void processFinish(List<User> result) {
		users.clear();
		mUserAdapter.clear();

		Collections.sort(result, new Comparator<User>() {
			@Override
			public int compare(User lhs, User rhs) {
				return lhs.getDisplayName().toLowerCase()
						.compareTo(rhs.getDisplayName().toLowerCase());
			}
		});
		users.addAll(result);

		mUserAdapter.notifyDataSetChanged();

	}

}
