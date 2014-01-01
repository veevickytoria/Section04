package com.android.meetingninja.user;

import java.util.ArrayList;
import java.util.List;

import objects.Meeting;
import objects.User;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.meetingninja.R;
import com.android.meetingninja.database.AsyncResponse;
import com.android.meetingninja.meetings.MeetingFetcherTask;
import com.android.meetingninja.meetings.MeetingItemAdapter;
import com.loopj.android.image.SmartImageView;

public class ProfileFragment extends Fragment implements
		AsyncResponse<List<Meeting>> {

	private ListView meetingList;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private SessionManager session;
	private MeetingItemAdapter adpt;
	private User user;

	private TextView mTitleCompany, mName, mPhone, mEmail;
	private SmartImageView mUserImage;

	private RetUserObj infoFetcher = null;

	// private GetProfBitmap bitmapFetcher = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_view_profile, container,
				false);
		setupViews(v);
		session = SessionManager.newInstance(getActivity().getApplicationContext());

		// user = new User(session.getUserDetails().get(session.USERID));
		infoFetcher = new RetUserObj();
		infoFetcher.execute(session.getUserDetails().get(SessionManager.USERID));

		meetingList = (ListView) v.findViewById(R.id.profile_meetingList);
		adpt = new MeetingItemAdapter(getActivity(), R.layout.meeting_item,
				meetings);
		meetingList.setAdapter(adpt);

		MeetingFetcherTask fetcher = new MeetingFetcherTask(this);
		fetcher.execute(session.getUserDetails().get(SessionManager.USER));

		return v;

	}

	private void setupViews(View v) {
		mName = (TextView) v.findViewById(R.id.profile_name);
		mTitleCompany = (TextView) v.findViewById(R.id.profile_title_company);
		mPhone = (TextView) v.findViewById(R.id.profile_phone);
		mEmail = (TextView) v.findViewById(R.id.profile_email);

		mUserImage = (SmartImageView) v.findViewById(R.id.view_prof_pic);

	}

	@Override
	public void processFinish(List<Meeting> result) {
		adpt.clear();
		adpt.addAll(result);

	}

	private void setUser(User user) {
		this.user = user;
		if (user != null) {
			mName.setText(user.getDisplayName());
			mTitleCompany.setText(String.format(getString(R.id.profile_title_company), user.getTitle(), user.getCompany()));
			mPhone.setText(user.getPhone());
			mEmail.setText(user.getEmail());			
		}
//		Bitmap pic = BitmapFactory.decodeResource(getResources(),
//				R.drawable.ic_contact_picture);
//		qcb.setImageBitmap(pic);
		// bitmapFetcher.execute("http://www.tdnforums.com/uploads/profile/photo-27119.jpg?_r=0");
		mUserImage.setImageUrl("http://www.tdnforums.com/uploads/profile/photo-27119.jpg?_r=0");

	}

	final class RetUserObj implements AsyncResponse<User> {

		private UserInfoFetcher infoFetcher;

		public RetUserObj() {
			infoFetcher = new UserInfoFetcher(this);
		}

		public void execute(String userID) {
			infoFetcher.execute(userID);
		}

		@Override
		public void processFinish(User result) {
			setUser(result);
		}
	}

}
