package com.meetingninja.csse;

import java.util.ArrayList;
import java.util.List;

import objects.User;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.UserDatabaseAdapter;
import com.meetingninja.csse.extras.UsersCompletionView;
import com.meetingninja.csse.user.UserArrayAdapter;
import com.tokenautocomplete.FilteredArrayAdapter;
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
	private FilterUserArrayAdapter autoAdapter;
	private UserArrayAdapter addedAdapter;
	private List<User> allUsers = new ArrayList<User>();
	private List<User> addedUsers = new ArrayList<User>();
	private List<String> addedIds = new ArrayList<String>();
	private TextView mTxtID;

	// private SQLiteUserAdapter mySQLiteAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SearchableUserFragment() {
		// empty
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle icicle) {
		View view = inflater.inflate(R.layout.fragment_item_list, container,
				false);
		complete = (UsersCompletionView) view
				.findViewById(R.id.my_autocomplete);
		mTxtID = (TextView) view.findViewById(R.id.completed_ids);
		mListView = (AbsListView) view.findViewById(android.R.id.list);

		// token listener when autocompleted
		complete.setTokenListener(this);

		// mySQLiteAdapter = new SQLiteUserAdapter(getActivity());

		// mySQLiteAdapter.loadUsers();
		// allUsers = mySQLiteAdapter.getAllUsers();
		// mySQLiteAdapter.close();

		// TODO: store the users in the sqlite database
		if (allUsers.isEmpty())
			UserDatabaseAdapter.fetchAllUsers(this); // processFinish()

		autoAdapter = new FilterUserArrayAdapter(getActivity(),
				R.layout.chips_recipient_dropdown_item, allUsers);
		complete.setAdapter(autoAdapter);

		addedAdapter = new UserArrayAdapter(getActivity(),
				R.layout.list_item_user, addedUsers);

		((AdapterView<ListAdapter>) mListView).setAdapter(addedAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemClickListener(this);

		return view;
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
		User added;
		if (arg0 instanceof User) {
			added = (User) arg0;
			addedUsers.add(added);
			if (added.getID() != null) {
				addedIds.add(added.getID());
			} else {
				addedIds.add(added.getEmail());
			}
			mTxtID.setText(addedIds.toString());

			addedAdapter.notifyDataSetChanged();
		} else {
			System.out.println("Not added");
		}

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
		if (result instanceof List) {
			allUsers.clear();
			allUsers.addAll(result);
			autoAdapter.notifyDataSetChanged();
		}

	}

	private class FilterUserArrayAdapter extends FilteredArrayAdapter<User> {

		public FilterUserArrayAdapter(Context context, int resource,
				List<User> users) {
			super(context, resource, users);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {

				LayoutInflater l = (LayoutInflater) getContext()
						.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = (View) l.inflate(
						R.layout.chips_recipient_dropdown_item, parent, false);
			}

			User u = getItem(position);
			((TextView) convertView.findViewById(android.R.id.title)).setText(u
					.getDisplayName());
			((TextView) convertView.findViewById(android.R.id.text1)).setText(u
					.getEmail());
			// TODO : Get url's for user images
			SmartImageView img = (SmartImageView) convertView
					.findViewById(android.R.id.icon);
			img.setImageUrl("https://i.chzbgr.com/maxW500/6073452544/h4B353A81/");
			return convertView;
		}

		@Override
		protected boolean keepObject(User user, String mask) {
			mask = mask.toLowerCase();
			return user.getDisplayName().toLowerCase().startsWith(mask)
					|| user.getEmail().toLowerCase().startsWith(mask);
		}

	}

}
