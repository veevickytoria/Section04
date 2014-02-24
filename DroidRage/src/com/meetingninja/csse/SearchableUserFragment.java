package com.meetingninja.csse;

import java.util.ArrayList;
import java.util.List;

import objects.SerializableUser;
import objects.User;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.database.volley.UserVolleyAdapter;
import com.meetingninja.csse.extras.BaseFragment;
import com.meetingninja.csse.extras.ContactTokenTextView;
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

	private ContactTokenTextView complete;
	private AutoCompleteAdapter autoAdapter;
	private TextView addedIDsView;
	private UserArrayAdapter addedAdapter;

	private ArrayList<User> allUsers = new ArrayList<User>();
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

		UserVolleyAdapter.fetchAllUsers(this);

		// SQLiteUserAdapter sqlite = new SQLiteUserAdapter(mContext);
		// allUsers = sqlite.getAllUsers(); println(allUsers);
		// sqlite.close();

		autoAdapter = new AutoCompleteAdapter(getActivity(), allUsers);

		complete = (ContactTokenTextView) view
				.findViewById(R.id.my_autocomplete);
		complete.setAdapter(autoAdapter);
		complete.setTokenListener(this);

		// token listener when autocompleted
		addedIDsView = (TextView) view.findViewById(R.id.completed_ids);

		addedAdapter = new UserArrayAdapter(getActivity(),
				R.layout.list_item_user, addedUsers);
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(addedAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		savedState = saveState();
		createdStateInDestroyView = true;
		allUsers.clear();
	}

	private Bundle saveState() {
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		addedUsers.clear();
		addedIds.clear();
		addedAdapter.notifyDataSetChanged();
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
		// String className = arg0.getClass().getSimpleName();
		// System.out.println("Adding a " + className);

		SerializableUser added = null;
		if (arg0 instanceof SerializableUser)
			added = (SerializableUser) arg0;
		else if (arg0 instanceof User)
			added = new SerializableUser((User) arg0);

		if (added != null) {
			addedUsers.add(added);
			if (!TextUtils.equals(added.getID(), "")) {
				addedIds.add(added.getID());
			} else {
				addedIds.add(added.getEmail());
			}
		}
		addedAdapter.notifyDataSetChanged();

		addedIDsView.setText(addedIds.toString());

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
			if (!TextUtils.equals(removed.getID(), "")) {
				addedIds.remove(removed.getID());
			} else {
				addedIds.remove(removed.getEmail());
			}
			addedIDsView.setText(addedIds.toString());
			addedAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void processFinish(List<User> result) {
		allUsers.clear();
		autoAdapter.clear();
		allUsers.addAll(result);
		autoAdapter.notifyDataSetChanged();
	}

}
