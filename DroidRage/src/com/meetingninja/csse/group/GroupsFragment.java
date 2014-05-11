package com.meetingninja.csse.group;

import java.util.ArrayList;
import java.util.List;

import objects.Group;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.ConnectivityUtils;
import com.meetingninja.csse.group.tasks.GroupCreateTask;
import com.meetingninja.csse.group.tasks.GroupDeleteTask;
import com.meetingninja.csse.group.tasks.GroupFetcherTask;

public class GroupsFragment extends Fragment implements AsyncResponse<List<Group>> {
	private ListView groupsList;
	private static List<Group> groups = new ArrayList<Group>();;
	private GroupItemAdapter groupAdpt;
	private GroupFetcherTask fetcher;

	public GroupsFragment() {
		// Empty
	}

	private static GroupsFragment sInstance;

	public static GroupsFragment getInstance() {
		if (sInstance == null) {
			sInstance = new GroupsFragment();
		}
		return sInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_groups, container, false);
		setHasOptionsMenu(true);

		SessionManager.getInstance();
		groupsList = (ListView) v.findViewById(R.id.group_list);
		groupsList.setEmptyView(v.findViewById(android.R.id.empty));
		groupAdpt = new GroupItemAdapter(getActivity(),R.layout.list_item_group, groups);
		groupsList.setAdapter(groupAdpt);

		fetchGroups();

		groupAdpt.notifyDataSetChanged();
		groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentAdapter,View v, int position, long id) {
				Group clicked = groupAdpt.getItem(position);
				viewGroup(clicked);
			}
		});
		registerForContextMenu(groupsList);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_new_and_refresh, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			fetchGroups();
			return true;
		case R.id.action_new:
			Intent i = new Intent(getActivity(), EditGroupActivity.class);
			Group g = new Group();
			i.putExtra(Keys.Group.PARCEL, g);
			startActivityForResult(i, 7);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 7) {
				Group g = data.getParcelableExtra(Keys.Group.PARCEL);
				groups.add(g);
				GroupCreateTask creator = new GroupCreateTask(this);
				creator.createGroup(g);
			} else if (requestCode == 8) {
				fetchGroups();
			}
			groupAdpt.notifyDataSetChanged();
			return;
		}
		fetchGroups();
	}

	public void fetchGroups() {
		if (ConnectivityUtils.isConnected(getActivity()) && isAdded()) {

			fetcher = new GroupFetcherTask(this);
			fetcher.execute(SessionManager.getUserID()); // calls processFinish()
		}
	}

	private void viewGroup(Group group) {
		Intent i = new Intent(getActivity(), ViewGroupActivity.class);
		i.putExtra(Keys.Group.PARCEL, group);
		startActivityForResult(i, 8);
	}

	public void deleteGroup(String groupID) {
		GroupDeleteTask deltask = new GroupDeleteTask();
		deltask.deleteGroup(groupID);
	}

	@Override
	public void processFinish(List<Group> result) {
		groups.clear();
		groupAdpt.clear();
		groups.addAll(result);
		groupAdpt.notifyDataSetChanged();
	}

	public void notifyAdapter() {
		groupAdpt.notifyDataSetChanged();
	}

}
