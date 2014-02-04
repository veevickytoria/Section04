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
package com.meetingninja.csse.agenda;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment.HmsPickerDialogHandler;
import com.meetingninja.csse.R;

/**
 * This is a very simple adapter that provides very basic tree view with a
 * checkboxes and simple item description.
 * 
 * @param <T>
 * 
 */
public class ReviewAgendaItemAdapter extends AbstractTreeViewAdapter<Topic> {
	private final String TAG = ReviewAgendaItemAdapter.class.getSimpleName();

	private Context mContext;
	private TreeBuilder<Topic> builder;
	private TreeStateManager<Topic> manager;

	private static int _topics = 0;
	private final HashMap<Topic, Boolean> Comparison;
	private final HashMap<EditText, TextWatcher> TextHandlers;
	private boolean checked;
	private int counter;
	private ReviewAgendaActivity activty;

	// private void changeSelected(final boolean isChecked, final Long id) {
	// if (isChecked) {
	// selected.add(id);
	// } else {
	// selected.remove(id);
	// }
	// }

	public ReviewAgendaItemAdapter(final Context context,
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

	@Override
	public View getNewChildView(final TreeNodeInfo<Topic> treeNodeInfo) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final LinearLayout viewLayout = (LinearLayout) inflater.inflate(
				R.layout.list_item_agenda_review, null);
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

		final LinearLayout rowView = (LinearLayout) view;

		final Topic rowTopic = treeNodeInfo.getId();

		final TextView mTitle = (TextView) rowView
				.findViewById(R.id.agenda_edit_topic);

		mTitle.setText(rowTopic.getTitle() + "(" + rowTopic.getTime() + "m)");

		getManager().notifyDataSetChanged();
		return rowView;
	}

	@Override
	public long getItemId(final int position) {

		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	public void addActivity(ReviewAgendaActivity agendaActivity) {
		this.activty = agendaActivity;
	}
}
