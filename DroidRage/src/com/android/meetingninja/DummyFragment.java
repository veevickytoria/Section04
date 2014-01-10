package com.android.meetingninja;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DummyFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_dummy, container, false);
		TextView txt = (TextView) v.findViewById(R.id.TextView1);

		Bundle args = getArguments();
		if (args != null && !args.isEmpty()) {
			txt.setText(args.getString("Content"));
		}

		return v;

	}
}
