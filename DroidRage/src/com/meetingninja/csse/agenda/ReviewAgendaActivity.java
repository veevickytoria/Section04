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

import java.io.IOException;
import java.util.ArrayList;

import objects.Agenda;
import objects.Topic;
import objects.parcelable.AgendaParcel;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AgendaDatabaseAdapter;
import com.meetingninja.csse.database.Keys;

public class ReviewAgendaActivity extends FragmentActivity {

	private static final String TAG = ReviewAgendaActivity.class
			.getSimpleName();
	private TreeViewList treeView;
	private TreeBuilder<Topic> treeBuilder = null;
	private TreeStateManager<Topic> manager = null;
	private ReviewAgendaItemAdapter mAgendaAdpt;

	private TextView mTitleView;
	private Button mAddTopicBtn;
	private Agenda displayedAgenda;
	private boolean collapsible;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean newCollapsible;
		setContentView(R.layout.activity_agenda_review);

		setupActionBar();

		if (savedInstanceState == null) {
			manager = new InMemoryTreeStateManager<Topic>();
			newCollapsible = false;
			displayedAgenda = ((AgendaParcel) getIntent()
					.getParcelableExtra(Keys.Agenda.PARCEL)).getAgenda();
		} else {
			manager = (TreeStateManager<Topic>) savedInstanceState
					.getSerializable("treeManager");
			newCollapsible = savedInstanceState.getBoolean("collapsible");
			displayedAgenda = ((AgendaParcel) savedInstanceState
					.getParcelable(Keys.Agenda.PARCEL)).getAgenda();
		}

		setupViews();
		treeBuilder = new TreeBuilder<Topic>(manager);

		if (displayedAgenda != null) {
			int depth = displayedAgenda.getDepth();
			mAgendaAdpt = new ReviewAgendaItemAdapter(this, manager,
					treeBuilder, depth);
			mAgendaAdpt.addActivity(this);
			buildTree(treeBuilder);
		}
		treeView.setAdapter(mAgendaAdpt);

		setCollapsible(newCollapsible);

		// registerForContextMenu(treeView);
	}

	private void setupViews() {
		treeView = (TreeViewList) findViewById(R.id.agendaTreeView);
		mTitleView = (TextView) findViewById(R.id.agenda_title_edittext);
		mAddTopicBtn = (Button) findViewById(R.id.agenda_addTopicBtn);

	}

	private void buildTree(final TreeBuilder<Topic> builder) {
		final ArrayList<Topic> topics = displayedAgenda.getTopics();
		for (Topic t : topics) {
			builder.addRelation(null, t);
			buildTreeHelper(builder, t);
		}
	}

	private void buildTreeHelper(final TreeBuilder<Topic> builder,
			final Topic root) {
		final ArrayList<Topic> topicList = root.getTopics();
		for (Topic sub_t : topicList) {
			builder.addRelation(root, sub_t);
			buildTreeHelper(builder, sub_t);
		}
	}

	public void reconstructTree() {
		manager.clear();
		mAgendaAdpt.refresh();

		int depth = 0;
		if (displayedAgenda != null) {
			depth = displayedAgenda.getDepth();
			mAgendaAdpt = new ReviewAgendaItemAdapter(this, manager,
					treeBuilder, depth);
			mAgendaAdpt.addActivity(this);
			mTitleView.setText(displayedAgenda.getTitle());
			buildTree(treeBuilder);
		}
		treeView.setAdapter(mAgendaAdpt);

	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("treeManager", manager);
		outState.putBoolean("collapsible", this.collapsible);
		outState.putParcelable(Keys.Agenda.PARCEL, new AgendaParcel(displayedAgenda));
	}

	protected final void setCollapsible(final boolean newCollapsible) {
		this.collapsible = newCollapsible;
		treeView.setCollapsible(this.collapsible);
	}

	private final View.OnClickListener mActionBarListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onActionBarItemSelected(v);

		}
	};

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Make an Ok/Cancel ActionBar
		View actionBarButtons = inflater.inflate(
				R.layout.actionbar_comment_accept, new LinearLayout(this),
				false);

		View cancelActionView = actionBarButtons
				.findViewById(R.id.action_comment);
		cancelActionView.setOnClickListener(mActionBarListener);

		View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		doneActionView.setOnClickListener(mActionBarListener);

		getActionBar().setHomeButtonEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(actionBarButtons);
		// end Ok-Cancel ActionBar
	}

	private boolean onActionBarItemSelected(View v) {
		switch (v.getId()) {
		case R.id.action_done:
			finish();
			break;
		case R.id.action_comment:
			comment();
			break;
		}

		return true;
	}

	private void comment() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Comment");
		alert.setMessage("Comment");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				finish();
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		alert.show();

	}

	/**
	 * Represents an asynchronous task used to delete the agenda
	 */
	public class DeleteAgendaTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			String AgendaID = params[0];
			try {
				AgendaDatabaseAdapter.deleteAgenda(AgendaID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}

	}

}
