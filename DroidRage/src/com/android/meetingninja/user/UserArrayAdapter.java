package com.android.meetingninja.user;

import java.util.List;

import com.android.meetingninja.R;
import com.android.meetingninja.R.id;
import com.android.meetingninja.R.layout;
import com.loopj.android.image.SmartImageView;

import objects.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserArrayAdapter extends ArrayAdapter<User> {
	private final Context context;
	private final List<User> users;

	public UserArrayAdapter(Context context, int resource, List<User> users) {
		super(context, resource, users);
		this.context = context;
		this.users = users;
	}

	// class for caching the views in a row
	private class ViewHolder {
		SmartImageView photo;
		TextView name, email;
	}

	ViewHolder viewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.line_item_user, parent,
					false);
			viewHolder = new ViewHolder();
			
			// cache the views
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.txtUsername);
			viewHolder.email = (TextView) convertView
					.findViewById(R.id.txtEmail);
			viewHolder.photo = (SmartImageView) convertView
					.findViewById(R.id.user_list_item_image);

			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		User user = users.get(position);
		viewHolder.name.setText(user.getDisplayName());
		viewHolder.email.setText(user.getEmail());
		return convertView;
	}

}