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
package com.meetingninja.csse.user;

import java.util.List;

import objects.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.meetingninja.csse.R;

public class UserArrayAdapter extends ArrayAdapter<User> {
	private final Context mContext;
	private final List<User> users;

	public UserArrayAdapter(Context context, int resource, List<User> users) {
		super(context, resource, users);
		this.mContext = context;
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
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_user, parent,
					false);
			viewHolder = new ViewHolder();

			// cache the views
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.user_list_name);
			viewHolder.email = (TextView) convertView
					.findViewById(R.id.user_list_email);
			viewHolder.photo = (SmartImageView) convertView
					.findViewById(R.id.user_list_image);

			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		User user = users.get(position);
		viewHolder.name.setText(user.getDisplayName());
		viewHolder.email.setText(user.getEmail());
		return convertView;
	}

}
