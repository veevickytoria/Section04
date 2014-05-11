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
import java.util.List;

import objects.Contact;
import objects.SerializableUser;
import objects.User;
import objects.parcelable.UserParcel;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.local.SQLiteUserAdapter;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.AlertDialogUtil;
import com.meetingninja.csse.extras.ContactTokenTextView;
import com.meetingninja.csse.extras.NinjaToastUtil;
import com.meetingninja.csse.user.adapters.AutoCompleteAdapter;
import com.meetingninja.csse.user.adapters.ContactArrayAdapter;
import com.meetingninja.csse.user.tasks.AddContactTask;
import com.meetingninja.csse.user.tasks.DeleteContactTask;
import com.meetingninja.csse.user.tasks.GetContactsTask;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

import de.timroes.android.listview.EnhancedListView;

public class ContactsFragment extends Fragment implements TokenListener {

	private SQLiteUserAdapter dbHelper;
	private ContactArrayAdapter mContactAdapter;
	RetContactsObj fetcher = null;
	private EnhancedListView l;

	private AutoCompleteAdapter autoAdapter;
	private ArrayList<User> allUsers = new ArrayList<User>();
	private User addedUser;
	private List<Contact> contacts = new ArrayList<Contact>();
	private List<Contact> tempDeletedContacts = new ArrayList<Contact>();
	private Dialog dlg;
	EditText input;

