package com.meetingninja.csse.extras;

import java.io.Serializable;
import java.util.ArrayList;

import objects.SerializableUser;
import objects.User;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.tokenautocomplete.TokenCompleteTextView;

public class UsersCompletionView extends TokenCompleteTextView {

	private final String TAG = UsersCompletionView.class.getSimpleName();

	public UsersCompletionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addObject(Object object) {
		super.addObject(object);
		String className = object.getClass().getSimpleName();
		Log.d(TAG, "Added a " + className);
	}

	@Override
	protected View getViewForObject(Object object) {
		String className = object.getClass().getSimpleName();
		Log.d(TAG, "Get View for " + className);

		SerializableUser su = null;
		if (object instanceof User)
			su = ((User) object).toSimpleUser();
		else
			su = (SerializableUser) object;

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.contact_token,
				(ViewGroup) UsersCompletionView.this.getParent(), false);
		((TextView) view.findViewById(R.id.token_name)).setText(su
				.getDisplayName());

		return view;
	}

	@Override
	protected Object defaultObject(String completionText) {
		// Stupid simple example of guessing if we have an email or not
		int index = completionText.indexOf('@');
		SerializableUser u = new SerializableUser();
		if (index == -1) {
			u.setDisplayName(completionText);
			u.setEmail(completionText.replace(" ", "") + "@meetingninja.com");
		} else {
			u.setDisplayName(completionText.substring(0, index));
			u.setEmail(completionText);
		}
		return u;
	}

	@Override
	protected ArrayList<Object> convertSerializableArrayToObjectArray(
			ArrayList<Serializable> sers) {
		ArrayList<Object> objs = new ArrayList<Object>();

		for (Serializable s : sers) {
			String className = s.getClass().getSimpleName();
			Log.d(TAG, "Converting a " + className);

			// Log.d("get", ((SerializableUser) s).getDisplayName());
			objs.add(s);
		}

		return objs;
	}

	@Override
	protected ArrayList<Serializable> getSerializableObjects() {
		ArrayList<Serializable> s = new ArrayList<Serializable>();

		for (final Object obj : getObjects()) {
			String className = obj.getClass().getSimpleName();
			Log.d(TAG, "Serializing a " + className);

			if (obj instanceof User) {
				SerializableUser su = ((User) obj).toSimpleUser();
				// Log.d("build", su.getDisplayName());
				s.add(su);
			} else {
				s.add((SerializableUser) obj);
			}
		}
		return s;
	}
}