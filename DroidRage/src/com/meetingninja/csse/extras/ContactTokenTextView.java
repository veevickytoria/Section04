package com.meetingninja.csse.extras;

import java.io.Serializable;
import java.util.ArrayList;

import objects.SerializableUser;
import objects.User;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meetingninja.csse.R;
import com.tokenautocomplete.TokenCompleteTextView;

public class ContactTokenTextView extends TokenCompleteTextView {

	private static final String TAG = "Test";
	private final LayoutInflater mLayoutInflater;

	public ContactTokenTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLayoutInflater = LayoutInflater.from(context);
		allowDuplicates(false);
		setDeletionStyle(TokenCompleteTextView.TokenDeleteStyle.PartialCompletion);
	}

	@Override
	protected View getViewForObject(Object object) {
		// String className = object.getClass().getSimpleName();
		// Log.d(TAG, "Get View for " + className);

		SerializableUser su = null;
		if (object instanceof SerializableUser)
			su = (SerializableUser) object;
		else if (object instanceof User)
			su = new SerializableUser((User) object);

		LinearLayout view = (LinearLayout) mLayoutInflater.inflate(
				R.layout.contact_token,
				(ViewGroup) ContactTokenTextView.this.getParent(), false);

		TextView name = (TextView) view.findViewById(R.id.token_name);
		if (!TextUtils.equals(su.getID(), ""))
			name.setText(su.getDisplayName());
		else
			name.setText(su.getEmail());

		return view;
	}

	@Override
	protected Object defaultObject(String completionText) {
		// Stupid simple example of guessing if we have an email or not
		int index = completionText.indexOf('@');
		User u = new User();
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
			objs.add((SerializableUser) s);
		}

		return objs;
	}

	@Override
	protected ArrayList<Serializable> getSerializableObjects() {
		ArrayList<Serializable> s = new ArrayList<Serializable>();

		for (final Object obj : getObjects()) {
			String className = obj.getClass().getSimpleName();
			Log.d(TAG, "Serializing a " + className);

			SerializableUser su = null;
			if (obj instanceof SerializableUser)
				su = (SerializableUser) obj;
			else
				su = new SerializableUser((User) obj);
			s.add(su);
		}
		return s;
	}
}