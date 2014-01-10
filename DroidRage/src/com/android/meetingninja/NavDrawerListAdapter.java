package com.android.meetingninja;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class NavDrawerListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;

	public NavDrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.drawer_item, null);

		}
		ImageView imgIcon = (ImageView) v.findViewById(R.id.icon);
		TextView txtTitle = (TextView) v.findViewById(R.id.title);
		TextView txtCount = (TextView) v.findViewById(R.id.counter);

		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		txtTitle.setText(navDrawerItems.get(position).getTitle());

		// displaying count
		// check whether it set visible or not
		if (navDrawerItems.get(position).getCounterVisibility()) {
			txtCount.setText(navDrawerItems.get(position).getCount());
		} else {
			// hide the counter view
			txtCount.setVisibility(View.GONE);
		}
		return v;
	}

	@Override
	public int getCount() {
		return this.navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return this.navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}