package com.android.meetingninja.agenda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import objects.Agenda;
import objects.MockObjectFactory;
import objects.Topic;
import objects.User;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.R.anim;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.meetingninja.MainActivity;
import com.android.meetingninja.R;
import com.android.meetingninja.MainActivity.DrawerLabel;
import com.android.meetingninja.database.AgendaDatabaseAdapter;
import com.android.meetingninja.database.UserDatabaseAdapter;
import com.android.meetingninja.notes.CreateNoteActivity;
import com.android.meetingninja.user.LoginActivity;
import com.android.meetingninja.user.SessionManager;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AgendaActivity extends FragmentActivity {

	private static final String TAG = AgendaActivity.class.getSimpleName();
	private TreeViewList treeView;
	private TreeBuilder<Topic> treeBuilder = null;
	private TreeStateManager<Topic> manager = null;
	private AgendaItemAdapter mAgendaAdpt;
	
	private TextView mTitleView;
	private Button mAddTopicBtn;
	private Agenda mAgenda;
	private boolean collapsible;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean newCollapsible;
		ObjectMapper mapper = new ObjectMapper();

		// TODO : Get Agenda attached to meeting
		String json = "";
		try {
			json = MockObjectFactory.getMockAgenda();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			mAgenda = mapper.readValue(json, Agenda.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mAgenda = new Agenda();
		// End getAgenda 
		
		if (savedInstanceState == null) {
			manager = new InMemoryTreeStateManager<Topic>();
			newCollapsible = false;
		} else {
			manager = (TreeStateManager<Topic>) savedInstanceState
					.getSerializable("treeManager");
			newCollapsible = savedInstanceState.getBoolean("collapsible");
		}

		setContentView(R.layout.activity_agenda);
		setupViews();
		treeBuilder = new TreeBuilder<Topic>(manager);

		int depth = 0;
		if (mAgenda != null) {
			depth = mAgenda.getDepth();
			mAgendaAdpt = new AgendaItemAdapter(this, manager, treeBuilder,
					depth);
			mTitleView.setText(mAgenda.getTitle());
			buildTree(treeBuilder);
		}
		treeView.setAdapter(mAgendaAdpt);

		setCollapsible(newCollapsible);

		// registerForContextMenu(treeView);
	}

	private void setupViews() {
		treeView = (TreeViewList) findViewById(R.id.agendaTreeView);
		mTitleView = (TextView) findViewById(R.id.agenda_title);
		mAddTopicBtn = (Button) findViewById(R.id.agenda_addTopicBtn);
		mAddTopicBtn.setOnClickListener(new TopicListener());

	}

	private void buildTree(final TreeBuilder<Topic> builder) {
		final ArrayList<Topic> topics = mAgenda.getTopics();
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

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		outState.putSerializable("treeManager", manager);
		outState.putBoolean("collapsible", this.collapsible);
		super.onSaveInstanceState(outState);
	}

	protected final void setCollapsible(final boolean newCollapsible) {
		this.collapsible = newCollapsible;
		treeView.setCollapsible(this.collapsible);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.agenda_menu, menu);
		return true;
	}

	private class TopicListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.agenda_addTopicBtn:
				Topic t = new Topic(); // TODO : Create a Topic
				treeBuilder.addRelation(null, t);
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
