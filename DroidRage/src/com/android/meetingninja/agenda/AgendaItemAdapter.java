package com.android.meetingninja.agenda;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.meetingninja.R;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment;

import objects.Agenda;
import objects.Topic;
import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is a very simple adapter that provides very basic tree view with a
 * checkboxes and simple item description.
 * @param <T>
 * 
 */
public class AgendaItemAdapter<T> extends AbstractTreeViewAdapter<Topic> {
	private final String TAG = AgendaItemAdapter.class.getSimpleName();
	private Context mContext;
	private TreeBuilder<Topic> builder;
	private TreeStateManager<Topic> manager;
	private static int _topics = 0;
	private Agenda Agen = new Agenda();
	private final HashMap<Topic,Boolean> Comparison;

	// private void changeSelected(final boolean isChecked, final Long id) {
	// if (isChecked) {
	// selected.add(id);
	// } else {
	// selected.remove(id);
	// }
	// }

	public AgendaItemAdapter(final Context context,
			final TreeStateManager<Topic> treeStateManager,
			TreeBuilder<Topic> treeBuilder, final int numberOfLevels) {
		super((Activity) context, treeStateManager, numberOfLevels);
		this.mContext = context;
		this.builder = treeBuilder;
		this.manager = treeStateManager;
		_topics = manager.getVisibleCount();
		Comparison = new HashMap<Topic,Boolean>();
	}

	private Map<String, String> getDescription(final Topic topic) {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("title", topic.getTitle());
		values.put("levels",
				Arrays.asList(getManager().getHierarchyDescription(topic))
						.toString());

		int totalMins = Integer.valueOf(topic.getTime()); // in minutes
		int hrs = totalMins / 60; // get hours
		int mins = totalMins % 60;
		if (totalMins >= 60) {
			values.put("time", String.format(" (%dh %02dm)", hrs, mins));
		} else if (totalMins >= 0) {
			values.put("time", String.format(" (%02dm)", hrs, mins));
		}

		return values;
	}

	@Override
	public View getNewChildView(final TreeNodeInfo<Topic> treeNodeInfo) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final LinearLayout viewLayout = (LinearLayout) inflater.inflate(
				R.layout.list_item_agenda, null);
		View updated = updateView(viewLayout, treeNodeInfo);

		return updated;
	}

	
	public void addhash(Topic s){
		Comparison.put(s, true);
		
	}
	
	@Override
	public LinearLayout updateView(final View view,
			final TreeNodeInfo<Topic> treeNodeInfo) {
		
		final LinearLayout rowView = (LinearLayout) view;
		
		
//		LinearLayout rowView;
//		LayoutInflater inflater = (LayoutInflater) mContext
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		rowView = (LinearLayout) inflater.inflate(R.layout.list_item_agenda, null, false);

		final Topic rowTopic = treeNodeInfo.getId();
		
		if(rowTopic==null)
			return rowView; 
		
		System.out.println("Echo: Checked"+rowTopic);
		
		if(Comparison.get(rowTopic)== null)
			return rowView;
		
		if(Comparison.get(rowTopic).booleanValue()){
			Comparison.put(rowTopic, false);
			return rowView;
			
		}else{
			Comparison.put(rowTopic, true);
			
		}

		final EditText mTitle = (EditText) rowView
				.findViewById(R.id.agenda_edit_topic);

		
		final TextView mTime = (TextView) rowView
				.findViewById(R.id.agenda_topic_time);

		mTitle.setText(rowTopic.getTitle());
		
		System.out.println("Echo: Here"+ rowTopic+""+mTitle);
		
		mTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = s.toString();

				rowTopic.setTitle(text);
				mTitle.setTag(text);

				Log.d(TAG, "Text changed" + treeNodeInfo.getLevel() + " "
						+ treeNodeInfo.getId());
				manager.getChildren(treeNodeInfo.getId());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		final Button mAddTopicBtn = (Button) rowView
				.findViewById(R.id.agenda_subtopicAddBtn);
		final Button mTimeBtn = (Button) rowView
				.findViewById(R.id.agenda_topicTimeBtn);

		// Add SubTopic Button
		// mAddTopicBtn.setTag(rowTopic);
		mAddTopicBtn.setOnClickListener(new SubTopicListener(rowTopic));

		// Set Time Button
		mTimeBtn.setTag(rowTopic);
		mTimeBtn.setOnClickListener(new OnTimeBtnClick());

		Map<String, String> info = getDescription(rowTopic);
		// String title = "";
		// if (mTitle.getTag() != null) {
		// title = (String) mTitle.getTag();
		// Log.d(TAG + "using title >>", title);
		// } else {
		// title = info.containsKey("title") ? info.get("title") : "";
		// }
		// mTitle.setText(title);
		mTitle.setText(rowTopic.getTitle());
		String time = info.containsKey("time") ? info.get("time") : "";
		mTime.setText(time);

		// If a topic has subTopics, then its time is determined by the sum of
		// the subTopics
		if (treeNodeInfo.isWithChildren()) {
			// mTimeBtn.setVisibility(View.GONE);
			mTime.setVisibility(View.GONE);
		} else {
			mTimeBtn.setVisibility(View.VISIBLE);
			// timeBtn.setChecked(selected.contains(treeNodeInfo.getId()));
		}

		return rowView;
	}

	// @Override
	// public void handleItemClick(final View view, final Object id) {
	// final Topic t = (Topic) id;
	// final TreeNodeInfo<Topic> info = getManager().getNodeInfo(t);
	// if (info.isWithChildren()) {
	// super.handleItemClick(view, id);
	// } else {
	// final ViewGroup vg = (ViewGroup) view;
	//
	// }
	// }

	@Override
	public long getItemId(final int position) {

		return (long) position;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	private class OnTimeBtnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// HmsPickerBuilder hms = new HmsPickerBuilder().setFragmentManager(
			// ((FragmentActivity) getActivity())
			// .getSupportFragmentManager()).setStyleResId(
			// R.style.BetterPickersDialogFragment);
			// hms.show();
			Topic t = (Topic) v.getTag();
			Map<String, String> info = getDescription(t);
			Log.d(TAG, info.get("title"));

		}
	}

	private class SubTopicListener implements OnClickListener {
		private final Topic parent;

		public SubTopicListener(Topic parent) {
			this.parent = parent;
		}

		@Override
		public void onClick(View v) {
			// final Topic t = (Topic) v.getTag();
			Topic subT = new Topic(); // TODO : Make new subtopic
			subT.setTitle("New Topic");
			Agen.addTopic(subT);
			parent.addTopic(subT);
			System.out.println("Echo: Created"+subT);
			Comparison.put(subT, true);
			if (getManager().isInTree(parent))
				builder.addRelation(parent, subT);
			else {
				Log.wtf(TAG, "Topic is not in tree?");
			}
			// getManager().notifyDataSetChanged();
		}

	}
}