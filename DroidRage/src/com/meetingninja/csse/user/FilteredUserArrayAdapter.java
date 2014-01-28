package com.meetingninja.csse.user;

import java.util.ArrayList;

import objects.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.tokenautocomplete.FilteredArrayAdapter;

public class FilteredUserArrayAdapter extends FilteredArrayAdapter<User> {

	private int mLayoutId;

	private final LayoutInflater mLayoutInflater;

	public FilteredUserArrayAdapter(Context context, int resourceId,
			ArrayList<User> allUsers) {
		super(context, resourceId, new ArrayList<User>(allUsers));
		mLayoutInflater = LayoutInflater.from(context);
		mLayoutId = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = newView();
		}
		bindView(convertView, getItem(position));
		return convertView;
	}

	private void bindView(View view, User entry) {
		TextView display = (TextView) view.findViewById(android.R.id.title);
		SmartImageView imageView = (SmartImageView) view
				.findViewById(android.R.id.icon);
		display.setText(entry.getDisplayName());
		display.setVisibility(View.VISIBLE);
		imageView.setVisibility(View.VISIBLE);
		TextView destination = (TextView) view.findViewById(android.R.id.text1);
		destination.setText(entry.getEmail());

		// TODO : Get url's for user images
		// String imgURL = u.getImage();
		// img.setImageUrl(imgURL);

	}

	private View newView() {
		return mLayoutInflater.inflate(mLayoutId, null);
	}

	@Override
	protected boolean keepObject(User user, String mask) {
		mask = mask.toLowerCase();
		return user.getDisplayName().toLowerCase().startsWith(mask)
				|| user.getEmail().toLowerCase().startsWith(mask);
	}

}