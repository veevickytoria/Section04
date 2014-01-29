package com.meetingninja.csse;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

public class ContactsCompletionView extends TokenCompleteTextView {
	public ContactsCompletionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View getViewForObject(Object object) {
		SimpleUser p = (SimpleUser) object;

		LayoutInflater l = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout) l.inflate(R.layout.contact_token,
				(ViewGroup) ContactsCompletionView.this.getParent(), false);
		((TextView) view.findViewById(R.id.token_name)).setText(p.getEmail());

		return view;
	}

	@Override
	protected Object defaultObject(String completionText) {
		// Stupid simple example of guessing if we have an email or not
		int index = completionText.indexOf('@');
		if (index == -1) {
			return new SimpleUser(completionText, completionText.replace(" ",
					"") + "@example.com");
		} else {
			return new SimpleUser(completionText.substring(0, index),
					completionText);
		}
	}
}