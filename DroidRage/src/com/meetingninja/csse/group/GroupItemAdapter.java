package com.meetingninja.csse.group;

import java.util.List;

import com.meetingninja.csse.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import objects.Group;
import objects.Meeting;

public class GroupItemAdapter extends ArrayAdapter<Group>{

	private List<Group> groups;
	private Context context;
	
	public GroupItemAdapter(Context context, int textViewResourceId, List<Group> groups){
		super(context, textViewResourceId, groups);
		this.groups = groups;
		this.context = context;
	}
	// class for cahcing the views in a row
	private class ViewHolder{
		TextView title, numMembers;
	}
	ViewHolder viewHolder;
	/*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.list_item_group, null);
			viewHolder = new ViewHolder();

			viewHolder.title = (TextView) rowView.findViewById(R.id.group_item_title);
			viewHolder.numMembers = (TextView) rowView.findViewById(R.id.group_item_num_members);

			rowView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) rowView.getTag();
		
		// Setup from the meeting_item XML file
		Group group = groups.get(position);

		viewHolder.title.setText(group.getGroupTitle());
		viewHolder.numMembers.setText("Number of members:  " + group.getMembers().size());
		return rowView;
	}
	
	
	
}
