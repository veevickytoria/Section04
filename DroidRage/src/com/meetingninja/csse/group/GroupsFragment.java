package com.meetingninja.csse.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.meetingninja.csse.ApplicationController;
import com.meetingninja.csse.R;
import com.meetingninja.csse.SessionManager;
import com.meetingninja.csse.database.AsyncResponse;
import com.meetingninja.csse.database.GroupDatabaseAdapter;
import com.meetingninja.csse.database.Keys;
import com.meetingninja.csse.extras.Connectivity;

public class GroupsFragment extends Fragment implements
		AsyncResponse<List<Group>> {
	private SessionManager session;
	private ListView groupsList;
	private static List<Group> groups = new ArrayList<Group>();;
	private GroupItemAdapter groupAdpt;
	private GroupFetcherTask fetcher;

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

		if (Connectivity.isConnected(getActivity()) && isAdded()) {
			fetchGroups();
		}

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
		inflater.inflate(R.menu.new_and_refresh_menu, menu);
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

	public void fetchGroups() {
		fetcher = new GroupFetcherTask(this);
		fetcher.execute(session.getUserID()); // calls processFinish()
	}

	private void editGroup(Group group) {
		Intent i = new Intent(getActivity(), EditGroupActivity.class);
		i.putExtra(Keys.Group.PARCEL, group);
		startActivityForResult(i, 8);
	}

	public void deleteGroup(String groupID) {
		String url = GroupDatabaseAdapter.getBaseUri().appendPath(groupID)
				.build().toString();
		StringRequest dr = new StringRequest(
				com.android.volley.Request.Method.DELETE, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// response
						Toast.makeText(getActivity(), response,
								Toast.LENGTH_SHORT).show();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// error.

					}

				});
		ApplicationController.getInstance().addToRequestQueue(dr);
	}

	@Override
	public void processFinish(List<Group> result) {
		groups.clear();
		groupAdpt.clear();
		Collections.sort(result, new Comparator<Group>() {
			@Override
			public int compare(Group lhs, Group rhs) {
				return lhs.compareTo(lhs);
			}
		});

		groups.addAll(result);

		groupAdpt.notifyDataSetChanged();
	}

}
