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

import objects.Contact;
import objects.Group;
import objects.SerializableUser;
import objects.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteUserAdapter;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.extras.ContactTokenTextView;

import objects.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.local.SQLiteUserAdapter;
import com.meetingninja.csse.extras.ContactTokenTextView;
import com.meetingninja.csse.user.TempContactArrayAdapter.addContactObj;

import de.timroes.android.listview.EnhancedListView;

public class UserListFragment extends Fragment implements TokenListener{
	
	private SQLiteUserAdapter dbHelper;
	private ContactArrayAdapter mContactAdapter;
	RetContactsObj fetcher = null;
	private EnhancedListView l;
	
	private AutoCompleteAdapter autoAdapter;
	private ArrayList<User> allUsers = new ArrayList<User>();
	private User addedUser;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();

	public UserListFragment() {
		// Required empty public constructor
	}
	//to add users. change db adapter to ContactArrayAdapter change list_item_user to list_item_contact and make it show all users not just contacts
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		setHasOptionsMenu(true);
		View v = inflater.inflate(R.layout.fragment_userlist, container, false);
		
		dbHelper = new SQLiteUserAdapter(getActivity());

//		mContactAdapter = new ContactArrayAdapter(getActivity(),R.layout.list_item_user, contacts);
		setUpListView(v);
		populateList(true); // uses async-task
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_new_and_refresh, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			populateList(false);
			return true;
		case R.id.action_new:
			addContactsOption();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	public void addContactsOption() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Search by name or email:");
		//TODO: only display users that aren't already a contact
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
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean contains=false;
				for(int i=0;i<contacts.size();i++){
					if(contacts.get(i).getContact().equals(addedUser)){
						contains=true;
					}
				}
				if(contains){
					AlertDialogUtil.displayDialog(getActivity(), "Unable to add contact","This user is already added as a contact", "OK",null);
				}else{
					addContact(addedUser);
					addedUser=null;
				}
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
			addedUser = added;
//			addedContacts.add(added);
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
			addedUser = null;
		}

	}
	public void setContacts(List<Contact> tempContacts){
		contacts.clear();
		contacts.addAll(tempContacts);
		mContactAdapter.notifyDataSetChanged();
	}
	private void addContact(User user) {
		AddContactTask adder = new AddContactTask(this);
		adder.addContact(user.getID());
	}

	@Override
	public void onPause() {
		dbHelper.close();
		super.onPause();
	}

	private void populateList(boolean add){
//		UserVolleyAdapter.fetchAllUsers(this);
		SessionManager session = SessionManager.getInstance();
		fetcher = new RetContactsObj(add);
		fetcher.execute(session.getUserID());
	}
	
	private void setUpListView(View v){
		mContactAdapter = new ContactArrayAdapter(getActivity(), R.layout.list_item_user,contacts);
		l = (EnhancedListView) v.findViewById(R.id.contacts_list);
		l.setAdapter(mContactAdapter);
		l.setEmptyView(v.findViewById(android.R.id.empty));
		l.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
			@Override
			public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {

				final Contact item = (Contact) mContactAdapter.getItem(position);
				mContactAdapter.remove(item);
				
				return new EnhancedListView.Undoable() {
					@Override
					public void undo() {
						mContactAdapter.insert(item, position);
					}

					@Override
					public String getTitle() {
						return "Member deleted";
					}
					@Override
						public void discard(){
						//TODO: check if user does want to eliminate contact
						//TODO: delete them
						
					}
				};
			}
		});
		l.setUndoHideDelay(5000);
		l.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				User clicked = mContactAdapter.getItem(position).getContact();
				Intent profileIntent = new Intent(v.getContext(),ProfileActivity.class);
				profileIntent.putExtra(Keys.User.PARCEL, clicked);
				startActivity(profileIntent);

			}

		});
		l.enableSwipeToDismiss();
		l.setSwipingLayout(R.id.list_group_item_frame_1); 

		l.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
		
//		List<User> mems = new ArrayList<User>();
//		mems = displayTask.getMembers();
//		for(int i = 0;i<mems.size();i++){
//			loadUser(mems.get(i).toString(),false);
//		}
//		//TODO: change to a loop when backend catches up
//		String mem = displayTask.getAssignedTo();
//		loadUser(mem,false);

		
		
	}
	
	final class RetContactsObj implements AsyncResponse<List<Contact>> {

		private ContactsFetcher contactsFetcher;
		private boolean add;
		public RetContactsObj(boolean add) {
			contactsFetcher = new ContactsFetcher(this);
			this.add = add;
		}

		public void execute(String userID) {
			contactsFetcher.execute(userID);
		}

		@Override
		public void processFinish(List<Contact> result) {
			contacts.clear();
			contacts.addAll(result);
			mContactAdapter.notifyDataSetChanged();
		}
	}

//	@Override
//	public void processFinish(List<User> result) {
//		contacts.clear();
//		mContactAdapter.clear();
//
//		Collections.sort(result, new Comparator<User>() {
//			@Override
//			public int compare(User lhs, User rhs) {
//				return lhs.getDisplayName().toLowerCase().compareTo(rhs.getDisplayName().toLowerCase());
//			}
//		});
//		contacts.addAll(result);
//
//		mContactAdapter.notifyDataSetChanged();
//
//	}

}
