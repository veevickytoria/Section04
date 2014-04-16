package com.meetingninja.csse.extras.examples;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meetingninja.csse.R;

import de.timroes.android.listview.EnhancedListView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link ExampleSwipeListFragment.OnFragmentInteractionListener} interface to
 * handle interaction events. Use the
 * {@link ExampleSwipeListFragment#newInstance} factory method to create an
 * instance of this fragment.
 * 
 */
@SuppressLint("ValidFragment")
public class ExampleSwipeListFragment extends Fragment {

	public static int MODE_PRIVATE = FragmentActivity.MODE_PRIVATE;
	private static final String PREF_UNDO_STYLE = "de.timroes.android.listviewdemo.UNDO_STYLE";
	private static final String PREF_SWIPE_TO_DISMISS = "de.timroes.android.listviewdemo.SWIPE_TO_DISMISS";
	private static final String PREF_SWIPE_DIRECTION = "de.timroes.android.listviewdemo.SWIPE_DIRECTION";
	private static final String PREF_SWIPE_LAYOUT = "de.timroes.android.listviewdemo.SWIPE_LAYOUT";

	private Context mContext;
	private EnhancedListView mListView;
	private EnhancedListAdapter mAdapter;

	private Bundle mUndoStylePref;
	private Bundle mSwipeDirectionPref;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @return A new instance of fragment SwipeListFragment.
	 */
	public static ExampleSwipeListFragment newInstance(Bundle args) {
		ExampleSwipeListFragment fragment = new ExampleSwipeListFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public ExampleSwipeListFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContext = getActivity();

		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_swipe_list, container,
				false);

		mListView = (EnhancedListView) v.findViewById(R.id.enhancedList);
		mAdapter = new EnhancedListAdapter();
		mAdapter.resetItems();

		mListView.setAdapter(mAdapter);

		// sets swipe to true
		getActivity().getPreferences(MODE_PRIVATE).edit()
				.putBoolean(PREF_SWIPE_TO_DISMISS, true).commit();
		// uses the swipe layout behind the list items
		getActivity().getPreferences(MODE_PRIVATE).edit()
				.putBoolean(PREF_SWIPE_LAYOUT, true).commit();
		// sets the swipe layout
		mListView.setSwipingLayout(R.id.swiping_layout);

		// Set the callback that handles dismisses.
		mListView
				.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
					/**
					 * This method will be called when the user swiped a way or
					 * deleted it via
					 * {@link de.timroes.android.listview.EnhancedListView#delete(int)}
					 * .
					 * 
					 * @param listView
					 *            The {@link EnhancedListView} the item has been
					 *            deleted from.
					 * @param position
					 *            The position of the item to delete from your
					 *            adapter.
					 * @return An
					 *         {@link de.timroes.android.listview.EnhancedListView.Undoable}
					 *         , if you want to give the user the possibility to
					 *         undo the deletion.
					 */
					@Override
					public EnhancedListView.Undoable onDismiss(
							EnhancedListView listView, final int position) {

						final String item = (String) mAdapter.getItem(position);
						mAdapter.remove(position);
						return new EnhancedListView.Undoable() {
							@Override
							public void undo() {
								mAdapter.insert(position, item);
							}
						};
					}
				});

		// Show toast message on click and long click on list items.
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(mContext,
						"Clicked on item " + mAdapter.getItem(position),
						Toast.LENGTH_SHORT).show();
			}
		});
		mListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						Toast.makeText(
								mContext,
								"Long clicked on item "
										+ mAdapter.getItem(position),
								Toast.LENGTH_SHORT).show();
						return true;
					}
				});

		applySettings();

		return v;
	}

	/**
	 * Applies the settings the user has made to the list view.
	 */
	private void applySettings() {

		SharedPreferences prefs = getActivity().getPreferences(MODE_PRIVATE);

		// Set the UndoStyle, the user selected.
		EnhancedListView.UndoStyle style;
		switch (prefs.getInt(PREF_UNDO_STYLE, 0)) {
		default:
			style = EnhancedListView.UndoStyle.SINGLE_POPUP;
			break;
		case 1:
			style = EnhancedListView.UndoStyle.MULTILEVEL_POPUP;
			break;
		case 2:
			style = EnhancedListView.UndoStyle.COLLAPSED_POPUP;
			break;
		}
		mListView.setUndoStyle(style);

		// Enable or disable Swipe to Dismiss
		if (prefs.getBoolean(PREF_SWIPE_TO_DISMISS, false)) {
			mListView.enableSwipeToDismiss();

			// Set the swipe direction
			EnhancedListView.SwipeDirection direction;
			switch (prefs.getInt(PREF_SWIPE_DIRECTION, 0)) {
			default:
				direction = EnhancedListView.SwipeDirection.BOTH;
				break;
			case 1:
				direction = EnhancedListView.SwipeDirection.START;
				break;
			case 2:
				direction = EnhancedListView.SwipeDirection.END;
				break;
			}
			mListView.setSwipeDirection(direction);

			// Enable or disable swiping layout feature
			mListView.setSwipingLayout(prefs.getBoolean(PREF_SWIPE_LAYOUT,
					false) ? R.id.swiping_layout : 0);

		} else {
			mListView.disableSwipeToDismiss();
		}

	}

	@Override
	public void onDetach() {
		if (mListView != null) {
			mListView.discardUndo();
		}
		super.onDetach();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			getArguments();
		}
	}

	public void resetItems(View view) {
		mListView.discardUndo();
		mAdapter.resetItems();
	}

	public void selectUndoStyle(View view) {
		DialogPicker picker = new DialogPicker();
		picker.setArguments(mUndoStylePref);
		picker.show(getActivity().getSupportFragmentManager(),
				"UNDO_STYLE_PICKER");
	}

	public void selectSwipeDirection(View view) {
		DialogPicker picker = new DialogPicker();
		picker.setArguments(mSwipeDirectionPref);
		picker.show(getActivity().getSupportFragmentManager(),
				"SWIPE_DIR_PICKER");
	}

	private class EnhancedListAdapter extends BaseAdapter {

		private List<String> mItems = new ArrayList<String>();

		void resetItems() {
			mItems.clear();
			for (int i = 1; i <= 40; i++) {
				mItems.add("Item " + i);
			}
			notifyDataSetChanged();
		}

		public void remove(int position) {
			mItems.remove(position);
			notifyDataSetChanged();
		}

		public void insert(int position, String item) {
			mItems.add(position, item);
			notifyDataSetChanged();
		}

		/**
		 * How many items are in the data set represented by this Adapter.
		 * 
		 * @return Count of items.
		 */
		@Override
		public int getCount() {
			return mItems.size();
		}

		/**
		 * Get the data item associated with the specified position in the data
		 * set.
		 * 
		 * @param position
		 *            Position of the item whose data we want within the
		 *            adapter's data set.
		 * @return The data at the specified position.
		 */
		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		/**
		 * Get the row id associated with the specified position in the list.
		 * 
		 * @param position
		 *            The position of the item within the adapter's data set
		 *            whose row id we want.
		 * @return The id of the item at the specified position.
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			TextView mTextView;
			int position;
		}

		ViewHolder holder;

		/**
		 * Get a View that displays the data at the specified position in the
		 * data set. You can either create a View manually or inflate it from an
		 * XML layout file. When the View is inflated, the parent View
		 * (GridView, ListView...) will apply default layout parameters unless
		 * you use
		 * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
		 * to specify a root view and to prevent attachment to the root.
		 * 
		 * @param position
		 *            The position of the item within the adapter's data set of
		 *            the item whose view we want.
		 * @param convertView
		 *            The old view to reuse, if possible. Note: You should check
		 *            that this view is non-null and of an appropriate type
		 *            before using. If it is not possible to convert this view
		 *            to display the correct data, this method can create a new
		 *            view. Heterogeneous lists can specify their number of view
		 *            types, so that this View is always of the right type (see
		 *            {@link #getViewTypeCount()} and
		 *            {@link #getItemViewType(int)}).
		 * @param parent
		 *            The parent that this view will eventually be attached to
		 * @return A View corresponding to the data at the specified position.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_swipable, parent, false);
				// Clicking the delete icon, will read the position of the item
				// stored in
				// the tag and delete it from the list. So we don't need to
				// generate a new
				// onClickListener every time the content of this view changes.
				final View origView = convertView;
				convertView.findViewById(R.id.action_delete)
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mListView.delete(((ViewHolder) origView
										.getTag()).position);
							}
						});

				holder = new ViewHolder();
				assert convertView != null;
				holder.mTextView = (TextView) convertView
						.findViewById(R.id.text);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.position = position;
			holder.mTextView.setText(mItems.get(position));

			return convertView;
		}
	}

	private class DialogPicker extends DialogFragment {

		final static String DIALOG_TITLE = "dialog_title";
		final static String DIALOG_ITEMS_ID = "items_id";
		final static String DIALOG_PREF_KEY = "pref_key";

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Bundle args = getArguments();

			// This is a fragment, so get the containing activity
			final FragmentActivity container = getActivity();

			AlertDialog.Builder builder = new AlertDialog.Builder(container);
			builder.setTitle(args.getInt(DIALOG_TITLE));
			builder.setSingleChoiceItems(
					args.getInt(DIALOG_ITEMS_ID),
					container.getPreferences(MODE_PRIVATE).getInt(
							args.getString(DIALOG_PREF_KEY), 0),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences prefs = container
									.getPreferences(MODE_PRIVATE);
							prefs.edit()
									.putInt(args.getString(DIALOG_PREF_KEY),
											which).commit();
							dialog.dismiss();
						}
					});

			return builder.create();
		}

	}
}
