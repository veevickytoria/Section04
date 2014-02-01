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

import objects.SerializableUser;
import objects.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
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

public class UserListFragment extends ListFragment implements AsyncResponse<List<User>> {
	
	private SQLiteUserAdapter dbHelper;
	private UserArrayAdapter mUserAdapter;
	private List<User> users = new ArrayList<User>();
	RetContactsObj fetcher = null;
	public UserListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_userlist, container, false);

		dbHelper = new SQLiteUserAdapter(getActivity());

		mUserAdapter = new UserArrayAdapter(getActivity(),R.layout.list_item_contact, users);
		setListAdapter(mUserAdapter);

		populateList(); // uses async-task

		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(getTag(), "Clicked this one");
		User clicked = mUserAdapter.getItem(position);
//		Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
		Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
		profileIntent.putExtra(Keys.User.PARCEL, clicked);
		startActivity(profileIntent);
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
			//displayTask.addMember(result);
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
