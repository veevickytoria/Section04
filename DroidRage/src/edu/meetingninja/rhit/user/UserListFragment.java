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
package edu.meetingninja.rhit.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import objects.User;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.meetingninja.rhit.ApplicationController;
import edu.meetingninja.rhit.R;
import edu.meetingninja.rhit.database.AsyncResponse;
import edu.meetingninja.rhit.database.JsonNodeRequest;
import edu.meetingninja.rhit.database.Keys;
import edu.meetingninja.rhit.database.UserDatabaseAdapter;
import edu.meetingninja.rhit.database.local.SQLiteUserAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.fasterxml.jackson.databind.JsonNode;

public class UserListFragment extends ListFragment implements
		AsyncResponse<List<User>> {

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
				R.layout.line_item_user, users);
		setListAdapter(mUserAdapter);

		fetchAllUsers();

		// populateList();

		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(getTag(), "Clicked");
		User clicked = mUserAdapter.getItem(position);
		Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
		profileIntent.putExtra(Keys.User.PARCEL, clicked);
		startActivity(profileIntent);
	}

	private void fetchAllUsers() {
		String _url = UserDatabaseAdapter.getBaseUri().appendPath("Users")
				.build().toString();

		JsonNodeRequest req = new JsonNodeRequest(_url, null,
				new Response.Listener<JsonNode>() {

					@Override
					public void onResponse(JsonNode response) {
						VolleyLog.v("Response:%n %s", response);
						// List<User> userList = new ArrayList<User>();
						// final JsonNode userArray = response.get("users");
						//
						// if (userArray.isArray()) {
						// for (final JsonNode userNode : userArray) {
						// User u = UserDatabaseAdapter
						// .parseUser(userNode);
						// // assign and check null and do not add local
						// // user
						// if (u != null) {
						// userList.add(u);
						// // dbHelper.insertUser(u);
						// }
						// }
						// }

						processFinish(UserDatabaseAdapter
								.parseUserList(response));
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.e("Error: ", error.getMessage());

					}
				});

		// add the request object to the queue to be executed
		ApplicationController app = ApplicationController.getInstance();
		app.addToRequestQueue(req, "JSON");

	}

	@Override
	public void onPause() {
		dbHelper.close();
		super.onPause();
	}

	private void populateList() {
		UserListFetcher task = new UserListFetcher(UserListFragment.this);
		task.execute((Void) null);

	}

	@Override
	public void processFinish(List<User> result) {
		users.clear();
		mUserAdapter.clear();

		users.addAll(result);

		mUserAdapter.notifyDataSetChanged();

	}

	private class UserListFetcher extends AsyncTask<Void, Void, List<User>> {

		private AsyncResponse<List<User>> delegate;

		public UserListFetcher(AsyncResponse<List<User>> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected List<User> doInBackground(Void... params) {
			try {
				return UserDatabaseAdapter.getAllUsers();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<User> result) {
			delegate.processFinish(result);
			super.onPostExecute(result);
		}

	}

}
