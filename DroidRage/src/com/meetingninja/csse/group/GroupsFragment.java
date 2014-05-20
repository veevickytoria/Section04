package com.meetingninja.csse.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import objects.Group;
import objects.Meeting;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.meetingninja.csse.MainActivity;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.ConnectivityUtils;
import com.meetingninja.csse.extras.IRefreshable;
import com.meetingninja.csse.group.tasks.AsyncGroupDeleteTask;
import com.meetingninja.csse.group.tasks.GroupCreateTask;
import com.meetingninja.csse.group.tasks.GroupFetcherTask;
import com.meetingninja.csse.group.tasks.GroupUpdaterTask;
import com.meetingninja.csse.meetings.MeetingsFragment;

public class GroupsFragment extends Fragment implements AsyncResponse<List<Group>>, IRefreshable {
	private static final String TAG = GroupsFragment.class.getSimpleName();
	private ListView groupsList;
	private static List<Group> groups = new ArrayList<Group>();
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

		groupsList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
				AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;

				Group longClicked = groupAdpt.getItem(aInfo.position);

				menu.setHeaderTitle("Options for "+ longClicked.getTitle());
				menu.add(MainActivity.DrawerLabel.GROUPS.getPosition(),aInfo.position, 1, "Edit");
				menu.add(MainActivity.DrawerLabel.GROUPS.getPosition(),aInfo.position, 2, "Delete");
			}
		});
		return v;
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int position = item.getItemId();
		boolean handled = false;
		item.getMenuInfo();
		if (item.getGroupId() == MainActivity.DrawerLabel.GROUPS.getPosition()) {
			switch (item.getOrder()) {
			case 1: // Edit
				editGroup(groupAdpt.getItem(position));
				handled = true;
				break;
			case 2: // Delete
				Group group = groupAdpt.getItem(position);
				deleteGroup(group.getID());
				handled = true;
				break;
			default:
				Log.wtf(TAG, "Invalid context menu option selected");
				break;
			}
		} else {
			Log.wtf(TAG, "What happened here?");
		}

		return handled;
	}

	private void editGroup(Group group){
		Intent i = new Intent(getActivity(), EditGroupActivity.class);
		i.putExtra(Keys.Group.PARCEL, group);
		startActivityForResult(i, 0);
	}

	private void viewGroup(Group group) {
		Intent i = new Intent(getActivity(), ViewGroupActivity.class);
		i.putExtra(Keys.Group.PARCEL, group);
		startActivityForResult(i, ViewGroupActivity.REQUEST_CODE);
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
			startActivityForResult(i, EditGroupActivity.REQUEST_CODE);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == EditGroupActivity.REQUEST_CODE) {
				Group g = data.getParcelableExtra(Keys.Group.PARCEL);
//				groups.add(g);
				GroupCreateTask creator = new GroupCreateTask(this);
				creator.createGroup(g);
			} else if (requestCode == ViewGroupActivity.REQUEST_CODE) {
//				fetchGroups();
			} else if(requestCode == 0){
				Group group = data.getParcelableExtra(Keys.Group.PARCEL);
				GroupUpdaterTask updater = new GroupUpdaterTask();
				updater.updateGroup(group);
//				if(!groups.contains(group)){
//					for(int i=0;i<groups.size();i++){
//						if(groups.get(i).getID().equals(group.getID())){
//							groups.set(i, group);
//							break;
//						}
//					}
//					fetchGroups();
//				}
			}
			fetchGroups();
//			groupAdpt.notifyDataSetChanged();
			return;
		}
	}

	public void fetchGroups() {
		if (ConnectivityUtils.isConnected(getActivity()) && isAdded()) {

			fetcher = new GroupFetcherTask(this);
			fetcher.execute(SessionManager.getUserID()); // calls processFinish()
		}
	}

	public void deleteGroup(String groupID) {
		new AsyncGroupDeleteTask(new AsyncResponse<Boolean>() {
			@Override
			public void processFinish(Boolean result) {
				fetchGroups();
			}
		}).execute(groupID);
	}

	@Override
	public void processFinish(List<Group> result) {
		groups.clear();
		groupAdpt.clear();
		groups.addAll(result);
		Collections.sort(groups);
		groupAdpt.notifyDataSetChanged();
	}

	public void notifyAdapter() {
		groupAdpt.notifyDataSetChanged();
	}

	@Override
	public void refresh() {
		processFinish(groups);
	}

}