	public ContactsFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_userlist, container, false);

		dbHelper = new SQLiteUserAdapter(getActivity());

		setUpAutoCompelete(v);
		Bundle args = getArguments();
		if (args != null && args.containsKey(Keys.Project.MEMBERS)) {
			List<UserParcel> members = args.getParcelableArrayList(Keys.Project.MEMBERS);
			for (UserParcel memberParcel : members) {
				contacts.add(new Contact(memberParcel.getData(), ""));
			}
			mContactAdapter.notifyDataSetChanged();
			mContactAdapter.getFilter().filter("");

		} else {
			setHasOptionsMenu(true);
			refresh(); // uses async-task
		}
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
			refresh();
			return true;
		case R.id.action_new:
			addContactsOption();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void addContactsOption() {
		UserVolleyAdapter.fetchAllUsers(new AsyncResponse<List<User>>() {
			@Override
			public void processFinish(List<User> result) {
				allUsers = new ArrayList<User>(result);
				addContactsOptionLoaded();
			}
		});
	}

	public void addContactsOptionLoaded() {
		dlg = new Dialog(getActivity());
		dlg.setTitle("Search by name or email:");
		View autocompleteView = getActivity().getLayoutInflater().inflate(R.layout.fragment_autocomplete, null);
		final ContactTokenTextView input1 = (ContactTokenTextView) autocompleteView.findViewById(R.id.my_autocomplete);
		autoAdapter = new AutoCompleteAdapter(getActivity(), allUsers);
		input1.setAdapter(autoAdapter);
		input1.setTokenListener(this);
		dlg.setContentView(autocompleteView);
		dlg.show();
	}

	@Override
	public void onTokenAdded(Object arg0) {
		SerializableUser added = null;
		if (arg0 instanceof SerializableUser) {
			added = (SerializableUser) arg0;
		} else if (arg0 instanceof User) {
			added = new SerializableUser((User) arg0);
		}

		if (added != null) {
			addedUser = added;
			dlg.dismiss();
			if (addedUser == null) {
				return;
			}
			boolean contains = false;
			for (int i = 0; i < contacts.size(); i++) {
				if (contacts.get(i).getContact().equals(addedUser)) {
					contains = true;
				}
			}
			if (contains) {
				AlertDialogUtil.displayDialog(getActivity(),"Unable to add contact","This user is already added as a contact", "OK", null);
				addedUser = null;
			} else {
				addContact(addedUser);
				addedUser = null;
			}
		}
	}

	@Override
	public void onTokenRemoved(Object arg0) {
		SerializableUser removed = null;
		if (arg0 instanceof SerializableUser) {
			removed = (SerializableUser) arg0;
		} else if (arg0 instanceof User) {
			removed = new SerializableUser((User) arg0);
		}
		if (removed != null) {
			addedUser = null;
		}

	}

	public void setContacts(List<Contact> tempContacts) {
		if (!tempContacts.isEmpty()) {
			contacts.clear();
			contacts.addAll(tempContacts);
			Collections.sort(contacts);

			for (int i = 0; i < tempDeletedContacts.size(); i++) {
				// why doesn't this work? cuz i need to make equals method in
				// contact
				// contacts.remove(tempDeletedContacts.get(i));
				for (int j = 0; j < contacts.size(); j++) {
					if (contacts.get(j).getContact().getID().equals(tempDeletedContacts.get(i).getContact().getID())) {
						contacts.remove(j);
						break;
					}
				}
			}
		}
		mContactAdapter.notifyDataSetChanged();
		input.setText("");
	}

	protected void addContact(User user) {
		AddContactTask adder = new AddContactTask(this);
		adder.addContact(user.getID());
		input.setText("");
	}

	protected void deleteContact(final Contact item) {
//		DeleteContactTask deleter = new DeleteContactTask(this);
//		deleter.deleteContact(item.getRelationID());
		new DeleteContactTask(new AsyncResponse<Boolean>(){
			@Override
			public void processFinish(Boolean result) {
				refresh();
				if (result) {
					NinjaToastUtil.show(getActivity(), item.getContact().getDisplayName() + " was removed as a contact");
				}
//				allUsers = new ArrayList<User>(result);
//				addContactsOptionLoaded();
			}
			
		}).execute(item.getRelationID());
		
		
		mContactAdapter.remove(item);
		mContactAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		dbHelper.close();
		super.onPause();
	}

	public void refresh() {
		SessionManager.getInstance();
		fetcher = new RetContactsObj();
		fetcher.execute(SessionManager.getUserID());
		// TODO: also remeve tempDeletedContacts
	}

	private void setUpListOnDismiss(View v) {
		l.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
			@Override
			public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {

				final Contact item = mContactAdapter.getItem(position);
				tempDeletedContacts.add(item);
				contacts.remove(item);
				mContactAdapter.remove(item);

				return new EnhancedListView.Undoable() {
					@Override
					public void undo() {
						contacts.add(item);
						tempDeletedContacts.remove(item);
						mContactAdapter.insert(item, position);
						mContactAdapter.notifyDataSetChanged();
					}

					@Override
					public String getTitle() {
						return "Member deleted";
					}

					@Override
					public void discard() {
						deleteContact(item);
						tempDeletedContacts.remove(item);

					}
				};
			}
		});
		l.setRequireTouchBeforeDismiss(false);
		l.setUndoHideDelay(5000);
		l.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				User clicked = mContactAdapter.getItem(position).getContact();
				Intent profileIntent = new Intent(v.getContext(),ProfileActivity.class);
				profileIntent.putExtra(Keys.User.PARCEL,new UserParcel(clicked));
				startActivity(profileIntent);
			}
		});
		l.enableSwipeToDismiss();
		l.setSwipingLayout(R.id.list_group_item_frame_1);

		l.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
	}

	private void setUpAutoCompelete(View v) {
		mContactAdapter = new ContactArrayAdapter(getActivity(),R.layout.list_item_user, contacts);

		l = (EnhancedListView) v.findViewById(R.id.contacts_list);
		l.setAdapter(mContactAdapter);
		l.setEmptyView(v.findViewById(android.R.id.empty));
		input = (EditText) v.findViewById(R.id.my_autocomplete);
		input.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				String text = input.getText().toString().toLowerCase();
				mContactAdapter.getFilter().filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
			}
		});
		setUpListOnDismiss(v);
	}

	final class RetContactsObj implements AsyncResponse<List<Contact>> {

		private GetContactsTask contactsFetcher;

		public RetContactsObj() {
			contactsFetcher = new GetContactsTask(this);
		}

		public void execute(String userID) {
			contactsFetcher.execute(userID);
		}

		@Override
		public void processFinish(List<Contact> result) {
			contacts.clear();
			contacts.addAll(result);
			for (int i = 0; i < tempDeletedContacts.size(); i++) {
				// why doesn't this work?
				// contacts.remove(tempDeletedContacts.get(i));
				for (int j = 0; j < contacts.size(); j++) {
					if (contacts.get(j).getContact().getID().equals(tempDeletedContacts.get(i).getContact().getID())) {
						contacts.remove(j);
						break;
					}
				}
			}
			mContactAdapter.notifyDataSetChanged();
			input.setText("");
//			mContactAdapter.getFilter().filter("");
		}
	}

}
