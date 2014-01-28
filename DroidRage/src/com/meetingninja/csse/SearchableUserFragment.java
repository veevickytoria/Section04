package com.meetingninja.csse;

import java.util.ArrayList;
import java.util.List;

import objects.SerializableUser;
import objects.User;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.extras.UsersCompletionView;
import com.meetingninja.csse.user.FilteredUserArrayAdapter;
import com.meetingninja.csse.user.UserArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView.TokenListener;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SearchableUserFragment extends Fragment implements
		AbsListView.OnItemClickListener, TokenListener,
		AsyncResponse<List<User>> {

	// private OnFragmentInteractionListener mListener;
	private UsersCompletionView complete;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private FilteredUserArrayAdapter autoAdapter;
	private UserArrayAdapter addedAdapter;
	private ArrayList<User> allUsers = new ArrayList<User>();
	private ArrayList<User> addedUsers = new ArrayList<User>();
	private ArrayList<String> addedIds = new ArrayList<String>();
	private TextView mTxtID;

	private final String TAG = SearchableUserFragment.class.getSimpleName();

	// private SQLiteUserAdapter mySQLiteAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SearchableUserFragment() {
		// empty
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "Saving " + allUsers.size() + " users");
		outState.putParcelableArrayList(Keys.User.LIST + "ALL", allUsers);
		outState.putParcelableArrayList(Keys.User.LIST + "ADDED", addedUsers);
		outState.putStringArrayList(Keys.User.LIST + Keys.User.ID, addedIds);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "OnCreate");
		if (savedInstanceState == null
				|| !savedInstanceState.containsKey(Keys.User.LIST + "ALL")) {
			Log.w(TAG, "Not Saved");
			UserDatabaseAdapter.fetchAllUsers(this); // processFinish()
		} else {
			Log.i(TAG, "Restoring");
			allUsers = savedInstanceState.getParcelableArrayList(Keys.User.LIST
					+ "ALL");
			addedUsers = savedInstanceState
					.getParcelableArrayList(Keys.User.LIST + "ADDED");
			addedIds = savedInstanceState.getStringArrayList(Keys.User.LIST
					+ Keys.User.ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle icicle) {
		Log.d(TAG, "OnCreateView");
		super.onCreateView(inflater, container, icicle);
		View view = inflater.inflate(R.layout.fragment_item_list, container,
				false);
		setupViews(view);

		// TODO: store the users in the sqlite database
		// mySQLiteAdapter = new SQLiteUserAdapter(getActivity());
		// mySQLiteAdapter.loadUsers();
		// allUsers = mySQLiteAdapter.getAllUsers();
		// mySQLiteAdapter.close();

		autoAdapter = new FilteredUserArrayAdapter(getActivity(),
				R.layout.chips_recipient_dropdown_item, allUsers);
		complete.setAdapter(autoAdapter);

		addedAdapter = new UserArrayAdapter(getActivity(),
				R.layout.list_item_user, addedUsers);
		((AdapterView<ListAdapter>) mListView).setAdapter(addedAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
	}

	private void setupViews(View view) {
		complete = (UsersCompletionView) view
				.findViewById(R.id.my_autocomplete);
		// token listener when autocompleted
		complete.setTokenListener(this);
		mTxtID = (TextView) view.findViewById(R.id.completed_ids);
		mListView = (AbsListView) view.findViewById(android.R.id.list);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// mListener = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// if (null != mListener) {
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		// mListener.onFragmentInteraction("");
		// }
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}

	@Override
	public void onTokenAdded(Object arg0) {
		String className = arg0.getClass().getSimpleName();
		System.out.println("Adding a " + className);

		User added = (User) arg0;
		SerializableUser serialized = null;
		if (arg0 instanceof User)
			serialized = added.toSimpleUser();
		else if (arg0 instanceof SerializableUser)
			serialized = (SerializableUser) arg0;

		if (serialized != null) {
			addedUsers.add(serialized);
			if (serialized.getID() != null) {
				addedIds.add(serialized.getID());
			} else {
				addedIds.add(serialized.getEmail());
			}
		}
		addedAdapter.notifyDataSetChanged();

		mTxtID.setText(addedIds.toString());

	}

	@Override
	public void onTokenRemoved(Object arg0) {
		User removed;
		if (arg0 instanceof User) {
			removed = (User) arg0;
			if (removed.getID() != null) {
				addedIds.remove(removed.getID());
			} else {
				addedIds.remove(removed.getEmail());
			}
			addedUsers.remove(removed);
			mTxtID.setText(addedIds.toString());
			addedAdapter.notifyDataSetChanged();
		} else {
			System.out.println("Not removed");
		}

	}

	@Override
	public void processFinish(List<User> result) {
		allUsers.clear();
		allUsers.addAll(result);
		autoAdapter.notifyDataSetChanged();
	}

}
