package com.meetingninja.csse.user;

import java.util.List;

import objects.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.meetingninja.csse.R;
import com.tokenautocomplete.FilteredArrayAdapter;

public class AutoCompleteAdapter extends FilteredArrayAdapter<User> {

	private final LayoutInflater mLayoutInflater;

	public AutoCompleteAdapter(Context context, List<User> users) {
		super(context, R.layout.chips_recipient_dropdown_item, users);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User u = getItem(position);
		if (convertView == null || convertView instanceof TextView) {
			convertView = mLayoutInflater.inflate(
					R.layout.chips_recipient_dropdown_item, parent, false);
		}

//		if(u == null){
//			((TextView) convertView.findViewById(android.R.id.title)).setText((position == 0 ? "Contacts" : "Other"));
//			((TextView) convertView.findViewById(android.R.id.text1)).setText("");
//		}else{
			((TextView) convertView.findViewById(android.R.id.title)).setText(u
					.getDisplayName());
			((TextView) convertView.findViewById(android.R.id.text1)).setText(u
					.getEmail());

			// Get the URL for user images
			SmartImageView img = (SmartImageView) convertView
					.findViewById(android.R.id.icon);
//		}
		return convertView;
	}

	@Override
	protected boolean keepObject(User user, String mask) {
		mask = mask.toLowerCase();
//		Log.i(mask, user.toString());
		return user.getDisplayName().toLowerCase().startsWith(mask)
				|| user.getEmail().toLowerCase().startsWith(mask);
	}

}