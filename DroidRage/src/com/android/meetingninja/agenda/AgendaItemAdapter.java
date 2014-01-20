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
package com.android.meetingninja.agenda;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import objects.Agenda;
import objects.Topic;
import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.meetingninja.R;

/**
 * This is a very simple adapter that provides very basic tree view with a
 * checkboxes and simple item description.
 * 
 * @param <T>
 * 
 */
public class AgendaItemAdapter<T> extends AbstractTreeViewAdapter<Topic> {
	private final String TAG = AgendaItemAdapter.class.getSimpleName();
	private Context mContext;
	private TreeBuilder<Topic> builder;
	private TreeStateManager<Topic> manager;
	private static int _topics = 0;
	private final HashMap<Topic, Boolean> Comparison;
	private final HashMap<EditText, TextWatcher> TextHandlers;
	private boolean checked;
	private int counter;
	private AgendaActivity activty;

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
		Comparison = new HashMap<Topic, Boolean>();
		TextHandlers = new HashMap<EditText, TextWatcher>();
		checked = false;
		counter = 0;
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

	public void addhash(Topic s) {
		Comparison.put(s, true);
		checked = true;
		counter = 0;
	}


	@Override
	public LinearLayout updateView(final View view,
			final TreeNodeInfo<Topic> treeNodeInfo) {

//		final LinearLayout rowView = (LinearLayout) view;

		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
			      (Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout rowView = (LinearLayout) inflater.inflate(
				R.layout.list_item_agenda, null);


		final Topic rowTopic = treeNodeInfo.getId();

		System.out.println("Echo: Checked" + rowTopic+" "+counter+" "+Comparison.size()+" "+checked);

		final EditText mTitle = (EditText) rowView
				.findViewById(R.id.agenda_edit_topic);

		final TextView mTime = (TextView) rowView
				.findViewById(R.id.agenda_topic_time);

		mTitle.setText(rowTopic.getTitle());

		System.out.println("Echo: Here" + rowTopic.getTitle()+" "+rowView);
		
		if(TextHandlers.containsKey(mTitle)){
			mTitle.removeTextChangedListener(TextHandlers.get(mTitle));
		}
				
		TextWatcher c = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = s.toString();

//				rowTopic.setTitle(text);
				mTitle.setTag(text);
				rowTopic.setTitle(text);

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
		};
		
		mTitle.addTextChangedListener(c);
		TextHandlers.put(mTitle, c);
		
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
//		mTitle.setText(rowTopic.getTitle());
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
			subT.setTitle(parent.getTitle()+"New Topic");
			parent.addTopic(subT);
			System.out.println("Echo: Created" + subT+" "+parent);

			activty.reconstructTree();
			// getManager().notifyDataSetChanged();
		}

	}

	public void addActivity(AgendaActivity agendaActivity) {
		this.activty = agendaActivity;
	}
}
