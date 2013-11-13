package com.droidrage.meetingninja;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import objects.Meeting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ListView;
import android.widget.TextView;

public class ViewProfileFragment extends Fragment implements AsyncResponse<List<Meeting>> {

	private ListView meetingList;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private SessionManager session;
	private MeetingItemAdapter adpt;
	private TextView profileName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_view_profile,
				container, false);
		session = new SessionManager(getActivity().getApplicationContext());
		
		profileName = (TextView) v.findViewById(R.id.profile_name);
		profileName.setText(session.getUserDetails().get(SessionManager.USER));
		
		meetingList = (ListView) v.findViewById(R.id.profile_meetingList);
		adpt = new MeetingItemAdapter(getActivity(), R.layout.meeting_item, meetings);
		meetingList.setAdapter(adpt);
		
		MeetingFetcherTask fetcher = new MeetingFetcherTask(this);
		fetcher.execute(session.getUserDetails().get(SessionManager.USER));
		
		return v;

	}

	@Override
	public void processFinish(List<Meeting> result) {
		adpt.clear();
		adpt.addAll(result);
		
	}
}