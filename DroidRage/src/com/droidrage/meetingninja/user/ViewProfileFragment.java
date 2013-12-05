package com.droidrage.meetingninja.user;

import java.util.ArrayList;
import java.util.List;

import com.droidrage.meetingninja.R;
import com.droidrage.meetingninja.R.id;
import com.droidrage.meetingninja.R.layout;
import com.droidrage.meetingninja.database.AsyncResponse;
import com.droidrage.meetingninja.meetings.MeetingFetcherTask;
import com.droidrage.meetingninja.meetings.MeetingItemAdapter;

import objects.Meeting;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ViewProfileFragment extends Fragment implements
		AsyncResponse<List<Meeting>> {

	private ListView meetingList;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private SessionManager session;
	private MeetingItemAdapter adpt;
	private TextView profileName;

	private TextView company, jobTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_view_profile, container,
				false);

		setupViews(v);

		session = new SessionManager(getActivity().getApplicationContext());

		profileName.setText(session.getUserDetails().get(SessionManager.USER));
		// company.setText(session.getUserDetails().get(SessionManager.company));
		// jobTitle.setText(session.getUserDetails().get(SessionManager.jobTitle));

		meetingList = (ListView) v.findViewById(R.id.profile_meetingList);
		adpt = new MeetingItemAdapter(getActivity(), R.layout.meeting_item,
				meetings);
		meetingList.setAdapter(adpt);

		MeetingFetcherTask fetcher = new MeetingFetcherTask(this);
		fetcher.execute(session.getUserDetails().get(SessionManager.USER));

		return v;

	}

	private void setupViews(View v) {
		profileName = (TextView) v.findViewById(R.id.profile_name);
		company = (TextView) v.findViewById(R.id.company);
		jobTitle = (TextView) v.findViewById(R.id.jobtitle);
	}

	@Override
	public void processFinish(List<Meeting> result) {
		adpt.clear();
		adpt.addAll(result);

	}
}