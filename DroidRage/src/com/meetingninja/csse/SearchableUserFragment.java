package com.meetingninja.csse;

import java.util.ArrayList;
import java.util.List;

import objects.SerializableUser;
import objects.User;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.BaseFragment;
import com.meetingninja.csse.extras.UsersCompletionView;
import com.meetingninja.csse.user.AutoCompleteAdapter;
import com.meetingninja.csse.user.AutoCompleteAdapter;
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
public class SearchableUserFragment extends BaseFragment implements
		AbsListView.OnItemClickListener, AsyncResponse<List<User>>,
		TokenListener {

	// private OnFragmentInteractionListener mListener;
	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	private Context mContext;

	private UsersCompletionView complete;
	private AutoCompleteAdapter autoAdapter;
	private TextView addedIDsView;
	private UserArrayAdapter addedAdapter;

	private List<SerializableUser> allUsers = new ArrayList<SerializableUser>();
	private ArrayList<User> addedUsers = new ArrayList<User>();
	private ArrayList<String> addedIds = new ArrayList<String>();

	private Bundle savedState = null;
	private static final String SAVED_BUNDLE_TAG = "saved_bundle";

	private final String TAG = SearchableUserFragment.class.getSimpleName();

	// private SQLiteUserAdapter mySQLiteAdapter;

	private boolean createdStateInDestroyView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SearchableUserFragment() {
		// empty
	}

	private static SearchableUserFragment sInstance;

	public static SearchableUserFragment getInstance() {
		if (sInstance == null) {
			sInstance = new SearchableUserFragment();
		}
		return sInstance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
			for (String s : savedInstanceState.keySet()) {
				System.out.println(s + ": " + savedInstanceState.get(s));
			}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_autocomplete, container,
				false);
		setupViews(view);

		// if (savedInstanceState != null && savedState == null) {
		// savedState = savedInstanceState.getBundle(SAVED_BUNDLE_TAG);
		// for (String s : savedInstanceState.keySet()) {
		// System.out.println(s + ": " + savedInstanceState.get(s));
		// }
		// }
		// if (savedState != null)
		// allUsers = savedState
		// .getParcelableArrayList(Keys.User.LIST + "ALL");
		// savedState = null;

		// if (savedInstanceState == null
		// || !savedInstanceState.containsKey(Keys.User.LIST + "ALL")) {
		// Log.w(TAG, "Not Saved");
		// UserDatabaseAdapter.fetchAllUsers(this); // processFinish()
		// } else {
		// Log.i(TAG, "Restoring");
		// allUsers = savedInstanceState.getParcelableArrayList(Keys.User.LIST
		// + "ALL");
		// addedUsers = savedInstanceState
		// .getParcelableArrayList(Keys.User.LIST + "ADDED");
		// addedIds = savedInstanceState.getStringArrayList(Keys.User.LIST
		// + Keys.User.ID);
		// }

		// TODO: store the users in the sqlite database
		// mySQLiteAdapter = new SQLiteUserAdapter(getActivity());
		// mySQLiteAdapter.loadUsers();
		// allUsers = mySQLiteAdapter.getAllUsers();
		// mySQLiteAdapter.close();

		UserVolleyAdapter.fetchAllUsers(this);

		autoAdapter = new AutoCompleteAdapter(getActivity(),
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
		addedIDsView = (TextView) view.findViewById(R.id.completed_ids);
		mListView = (AbsListView) view.findViewById(android.R.id.list);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		savedState = saveState(); /* allUsers defined here for sure */
		createdStateInDestroyView = true;
		allUsers.clear();
	}

	private Bundle saveState() {
		Log.d(TAG, "Save " + allUsers.size() + " users");
		Bundle state = new Bundle();
		// state.putParcelableArrayList(Keys.User.LIST + "ALL", allUsers);
		state.putParcelableArrayList(Keys.User.LIST + "ADDED", addedUsers);
		state.putStringArrayList(Keys.User.LIST + Keys.User.ID, addedIds);
		return state;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (allUsers.isEmpty()) {
			outState.putBundle(SAVED_BUNDLE_TAG, savedState);
		} else {
			outState.putBundle(SAVED_BUNDLE_TAG,
					createdStateInDestroyView ? savedState : saveState());
		}
		createdStateInDestroyView = false;
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// mListener = null;
		mContext = null;
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

		SerializableUser serialized = null;
		if (arg0 instanceof SerializableUser)
			serialized = (SerializableUser) arg0;
		else if (arg0 instanceof User)
			serialized = new SerializableUser((User) arg0);

		if (serialized != null) {
			addedUsers.add(serialized);
			if (serialized.getID() != null) {
				addedIds.add(serialized.getID());
			} else {
				addedIds.add(serialized.getEmail());
			}
		}
		addedAdapter.notifyDataSetChanged();

		addedIDsView.setText(addedIds.toString());

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
			addedIDsView.setText(addedIds.toString());
			addedAdapter.notifyDataSetChanged();
		} else {
			System.out.println("Not removed");
		}

	}

	@Override
	public void processFinish(List<User> result) {
		allUsers.clear();
		autoAdapter.clear();
		for (User user : result) {
			allUsers.add(new SerializableUser(user));
		}
		autoAdapter.notifyDataSetChanged();
	}

}
