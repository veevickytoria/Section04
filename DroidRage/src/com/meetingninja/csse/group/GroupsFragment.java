package com.meetingninja.csse.group;

import java.util.ArrayList;
import java.util.List;

import objects.Group;
import objects.User;
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
import com.meetingninja.csse.SearchableUserFragment;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.Keys;

public class GroupsFragment extends Fragment {
	private SessionManager session;
	private ListView groupsList;
	private static List<Group> groups = new ArrayList<Group>();;
	private GroupItemAdapter groupAdpt;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_groups, container, false);
		setHasOptionsMenu(true);

		session = SessionManager.getInstance();
		groupsList = (ListView) v.findViewById(R.id.groupsList);
		groupAdpt = new GroupItemAdapter(getActivity(),
				R.layout.list_item_group, groups);
		groupsList.setAdapter(groupAdpt);

		groupAdpt.notifyDataSetChanged();
		groupsList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parentAdapter,
							View v, int position, long id) {
						Group clicked = groupAdpt.getItem(position);
						editGroup(clicked);
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
			// TODO: refresh groups db call
			return true;
		case R.id.action_new:
			Intent i = new Intent(getActivity(), EditGroupActivity.class);
			Group g = new Group("1234", "Testing groups edit page");
			User u1 = new User();
			u1.setEmail("group1@email.com");
			u1.setDisplayName("Grouper 1");
			u1.setID("5659");
			User u2 = new User();
			u2.setEmail("group2@email.com");
			u2.setDisplayName("Grouper 2");
			u2.setID("5660");
			User u3 = new User();
			u3.setEmail("group3@email.com");
			u3.setDisplayName("Grouper 3");
			u3.setID("5661");
			g.addMember(u1);
			g.addMember(u2);
			g.addMember(u3);
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
			Group g = data.getParcelableExtra(Keys.Group.PARCEL);
			if (requestCode == 7) {
				groups.add(g); // TODO: implement DB calls
			} else if (requestCode == 8) {
				// TODO: implement database calls
			}
			groupAdpt.notifyDataSetChanged();

		}
	}

	private void editGroup(Group group) {
		Intent i = new Intent(getActivity(), EditGroupActivity.class);
		i.putExtra(Keys.Group.PARCEL, group);
		startActivityForResult(i, 8);
	}

}
