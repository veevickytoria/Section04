package com.android.meetingninja.agenda;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import objects.Agenda;
import objects.MockObjectFactory;
import objects.Topic;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.meetingninja.R;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AgendaActivity extends FragmentActivity {

	private final Set<Long> selected = new HashSet<Long>();

	private static final String TAG = AgendaActivity.class.getSimpleName();
	private TextView mTitleView;
	private Button mAddTopicBtn;
	private TreeViewList treeView;
	private TreeBuilder<Topic> treeBuilder = null;
	private TreeStateManager<Topic> manager = null;
	private AgendaItemAdapter mAgendaAdpt;
	private boolean collapsible;
	private Agenda mAgenda;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean newCollapsible;

		String json = "";
		try {
			json = MockObjectFactory.getMockAgenda();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();

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

		if (savedInstanceState == null) {
			manager = new InMemoryTreeStateManager<Topic>();
			treeBuilder = new TreeBuilder<Topic>(
					manager);

			buildTree(treeBuilder);

			Log.d(TAG, manager.toString());
			// newTreeType = TreeType.SIMPLE;
			newCollapsible = true;
		} else {
			manager = (TreeStateManager<Topic>) savedInstanceState
					.getSerializable("treeManager");
			newCollapsible = savedInstanceState.getBoolean("collapsible");
		}

		setContentView(R.layout.activity_agenda);
		setupViews();

		int depth = 0;
		if (mAgenda != null) {
			mAgendaAdpt = new AgendaItemAdapter(this, mAgenda, manager, depth);
			mTitleView.setText(mAgenda.getTitle());
			depth = mAgenda.getDepth();
			// checkEmpty();

		}
		treeView.setAdapter(mAgendaAdpt);

		setCollapsible(newCollapsible);

		registerForContextMenu(treeView);
	}

	// private void checkEmpty() {
	// if (mAgenda.getTitle().isEmpty() || mAgenda.getTopics().size() <= 0) {
	// findViewById(R.id.content_frame).setVisibility(View.GONE);
	// findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
	// }
	//
	// }

	private void setupViews() {
		treeView = (TreeViewList) findViewById(R.id.agendaTreeView);
		mTitleView = (TextView) findViewById(R.id.agenda_title);
		mAddTopicBtn = (Button) findViewById(R.id.agenda_addTopicBtn);
		mAddTopicBtn.setOnClickListener(new AddTopicListener());

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	private void buildTree(final TreeBuilder<Topic> builder) {
		Topic t = null;
		for (Iterator<Topic> i = mAgenda.getTopics().iterator(); i.hasNext();) {
			t = i.next();
			builder.addRelation(null, t);
			buildTreeHelper(builder, t);
		}
	}

	private void buildTreeHelper(final TreeBuilder<Topic> builder,
			final Topic root) {
		Topic sub_t = null;
		final ArrayList<Topic> topicList = root.getTopics();
		for (Iterator<Topic> i = topicList.iterator(); i.hasNext();) {
			sub_t = i.next();
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
		getMenuInflater().inflate(R.menu.agenda, menu);
		return true;
	}
	
	private class AddTopicListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.agenda_addTopicBtn:
				Topic t = new Topic();
				t.setTime(120);
				treeBuilder.addRelation(null, t);
				break;

			default:
				break;
			}
			
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(final Menu menu) {
	// final MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.main_menu, menu);
	// return true;
	// }

	// @Override
	// public boolean onPrepareOptionsMenu(final Menu menu) {
	// final MenuItem collapsibleMenu = menu
	// .findItem(R.id.collapsible_menu_item);
	// if (collapsible) {
	// collapsibleMenu.setTitle(R.string.collapsible_menu_disable);
	// collapsibleMenu.setTitleCondensed(getResources().getString(
	// R.string.collapsible_condensed_disable));
	// } else {
	// collapsibleMenu.setTitle(R.string.collapsible_menu_enable);
	// collapsibleMenu.setTitleCondensed(getResources().getString(
	// R.string.collapsible_condensed_enable));
	// }
	// return super.onPrepareOptionsMenu(menu);
	// }

	// @Override
	// public boolean onOptionsItemSelected(final MenuItem item) {
	// if (item.getItemId() == R.id.simple_menu_item) {
	// setTreeAdapter(TreeType.SIMPLE);
	// } else if (item.getItemId() == R.id.fancy_menu_item) {
	// setTreeAdapter(TreeType.FANCY);
	// } else if (item.getItemId() == R.id.collapsible_menu_item) {
	// setCollapsible(!this.collapsible);
	// } else if (item.getItemId() == R.id.expand_all_menu_item) {
	// manager.expandEverythingBelow(null);
	// } else if (item.getItemId() == R.id.collapse_all_menu_item) {
	// manager.collapseChildren(null);
	// } else {
	// return false;
	// }
	// return true;
	// }

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

}
