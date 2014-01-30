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
import objects.MockObjectFactory;
import objects.Topic;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AgendaDatabaseAdapter;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.JsonUtils;

public class AgendaActivity extends FragmentActivity {

	private static final String TAG = AgendaActivity.class.getSimpleName();
	private TreeViewList treeView;
	private TreeBuilder<Topic> treeBuilder = null;
	private TreeStateManager<Topic> manager = null;
	private AgendaItemAdapter mAgendaAdpt;

	private TextView mTitleView;
	private Button mAddTopicBtn;
	private Agenda displayedAgenda;
	private boolean collapsible;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean newCollapsible;
		setContentView(R.layout.activity_agenda);

		if (savedInstanceState == null) {
			manager = new InMemoryTreeStateManager<Topic>();
			newCollapsible = false;
			displayedAgenda = new Agenda();
		} else {
			manager = (TreeStateManager<Topic>) savedInstanceState
					.getSerializable("treeManager");
			newCollapsible = savedInstanceState.getBoolean("collapsible");
			displayedAgenda = savedInstanceState
					.getParcelable(Keys.Agenda.PARCEL);
		}

		setupViews();
		treeBuilder = new TreeBuilder<Topic>(manager);

		if (displayedAgenda != null) {
			int depth = displayedAgenda.getDepth();
			mAgendaAdpt = new AgendaItemAdapter(this, manager, treeBuilder,
					depth);
			mAgendaAdpt.addActivity(this);
			mTitleView.setText(displayedAgenda.getTitle());
			buildTree(treeBuilder);
		}
		treeView.setAdapter(mAgendaAdpt);

		setCollapsible(newCollapsible);

		// registerForContextMenu(treeView);
	}

	private void mockUp() {
		try {
			displayedAgenda = JsonUtils.getObjectMapper().readValue(
					MockObjectFactory.getMockAgenda(), Agenda.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupViews() {
		treeView = (TreeViewList) findViewById(R.id.agendaTreeView);
		mTitleView = (TextView) findViewById(R.id.agenda_title);
		mAddTopicBtn = (Button) findViewById(R.id.agenda_addTopicBtn);
		mAddTopicBtn.setOnClickListener(new TopicListener());

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
			mAgendaAdpt = new AgendaItemAdapter(this, manager, treeBuilder,
					depth);
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
		outState.putParcelable(Keys.Agenda.PARCEL, displayedAgenda);
	}

	protected final void setCollapsible(final boolean newCollapsible) {
		this.collapsible = newCollapsible;
		treeView.setCollapsible(this.collapsible);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_agenda, menu);
		return true;
	}

	private class TopicListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.agenda_addTopicBtn:
				Topic t = new Topic(); // TODO : Create a Topic
				t.setTitle("new topic");
				displayedAgenda.addTopic(t);
				reconstructTree();

				break;

			default:
				break;
			}

		}
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		final MenuItem collapsibleMenu = menu
				.findItem(R.id.collapsible_menu_item);
		if (collapsible) {
			collapsibleMenu.setTitle(R.string.collapsible_menu_disable);
			collapsibleMenu.setTitleCondensed(getResources().getString(
					R.string.collapsible_condensed_disable));
			collapsibleMenu.setChecked(collapsible);
		} else {
			collapsibleMenu.setTitle(R.string.collapsible_menu_enable);
			collapsibleMenu.setTitleCondensed(getResources().getString(
					R.string.collapsible_condensed_enable));
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle presses on the action bar items
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event

		// Handle other action bar items...
		switch (item.getItemId()) {

		case R.id.action_delete:
			Intent intent = getIntent();
			Boolean isCreated = intent.getBooleanExtra("isCreated", false);
			if (isCreated) {
				String agendaID = intent.getStringExtra("agendaID");
				AsyncTask<String, Void, Void> deleteTask = new DeleteAgendaTask();
				deleteTask.execute(agendaID);
			}
			finish();
			return true;
		case R.id.collapsible_menu_item:
			setCollapsible(!this.collapsible);
			break;
		case R.id.expand_all_menu_item:
			manager.expandEverythingBelow(null);
			break;
		case R.id.collapse_all_menu_item:
			manager.collapseChildren(null);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	// @Override
	// public void onCreateContextMenu(final ContextMenu menu, final View v,
	// final ContextMenuInfo menuInfo) {
	// final AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo)
	// menuInfo;
	// final long id = adapterInfo.id;
	// final TreeNodeInfo<Long> info = manager.getNodeInfo(id);
	// final MenuInflater menuInflater = getMenuInflater();
	// menuInflater.inflate(R.menu.context_menu, menu);
	// if (info.isWithChildren()) {
	// if (info.isExpanded()) {
	// menu.findItem(R.id.context_menu_expand_item).setVisible(false);
	// menu.findItem(R.id.context_menu_expand_all).setVisible(false);
	// } else {
	// menu.findItem(R.id.context_menu_collapse).setVisible(false);
	// }
	// } else {
	// menu.findItem(R.id.context_menu_expand_item).setVisible(false);
	// menu.findItem(R.id.context_menu_expand_all).setVisible(false);
	// menu.findItem(R.id.context_menu_collapse).setVisible(false);
	// }
	// super.onCreateContextMenu(menu, v, menuInfo);
	// }

	// @Override
	// public boolean onContextItemSelected(final MenuItem item) {
	// final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	// .getMenuInfo();
	// final long id = info.id;
	// if (item.getItemId() == R.id.context_menu_collapse) {
	// manager.collapseChildren(id);
	// return true;
	// } else if (item.getItemId() == R.id.context_menu_expand_all) {
	// manager.expandEverythingBelow(id);
	// return true;
	// } else if (item.getItemId() == R.id.context_menu_expand_item) {
	// manager.expandDirectChildren(id);
	// return true;
	// } else if (item.getItemId() == R.id.context_menu_delete) {
	// manager.removeNodeRecursively(id);
	// return true;
	// } else {
	// return super.onContextItemSelected(item);
	// }
	// }

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
