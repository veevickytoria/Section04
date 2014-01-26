package com.meetingninja.csse.extras;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

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
	public UsersCompletionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addObject(Object object) {
		super.addObject(object);
		Log.d("Add", ((SerializableUser) object).getDisplayName());
	}

	@Override
	protected View getViewForObject(Object object) {
		SerializableUser su = null;
		if (object instanceof User)
			su = new SerializableUser((User) object);
		else
			su = (SerializableUser) object;

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
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
			Log.d("get", ((SerializableUser) s).getDisplayName());
			objs.add(s);
		}

		return objs;
	}

	@Override
	protected ArrayList<Serializable> getSerializableObjects() {
		ArrayList<Serializable> s = new ArrayList<Serializable>();

		for (final Object obj : getObjects()) {
			if (obj instanceof User) {
				SerializableUser su = new SerializableUser((User) obj);
				Log.d("build", su.getDisplayName());
				s.add(su);
			} else {
				s.add((SerializableUser) obj);
			}
		}
		return s;
	}

	private class SerializableUser implements Serializable {
		/**
		 * Generated Serial ID
		 */
		private static final long serialVersionUID = 2730681215135500775L;
		private String userID = "";
		private String name = "";
		private String email = "";

		public SerializableUser(User copy) {
			this.setID(copy.getID());
			this.setDisplayName(copy.getDisplayName());
			this.setEmail(copy.getEmail());
		}

		public SerializableUser() {
			this(new User());
		}

		public void setID(String id) {
			this.userID = id;
		}

		public String getID() {
			return this.userID;
		}

		public void setDisplayName(String displayName) {
			this.name = displayName;
		}

		public String getDisplayName() {
			return this.name;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getEmail() {
			return this.email;
		}

	}
}