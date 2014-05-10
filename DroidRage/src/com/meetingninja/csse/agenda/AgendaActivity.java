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
import objects.Task;
import objects.Topic;
import objects.parcelable.AgendaParcel;
import objects.parcelable.ParcelDataFactory;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AgendaDatabaseAdapter;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.JsonUtils;
import com.meetingninja.csse.tasks.tasks.CreateTaskTask;

public class AgendaActivity extends FragmentActivity {

	private static final String TAG = AgendaActivity.class.getSimpleName();
	private TreeViewList treeView;
	private TreeBuilder<Topic> treeBuilder = null;
	private TreeStateManager<Topic> manager = null;
	private AgendaItemAdapter mAgendaAdpt;

	String testAgenda = "{" + "  \"agendaID\": \"-1\",               "
			+ "  \"title\": \"\",                  "
			+ "  \"meetingID\": \"3408\",              "
			+ "  \"userID\": \"3748\",             "
			+ "  \"content\": {                    "
			+ "    \"1\": {                        "
			+ "      \"title\": \"TOPIC 1\",       "
			+ "      \"time\": \"0\",              "
			+ "      \"description\": \"\",        "
			+ "      \"content\": []               "
			+ "    },                              "
			+ "    \"2\": {                        "
			+ "      \"title\": \"Topic 2\",       "
			+ "      \"time\": \"0\",              "
			+ "      \"description\": \"\",        "
			+ "      \"content\": {                "
			+ "        \"1\": {                    "
			+ "          \"title\": \"Topic 2.1\", "
			+ "          \"time\": \"0\",          "
			+ "          \"description\": \"\",    "
			+ "          \"content\": []           "
			+ "        },                          "
			+ "        \"2\": {                    "
			+ "          \"title\": \"Topic 2.2\", "
			+ "          \"time\": \"0\",          "
			+ "          \"description\": \"\",    "
			+ "          \"content\": []           "
			+ "        }                           "
			+ "      }                             "
			+ "    },                              "
			+ "    \"3\": {                        "
			+ "      \"title\": \"Topoc 3\",       "
			+ "      \"time\": \"0\",              "
			+ "      \"description\": \"\",        "
			+ "      \"content\": []               "
			+ "    },                              "
			+ "    \"4\": {                        "
			+ "      \"title\": \"Title\",         "
			+ "      \"time\": \"0\",              "
			+ "      \"description\": \"\",        "
			+ "      \"content\": {                "
			+ "        \"1\": {                    "
			+ "          \"title\": \"\",          "
			+ "          \"time\": \"0\",          "
			+ "          \"description\": \"\",    "
			+ "          \"content\": []           "
			+ "        }                           "
			+ "      }                             "
			+ "    }                               "
			+ "  }                                 " + "}";

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

		Bundle extras = getIntent().getExtras();

		if (savedInstanceState == null) { // e.g. fresh activity
			manager = new InMemoryTreeStateManager<Topic>();
			newCollapsible = false;
			if (extras != null) {
				displayedAgenda = new ParcelDataFactory(extras).getAgenda();
			}

			// loadAgendaMock();

		} else { // e.g. orientation change
			manager = (TreeStateManager<Topic>) savedInstanceState
					.getSerializable("treeManager");
			newCollapsible = savedInstanceState.getBoolean("collapsible");
			displayedAgenda = new ParcelDataFactory(savedInstanceState)
					.getAgenda();
		}

		setupViews();

		treeBuilder = new TreeBuilder<Topic>(manager);

		if (displayedAgenda != null) {
			Log.i(TAG, displayedAgenda.getID());
			mTitleView.setText(displayedAgenda.getTitle());
		} else {
			Log.i(TAG, "Creating a new Agenda");
			displayedAgenda = new Agenda();
			displayedAgenda.setID("-1");
		}

		mTitleView.setText(displayedAgenda.getTitle());
		int depth = displayedAgenda.getDepth();
		mAgendaAdpt = new AgendaItemAdapter(this, manager, treeBuilder, depth);
		buildTree(treeBuilder);
		treeView.setAdapter(mAgendaAdpt);
		setTreeViewAsCollapsible(newCollapsible);

	}

	private void loadAgendaMock() {
		try {
			displayedAgenda = AgendaDatabaseAdapter.parseAgenda(JsonUtils
					.getObjectMapper().readTree(testAgenda));
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setupViews() {
		treeView = (TreeViewList) findViewById(R.id.agendaTreeView);
		mTitleView = (TextView) findViewById(R.id.agenda_title_edittext);
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
			mTitleView.setText(displayedAgenda.getTitle());
			depth = displayedAgenda.getDepth();
			mAgendaAdpt = new AgendaItemAdapter(this, manager, treeBuilder,
					depth);
			treeView.setAdapter(mAgendaAdpt);
			buildTree(treeBuilder);
		}

	}

	public void removeTopicRecursively(Topic topic) {
		if (displayedAgenda.removeTopic(topic)) {
			manager.removeNodeRecursively(topic);
			manager.notifyDataSetChanged();
			reconstructTree();
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("treeManager", manager);
		outState.putBoolean("collapsible", this.collapsible);
		outState.putParcelable(Keys.Agenda.PARCEL, new AgendaParcel(
				displayedAgenda));
	}

	protected final void setTreeViewAsCollapsible(final boolean newCollapsible) {
		this.collapsible = newCollapsible;
		treeView.setCollapsible(this.collapsible);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_agenda, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

	private class TopicListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.agenda_addTopicBtn:
				Topic newTopic = new Topic();
				newTopic.setTitle("");

				final EditText mTitle = (EditText) ((View) v.getParent())
						.findViewById(R.id.agenda_title_edittext);
				System.out.println("FOUND:" + mTitle);
				displayedAgenda.setTitle(mTitle.getText().toString());

				displayedAgenda.addTopic(newTopic);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 6) {
			} else if (requestCode == 7) {
				Task t = new ParcelDataFactory(data.getExtras()).getTask();
				t.setCreatedBy(SessionManager.getUserID());
				CreateTaskTask creator = new CreateTaskTask(null);
				creator.createTask(t);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle presses on the action bar items
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event

		// Handle other action bar items...
		switch (item.getItemId()) {
		case android.R.id.home:

		case R.id.edit_agenda_action_save:
			AgendaSaveTask saver = new AgendaSaveTask(
					new AsyncResponse<String>() {
						@Override
						public void processFinish(String result) {
							Log.i(TAG + " Agenda ID", result);
							displayedAgenda.setID(result);
						}
					});
			saver.execute(displayedAgenda);

		case R.id.action_delete:
			Intent intent = getIntent();
			Boolean isCreated = intent.getBooleanExtra("isCreated", false);
			finish();
			return true;
		case R.id.collapsible_menu_item:
			setTreeViewAsCollapsible(!this.collapsible);
			break;
		case R.id.expand_all_menu_item:
			manager.expandEverythingBelow(null);
			break;
		case R.id.collapse_all_menu_item:
			manager.collapseChildren(null);
			break;
		case R.id.Review:
			viewAgenda(AgendaActivity.this, displayedAgenda);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void viewAgenda(Context context, Agenda viewAgenda) {
		Intent viewIntent = new Intent(context, ReviewAgendaActivity.class);
		viewIntent.putExtra(Keys.Agenda.PARCEL, new AgendaParcel(viewAgenda));
		startActivity(viewIntent);
	}

}
