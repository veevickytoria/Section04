package com.meetingninja.csse.user.adapters;

import java.util.List;

import objects.Contact;
import objects.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.meetingninja.csse.R;
import com.tokenautocomplete.FilteredArrayAdapter;

public class ContactArrayAdapter extends FilteredArrayAdapter<Contact> {

	private int mLayoutId;
	private final LayoutInflater mLayoutInflater;

	public ContactArrayAdapter(Context context, int resourceId,
			List<Contact> contacts) {
		super(context, resourceId, contacts);
		mLayoutInflater = LayoutInflater.from(context);
		mLayoutId = resourceId;
	}

	// class for caching the views in a row
	private class ViewHolder {
		SmartImageView photo;
		TextView name, email;
	}

	ViewHolder viewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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

			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		Contact contact = getItem(position);
		viewHolder.name.setText(contact.getContact().getDisplayName());
		viewHolder.email.setText(contact.getContact().getEmail());
		return convertView;
	}

	@Override
	protected boolean keepObject(Contact contact, String mask) {
		mask = mask.toLowerCase();
		User user = contact.getContact();
		return user.getDisplayName().toLowerCase().startsWith(mask)
				|| user.getEmail().toLowerCase().startsWith(mask);
	}

}
