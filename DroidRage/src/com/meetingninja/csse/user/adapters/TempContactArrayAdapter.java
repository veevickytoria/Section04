package com.meetingninja.csse.user.adapters;

import java.util.List;

import objects.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.meetingninja.csse.R;
import com.meetingninja.csse.database.AsyncResponse;

/*
 this isn't being used but it would be used for a button in a listview and checking what even should be done when the buttons
 require different events
 */
public class TempContactArrayAdapter extends ArrayAdapter<User> {

	private int mLayoutId;
	private final LayoutInflater mLayoutInflater;
	private List<User> contacts;
	private addContactObj fetcher;

	public TempContactArrayAdapter(Context context, int resourceId,
			List<User> users) {
		super(context, resourceId, users);
		this.contacts = users;
		mLayoutInflater = LayoutInflater.from(context);
		mLayoutId = resourceId;
	}

	// class for caching the views in a row
	private class ViewHolder {
		SmartImageView photo;
		TextView name, email;
		Button button;
	}

	ViewHolder viewHolder;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(mLayoutId, parent, false);
			viewHolder = new ViewHolder();

			// cache the views
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.user_list_name);
			viewHolder.email = (TextView) convertView
					.findViewById(R.id.user_list_email);
			viewHolder.photo = (SmartImageView) convertView
					.findViewById(R.id.user_list_image);
			viewHolder.button = (Button) convertView
					.findViewById(R.id.contacts_add_contact_button);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("user being added"
						+ contacts.get(position).getDisplayName());
				addContact(contacts.get(position).getID());

			}
		});

		User user = contacts.get(position);
		viewHolder.name.setText(user.getDisplayName());
		viewHolder.email.setText(user.getEmail());
		return convertView;
	}

	private void addContact(String contactID) {
		fetcher = new addContactObj();
		fetcher.execute(contactID);
	}

	final class addContactObj implements AsyncResponse<User> {

		public addContactObj() {
		}

		public void execute(String contactID) {
			// AddContactTask adder = new AddContactTask();

			// adder.addContact(contactID);

		}

		@Override
		public void processFinish(User result) {
			notifyDataSetChanged();
		}
	}

}
