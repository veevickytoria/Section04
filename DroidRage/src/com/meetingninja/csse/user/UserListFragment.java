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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import objects.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteUserAdapter;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;

public class UserListFragment extends ListFragment {

	private SQLiteUserAdapter dbHelper;
	private UserArrayAdapter mUserAdapter;
	private List<User> users = new ArrayList<User>();

	public UserListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_userlist, container, false);

		dbHelper = new SQLiteUserAdapter(getActivity());

		mUserAdapter = new UserArrayAdapter(getActivity(),
				R.layout.list_item_user, users);
		setListAdapter(mUserAdapter);

		populateList(); // uses async-task

		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		User clicked = mUserAdapter.getItem(position);
		Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
		profileIntent.putExtra(Keys.User.PARCEL, clicked);
		startActivity(profileIntent);
	}

	@Override
	public void onPause() {
		dbHelper.close();
		super.onPause();
	}

	private void populateList() {
		// Async-Task
		UserVolleyAdapter.fetchAllUsers(new AsyncResponse<List<User>>() {
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
		});
	}
}
