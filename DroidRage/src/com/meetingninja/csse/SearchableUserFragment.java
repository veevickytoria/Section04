package com.meetingninja.csse;

import java.util.ArrayList;
import java.util.List;

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
import com.meetingninja.csse.user.FilteredUserArrayAdapter;
import com.meetingninja.csse.user.UserArrayAdapter;

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
		AbsListView.OnItemClickListener, AsyncResponse<List<User>> {

	private static SearchableUserFragment sInstance;
	// private OnFragmentInteractionListener mListener;
	private ContactsCompletionView complete;
	private RecipientEditTextView ex_complete;

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
//	private TextView addedIDsView;
	private Bundle savedState = null;
	private static final String SAVED_BUNDLE_TAG = "saved_bundle";
	private Context mContext;

	private final String TAG = SearchableUserFragment.class.getSimpleName();

	// private SQLiteUserAdapter mySQLiteAdapter;

	SimpleUser[] people;
	ArrayAdapter<SimpleUser> adapter;
	ContactsCompletionView completionView;

	private boolean createdStateInDestroyView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SearchableUserFragment() {
		// empty
	}
	
	public static SearchableUserFragment getInstance() {
		if (sInstance == null) {
			sInstance = new SearchableUserFragment();
		}
		return sInstance;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null)
			for (String s : savedInstanceState.keySet()) {
				System.out.println(s + ": " + savedInstanceState.get(s));
			}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_autocomplete, container,
				false);
		setupViews(view);
		if (savedInstanceState == null) {
			UserVolleyAdapter.fetchAllUsers(this); // processFinish()
		}

		if (savedInstanceState != null && savedState == null)
			savedState = savedInstanceState.getBundle(SAVED_BUNDLE_TAG);
		if (savedState != null)
			allUsers = savedState
					.getParcelableArrayList(Keys.User.LIST + "ALL");
		savedState = null;

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

		people = new SimpleUser[] {
				new SimpleUser("Marshall Weir", "marshall@example.com"),
				new SimpleUser("Margaret Smith", "margaret@example.com"),
				new SimpleUser("Max Jordan", "max@example.com"),
				new SimpleUser("Meg Peterson", "meg@example.com"),
				new SimpleUser("Amanda Johnson", "amanda@example.com"),
				new SimpleUser("Terry Anderson", "terry@example.com") };

		// TODO: store the users in the sqlite database
		// mySQLiteAdapter = new SQLiteUserAdapter(getActivity());
		// mySQLiteAdapter.loadUsers();
		// allUsers = mySQLiteAdapter.getAllUsers();
		// mySQLiteAdapter.close();

		adapter = new ArrayAdapter<SimpleUser>(getActivity(),
				android.R.layout.simple_list_item_1, people);

		complete.setAdapter(adapter);
		
		 final RecipientEditTextView emailRetv =
	                (RecipientEditTextView) view.findViewById(R.id.ex_autocomplete);
	        emailRetv.setTokenizer(new Rfc822Tokenizer());
	        emailRetv.setAdapter(new BaseRecipientAdapter(getActivity()) { });

		autoAdapter = new FilteredUserArrayAdapter(getActivity(),
				R.layout.chips_recipient_dropdown_item, allUsers);
		// complete.setAdapter(autoAdapter);

		addedAdapter = new UserArrayAdapter(getActivity(),
				R.layout.list_item_user, addedUsers);
		((AdapterView<ListAdapter>) mListView).setAdapter(addedAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
	}

	private void setupViews(View view) {
		complete = (ContactsCompletionView) view
				.findViewById(R.id.my_autocomplete);
		// token listener when autocompleted
		// complete.setTokenListener(this);
//		addedIDsView = (TextView) view.findViewById(R.id.completed_ids);
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
		state.putParcelableArrayList(Keys.User.LIST + "ALL", allUsers);
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

	// @Override
	// public void onTokenAdded(Object arg0) {
	// String className = arg0.getClass().getSimpleName();
	// System.out.println("Adding a " + className);
	//
	// User added = (User) arg0;
	// SerializableUser serialized = null;
	// if (arg0 instanceof User)
	// serialized = added.toSimpleUser();
	// else if (arg0 instanceof SerializableUser)
	// serialized = (SerializableUser) arg0;
	//
	// if (serialized != null) {
	// addedUsers.add(serialized);
	// if (serialized.getID() != null) {
	// addedIds.add(serialized.getID());
	// } else {
	// addedIds.add(serialized.getEmail());
	// }
	// }
	// addedAdapter.notifyDataSetChanged();
	//
	// mTxtID.setText(addedIds.toString());
	//
	// }
	//
	// @Override
	// public void onTokenRemoved(Object arg0) {
	// User removed;
	// if (arg0 instanceof User) {
	// removed = (User) arg0;
	// if (removed.getID() != null) {
	// addedIds.remove(removed.getID());
	// } else {
	// addedIds.remove(removed.getEmail());
	// }
	// addedUsers.remove(removed);
	// mTxtID.setText(addedIds.toString());
	// addedAdapter.notifyDataSetChanged();
	// } else {
	// System.out.println("Not removed");
	// }
	//
	// }

	@Override
	public void processFinish(List<User> result) {
		allUsers.clear();
		autoAdapter.clear();
		autoAdapter.notifyDataSetChanged();
	}

}
